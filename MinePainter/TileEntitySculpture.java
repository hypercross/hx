package hx.MinePainter;

import java.util.Arrays;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class TileEntitySculpture extends TileEntity implements IBlockAccess{
	byte[] data = new byte[64];
	
	private int biasX,biasY,biasZ;
	
	public TileEntitySculpture()
	{
		Arrays.fill(data,(byte)-1);
	}
	
	private boolean invalid(int x,int y,int z)
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
		byte strip = data[x*8+y];
		
		return ((strip >> z) & 1) == 1;
	}
	
	public void set (int x,int y,int z)
	{
		x%=8;
		y%=8;
		byte strip = data[x*8+y];
		
		strip |= (1 << z);
		data[x * 8 + y] = strip;
	}
	
	public void del (int x,int y,int z)
	{
		x%=8;
		y%=8;
		byte strip = data[x*8+y];
		
		strip ^= (1 << z);
		data[x * 8 + y] = strip;
	}
	
	//TileEntity util
	
	@Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setByteArray("sculpture", data);
    }
	
	@Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.readFromNBT(par1NBTTagCompound);
		data = par1NBTTagCompound.getByteArray("sculpture");
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

	public void bias(int x,int y,int z)
	{
		biasX = x;
		biasY = y;
		biasZ = z;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockOpaqueCube(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				   var2 - this.yCoord + biasY,
				   var3 - this.zCoord + biasZ);
	}
	
	@Override
	public int getBlockId(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				   var2 - this.yCoord + biasY,
				   var3 - this.zCoord + biasZ) ? Block.cobblestone.blockID : 0;
	}

	@Override
	public TileEntity getBlockTileEntity(int var1, int var2, int var3) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
		return worldObj.getLightBrightnessForSkyBlocks(var1, var2, var3, var4);
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
		return 0;
	}

	@Override
	public Material getBlockMaterial(int var1, int var2, int var3) {
		return Material.rock;
	}

	@Override
	public boolean isBlockNormalCube(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				   var2 - this.yCoord + biasY,
				   var3 - this.zCoord + biasZ);
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
	public boolean isBlockProvidingPowerTo(int var1, int var2, int var3,
			int var4) {
		return false;
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
		return new int[]{hitX, hitY, hitZ};
	}
	
	private boolean cross(float f,float g,float h, Vec3 st, Vec3 dist)
	{
		Vec3 normal = dist.crossProduct(Vec3.createVectorHelper(f + 1/16f, g + 1/16f, h + 1/16f).subtract(st))	
						.crossProduct(dist).normalize();
				
		
		boolean hasPos = false;
		boolean hasNeg = false;		
		for(int i = 0; i < 8; i ++)
		{
			Vec3 vert = Vec3.createVectorHelper(f + (i&1)/8f , g + ((i&2)>>1)/8f, h + ((i&4)>>2)/8f);
			vert = vert.subtract(st);
			
			if(vert.dotProduct(normal)>0) hasPos = true;
			else hasNeg = true;
			
			if(hasPos && hasNeg)return true;
		}

		return false;
	}
}
