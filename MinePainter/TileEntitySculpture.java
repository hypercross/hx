package hx.MinePainter;

import hx.utils.Debug;

import java.util.Arrays;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class TileEntitySculpture extends TileEntity implements IBlockAccess{

	public static TileEntitySculpture full = new TileEntitySculpture();

	public byte[] data = new byte[64];
	public int blockId;

	//hinge
	public byte hinge;
	
	//rendering
	public int biasX,biasY,biasZ;

	public boolean needUpdate = true;

	public int displayList = -1;
	public double tessellationX,tessellationY,tessellationZ;
	public boolean tessellationTranslated = false;

	public int light;

	public TileEntitySculpture()
	{
		if(BlockSculpture.createEmpty)clear();
		else fill();
	}

	public boolean invalid(int x,int y,int z)
	{
		if(x>=8)return true;
		if(x<0)return true;
		if(y>=8)return true;
		if(y<0)return true;
		if(z>=8)return true;
		if(z<0)return true;
		return false;
	}

	public boolean get(int x,int y,int z)
	{
		if(invalid(x,y,z))return false;
		byte strip = data[x*8+z];

		return ((strip >> y) & 1) == 1;
	}

	public void set (int x,int y,int z)
	{
		x = normalize(x);
		y = normalize(y);
		z = normalize(z);
		byte strip = data[x*8+z];

		strip |= (1 << y);
		data[x * 8 + z] = strip;

		needUpdate = true;
	}

	private int normalize(int x)
	{
		while(x<0)x+=8;
		return x % 8;
	}

	public void del (int x,int y,int z)
	{
		x%=8;
		y%=8;
		byte strip = data[x*8+z];

		strip &= (1 << y) ^ -1;
		data[x * 8 + z] = strip;

		needUpdate = true;
	}

	public void toggle(int x,int y,int z)
	{
		if(get(x,y,z))del(x,y,z);
		else set(x,y,z);
	}

	public void updateEntity()
	{
		if(needUpdate)
		{
			if(this.worldObj.isRemote)
			{
				needUpdate = BlockSculptureRenderer.instance.updateDisplayList(this, 
						new RenderBlocks(new ChunkCache(this.worldObj, 
								this.xCoord - 2, 
								this.yCoord - 2,
								this.zCoord - 2,
								this.xCoord + 2, 
								this.yCoord + 2,
								this.zCoord + 2, 1)), 
								this.getBlockType());
				
				if(!needUpdate)
					{
						worldObj.markBlockForRenderUpdate(this.xCoord,this.yCoord,this.zCoord);
					}
				
			}
			else{
				this.needUpdate = false;
				worldObj.markBlockForUpdate(this.xCoord,this.yCoord,this.zCoord);
			}
		}
	}
	
	public void invalidate()
	{
		onChunkUnload();
	}
	
	public void onChunkUnload()
	{
		if(this.worldObj.isRemote && this.displayList >= 0)
		{
			GLAllocation.deleteDisplayLists(this.displayList);
			this.displayList = -1;
		}
	}

	//TileEntity util

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setByteArray("sculpture", data);
		par1NBTTagCompound.setInteger("meta", blockId);
		par1NBTTagCompound.setByte("hinge", hinge);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		data = par1NBTTagCompound.getByteArray("sculpture");
		blockId = par1NBTTagCompound.getInteger("meta");
		hinge = par1NBTTagCompound.getByte("hinge");
		needUpdate = true;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		readFromNBT(pkt.customParam1);
	}

	//IBlockAccess
	//
	//
	//
	//
	//

	private int[] toLocal(int x,int y,int z)
	{
		return new int[]{ x - this.xCoord + biasX,
				y - this.yCoord + biasY,
				z - this.zCoord + biasZ};
	}

	private boolean invalid(int[] coord)
	{
		return invalid(coord[0],coord[1],coord[2]);
	}

	public void bias(int x,int y,int z)
	{
		biasX = x;
		biasY = y;
		biasZ = z;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockOpaqueCube(int var1, int var2, int var3) {
		int[] local = toLocal(var1,var2,var3);
		if(!invalid(local))
			return get(local[0],local[1],local[2]);
		
		return false;
	}

	@Override
	public int getBlockId(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				var2 - this.yCoord + biasY,
				var3 - this.zCoord + biasZ) ? this.blockId : 0;
	}

	@Override
	public TileEntity getBlockTileEntity(int var1, int var2, int var3) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
		//		int[] local = toLocal(var1,var2,var3);
		//		if(!invalid(local))
		//			return worldObj.getLightBrightnessForSkyBlocks(var1, var2, var3, var4);
		//		
		return worldObj.getLightBrightnessForSkyBlocks(this.xCoord,this.yCoord,this.zCoord,var4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getBrightness(int var1, int var2, int var3, int var4) {
		return worldObj.getBrightness(var1, var2, var3, var4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getLightBrightness(int var1, int var2, int var3) {
		return worldObj.getLightBrightness(var1, var2, var3);
	}

	@Override
	public int getBlockMetadata(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				var2 - this.yCoord + biasY,
				var3 - this.zCoord + biasZ) ? this.getBlockMetadata() : 0;
	}

	@Override
	public Material getBlockMaterial(int var1, int var2, int var3) {
		return Material.rock;
	}

	@Override
	public boolean isBlockNormalCube(int var1, int var2, int var3) {

		int x = var1 - this.xCoord + biasX;
		int y = var2 - this.yCoord + biasY;
		int z = var3 - this.zCoord + biasZ;

		if(get(x,y,z))return true;
		if(invalid(x,y,z))return false;
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isAirBlock(int var1, int var2, int var3) {
		return !get(var1 - this.xCoord + biasX,
				var2 - this.yCoord + biasY,
				var3 - this.zCoord + biasZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BiomeGenBase getBiomeGenForCoords(int var1, int var2) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight() {
		return 8;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean extendedLevelsInChunkCache() {
		return worldObj.extendedLevelsInChunkCache();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesBlockHaveSolidTopSurface(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				var2 - this.yCoord + biasY,
				var3 - this.zCoord + biasZ);
	}

	@Override
	public Vec3Pool getWorldVec3Pool() {
		return this.worldObj.getWorldVec3Pool();
	}

	@Override
	public int isBlockProvidingPowerTo(int var1, int var2, int var3,
			int var4) {
		return 0;
	}

	public int[] rayTrace(Vec3 start, Vec3 dist)
	{
		int hitX = -1;
		int hitY = -1;
		int hitZ = -1;

		for(int x=0;x<8;x++)
			for(int y=0;y<8;y++)
				for(int z=0;z<8;z++)
				{
					if(!get(x,y,z))continue;

					if(hitX == -1);
					else if( (x!= hitX) && (x>hitX) == (dist.xCoord>0) )
						continue;
					else if( (y!= hitY) && (y>hitY) == (dist.yCoord>0) )
						continue;
					else if( (z!= hitZ) && (z>hitZ) == (dist.zCoord>0) )
						continue;

					if(!cross(xCoord + x/8f, yCoord + y/8f, zCoord + z/8f, start, dist))continue;

					hitX = x;
					hitY = y;
					hitZ = z;
				}

		if(hitX == -1)return null;
		int face = face(start,dist,new int[]{hitX, hitY, hitZ});
		return new int[]{hitX, hitY, hitZ, face};
	}
	
	public double[] hitVec(Vec3 start, Vec3 dist, int[] pos)
	{
		Vec3 end = dist.addVector(start.xCoord,start.yCoord,start.zCoord);
		double[] hits = new double[3];

		double dx = xCoord + pos[0]/8f + (dist.xCoord > 0 ? 0 : .125f);   
		double dy = yCoord + pos[1]/8f + (dist.yCoord > 0 ? 0 : .125f);
		double dz = zCoord + pos[2]/8f + (dist.zCoord > 0 ? 0 : .125f);

		int face = -1;
		double len = -1;

		Vec3 to_face = start.getIntermediateWithXValue(end, dx); 
		if(to_face != null && to_face.squareDistanceTo(start) > len)
		{
			face = dist.xCoord > 0 ? 4 : 5;
			len = to_face.squareDistanceTo(start);
			hits[0] = to_face.xCoord;
			hits[1] = to_face.yCoord;
			hits[2] = to_face.zCoord;
		}

		to_face = start.getIntermediateWithYValue(end, dy);
		if(to_face != null && to_face.squareDistanceTo(start) > len)
		{
			face = dist.yCoord > 0 ? 0 : 1;
			len = to_face.squareDistanceTo(start);
			hits[0] = to_face.xCoord;
			hits[1] = to_face.yCoord;
			hits[2] = to_face.zCoord;
		}

		to_face = start.getIntermediateWithZValue(end, dz);
		if(to_face != null && to_face.squareDistanceTo(start) > len)
		{
			face = dist.zCoord > 0 ? 2 : 3;
			len = to_face.squareDistanceTo(start);
			hits[0] = to_face.xCoord;
			hits[1] = to_face.yCoord;
			hits[2] = to_face.zCoord;
		}			

		return hits;		
	}

	public int face(Vec3 start, Vec3 dist, int[] pos)
	{
		Vec3 end = dist.addVector(start.xCoord,start.yCoord,start.zCoord);

		double dx = xCoord + pos[0]/8f + (dist.xCoord > 0 ? 0 : .125f);   
		double dy = yCoord + pos[1]/8f + (dist.yCoord > 0 ? 0 : .125f);
		double dz = zCoord + pos[2]/8f + (dist.zCoord > 0 ? 0 : .125f);

		int face = -1;
		double len = -1;

		Vec3 to_face = start.getIntermediateWithXValue(end, dx); 
		if(to_face != null && to_face.squareDistanceTo(start) > len)
		{
			face = dist.xCoord > 0 ? 4 : 5;
			len = to_face.squareDistanceTo(start);
		}

		to_face = start.getIntermediateWithYValue(end, dy);
		if(to_face != null && to_face.squareDistanceTo(start) > len)
		{
			face = dist.yCoord > 0 ? 0 : 1;
			len = to_face.squareDistanceTo(start);
		}

		to_face = start.getIntermediateWithZValue(end, dz);
		if(to_face != null && to_face.squareDistanceTo(start) > len)
		{
			face = dist.zCoord > 0 ? 2 : 3;
			len = to_face.squareDistanceTo(start);
		}
		
		return face;
	}
	
	
	private Vec3 crossProduct(Vec3 a, Vec3 b)
	{
		return a.myVec3LocalPool.getVecFromPool(a.yCoord * b.zCoord - a.zCoord * b.yCoord, a.zCoord * b.xCoord - a.xCoord * b.zCoord, a.xCoord * b.yCoord - a.yCoord * b.xCoord);
	}

	private boolean cross(float f,float g,float h, Vec3 st, Vec3 dist)
	{
		Vec3 delta = Vec3.createVectorHelper(f + 1/16f - st.xCoord, 
				g + 1/16f - st.yCoord, h + 1/16f - st.zCoord);
		Vec3 normal = crossProduct(crossProduct(dist, delta),dist);


		boolean hasPos = false;
		boolean hasNeg = false;		
		for(int i = 0; i < 8; i ++)
		{
			Vec3 vert = Vec3.createVectorHelper(f + (i&1)/8f , g + ((i&2)>>1)/8f, h + ((i&4)>>2)/8f);
			vert = vert.addVector(-st.xCoord,-st.yCoord,-st.zCoord);

			if(vert.dotProduct(normal)>0) hasPos = true;
			else hasNeg = true;

			if(hasPos && hasNeg)return true;
		}

		return false;
	}

	public static int[] selectionBox(int[] selectionPoint, int mode, int axis, int[] minmax)
	{
		if(selectionPoint == null)return null;

		int[] box = new int[]{selectionPoint[0],
				selectionPoint[1],
				selectionPoint[2],
				selectionPoint[0]+1,
				selectionPoint[1]+1,
				selectionPoint[2]+1};

		if(mode > 2)
		{
			mode -= 3;
			shift(box,selectionPoint[3]);
			minmax[0]=minmax[1]=minmax[2]=0;
			minmax[3]=minmax[4]=minmax[5]=8;
			shiftMinmax(box,minmax);
		}
		switch(mode)
		{
		case 0:
			return box;
		case 1:
			box[axis]=minmax[axis];
			box[axis+3]=minmax[axis+3];
			return box;
		case 2:
			for(int i =0;i<3;i++)
				if(i!=axis)
				{
					box[i]=minmax[i];
					box[i+3]=minmax[i+3];
				}
			return box;
		}
		return null;
	}

	private static void shiftMinmax(int box[], int[] minmax)
	{
		for(int i =0;i<3;i++)
		{
			if(box[i] < minmax[i])
			{
				minmax[i]-=8;
				minmax[i+3]-=8;
			}
			if(box[i+3] > minmax[i+3])
			{
				minmax[i]+=8;
				minmax[i+3]+=8;
			}
		}

	}

	private static void shift(int[] box, int face)
	{
		if(face != cap(0,face,5))return;
		box[0] += Facing.offsetsXForSide[face];
		box[1] += Facing.offsetsYForSide[face];
		box[2] += Facing.offsetsZForSide[face];
		box[3] += Facing.offsetsXForSide[face];
		box[4] += Facing.offsetsYForSide[face];
		box[5] += Facing.offsetsZForSide[face];
	}

	public static int cap(int a,int b,int c)
	{
		if(a>b)return a;
		if(b>c)return c;
		return b;
	}

	public static int getMode(ItemStack is)
	{
		if(is == null)return -1;
		if(is.itemID == ModMinePainter.instance.item("StoneChisel").item().itemID)return 0;
		if(is.itemID == ModMinePainter.instance.item("IronChisel").item().itemID)return 1;
		if(is.itemID == ModMinePainter.instance.item("DiamondChisel").item().itemID)return 2;
		if(is.itemID == ModMinePainter.instance.item("SculpturePiece").item().itemID)return 3;
		if(is.itemID == ModMinePainter.instance.item("SculptureBar").item().itemID)return 4;
		if(is.itemID == ModMinePainter.instance.item("SculptureCover").item().itemID)return 5;
		return -1;
	}

	public static int getAxis(Vec3 look)
	{
		double[] val = new double[] { Math.abs(look.xCoord),Math.abs(look.yCoord),Math.abs(look.zCoord)};

		for(int i =0;i<3;i++)
		{
			boolean max = true;
			for(int j =0;j<3;j++)
				if(val[j]>val[i])max = false;
			if(max)return i;
		}
		return 0;
	}

	public void updateBounds(Block block)
	{
		int[] minmax = getMinMax();

		if(minmax[0] < minmax[3])
			block.setBlockBounds(minmax[0]/8f, minmax[1]/8f, minmax[2]/8f, minmax[3]/8f, minmax[4]/8f, minmax[5]/8f);
		else block.setBlockBounds(0, 0, 0, 1,1,1);
	}

	public int[] getMinMax()
	{
		int minX,minY,minZ,maxX,maxY,maxZ;
		minX=minY=minZ=8;
		maxX=maxY=maxZ=0;

		for(int _x = 0;_x<8;_x++)
			for(int _y = 0;_y<8;_y++)
				for(int _z = 0;_z<8;_z++)
					if(get(_x,_y,_z))
					{
						minX = Math.min(_x, minX);
						minY = Math.min(_y, minY);
						minZ = Math.min(_z, minZ);

						maxX = Math.max(_x+1, maxX);
						maxY = Math.max(_y+1, maxY);
						maxZ = Math.max(_z+1, maxZ);

					}

		return new int[]{minX,minY,minZ,maxX,maxY,maxZ};
	}

	public void clear() {
		Arrays.fill(data, (byte)0);
	}

	public void fill()
	{
		Arrays.fill(data, (byte)-1);
	}

	public boolean isEmpty() {
		for(int i =0;i<data.length;i++)
			if(data[i] != 0)return false;
		return true;
	}

	public void rotate()
	{
		byte[] newByte = new byte[64];
		for(int i =0;i<8;i++)
			for(int j =0;j<8;j++)
				newByte[i*8 + j] = data[(7-i) + j*8];

		this.data = newByte;
	}
	
	public void rotate_inverse()
	{
		byte[] newByte = new byte[64];
		for(int i =0;i<8;i++)
			for(int j =0;j<8;j++)
				newByte[(7-i) + j*8] = data[i*8 + j];

		this.data = newByte;
	}
	
	public int rotateHinge(int hinge)
	{
		if(hinge == 1)return 4;
		else if(hinge == 4)return 2;
		else if(hinge == 2)return 3;
		else return 1;
	}
	
	private boolean isVacantBlock(World w,int x,int y,int z)
	{
		if(w.isAirBlock(x, y, z))return true;
		return false;
	}
	
	public void rotateAroundHinge(boolean inverse)
	{
		int xOff = 0;
		int zOff = 0;

		int hinge = this.hinge;
		if(inverse)for(int i =0;i<3;i++)hinge = rotateHinge(hinge);
		
		if(hinge == 1)zOff = -1;
		else if(hinge == 2)zOff = 1;
		else if(hinge == 3)xOff = -1;
		else if(hinge == 4)xOff = 1;
		
		if(inverse)
		{
			xOff =-xOff;
			zOff =-zOff;
		}
		
		
		if(xOff != 0 || zOff != 0)
		{
			if(isVacantBlock(this.worldObj,this.xCoord + xOff,this.yCoord,this.zCoord + zOff))
			{
				BlockSculpture.createEmpty = true;
				this.worldObj.setBlock(this.xCoord + xOff,this.yCoord,this.zCoord + zOff,
						this.getBlockType().blockID,this.getBlockMetadata(), 3);
				
				BlockSculpture.dropScrap = false;
				this.worldObj.setBlock(this.xCoord,this.yCoord,this.zCoord,0);
				BlockSculpture.dropScrap = true;
				
				this.validate();
				this.worldObj.setBlockTileEntity(this.xCoord + xOff,this.yCoord,this.zCoord + zOff,this);
				if(inverse)this.rotate_inverse(); else this.rotate();
				this.needUpdate = true;

				if(!inverse)
				this.hinge = (byte) rotateHinge(this.hinge);
				else this.hinge = (byte) hinge;
//				Debug.dafuq(hinge);
			}
		}
	}
}
