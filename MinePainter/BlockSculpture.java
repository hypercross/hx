package hx.MinePainter;

import hx.utils.Debug;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockSculpture extends BlockContainer{

	public boolean onSelect = false;
	public static boolean createEmpty = false;
	public static int renderBlockID = 0;
	public static boolean dropScrap = true;
	public static BlockSculpture instance;

	public BlockSculpture(int id) {
		super(id, Material.rock);
		setUnlocalizedName("blockSculpture");
		setCreativeTab(CreativeTabs.tabDecorations);
		//this.setRequiresSelfNotify();
		this.setHardness(10f);
		instance = this;
	}
	public boolean shouldSideBeRendered(IBlockAccess iba, int par2, int par3, int par4, int par5)
	{
		return !iba.isBlockOpaqueCube(par2, par3, par4);
	}

	public Icon getBlockTextureFromSideAndMetadata(int par1, int par2)
	{
		if(renderBlockID == 0)return Block.stone.getBlockTextureFromSideAndMetadata(par1, par2);
		return Block.blocksList[renderBlockID].getBlockTextureFromSideAndMetadata(par1,par2);
	}

	public static Block getMaterialBlockAt(IBlockAccess w, int x,int y,int z)
	{
		TileEntitySculpture tes = (TileEntitySculpture) w.getBlockTileEntity(x, y,z);
		if(tes == null)return null;
		return Block.blocksList[tes.blockId];
	}


	public float getBlockHardness(World par1World, int par2, int par3, int par4)
	{
		Block b = getMaterialBlockAt(par1World, par2,par3,par4);
		if(b == null)return 0;
		return b.getBlockHardness(par1World, par2, par3, par4);
	}

	//	public float getAmbientOcclusionLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	//    {
	//        return 1.0F;
	//    }

	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		if(onSelect)return;
		TileEntitySculpture tes = (TileEntitySculpture) par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
		if(tes == null)return;
		tes.updateBounds(this);
	}

	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	{
		//setBlockBoundsBasedOnState(world,x,y,z);
		if		(side == ForgeDirection.DOWN)return this.minY <.1f;
		else if (side == ForgeDirection.UP)return this.maxY > .9f;
		else if (side == ForgeDirection.WEST)return this.minX  <.1f;
		else if (side == ForgeDirection.EAST)return this.maxX > .9f;
		else if (side == ForgeDirection.NORTH)return this.minZ  <.1f;
		else if (side == ForgeDirection.SOUTH)return this.maxZ > .9f;

		return false;
	}

	public int idDropped(int par1, Random par2Random, int par3)
	{
		return 0;
	}

	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		if(!dropScrap)return;
		TileEntitySculpture tes = (TileEntitySculpture) par1World.getBlockTileEntity(par2, par3, par4);

		int modCount = 0;

		for(int _x = 0;_x<8;_x++)
			for(int _y = 0;_y<8;_y++)
				for(int _z = 0;_z<8;_z++)
					if(tes.get(_x, _y, _z))modCount++;

		dropAllScrap(par1World,par2,par3,par4,modCount);

		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}

	public int idPicked(World par1World, int par2, int par3, int par4)
	{
		Block b = getMaterialBlockAt(par1World,par2,par3,par4);
		if(b == null)return 0;
		return b.blockID;
	}

	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
	{
		TileEntitySculpture tes = (TileEntitySculpture) par1World.getBlockTileEntity(par2, par3, par4);
		tes.updateBounds(this);		

		float minx = (float) this.minX;
		float maxx = (float)this.maxX;

		float miny = (float)this.minY;
		float maxy = (float)this.maxY;

		float minz = (float)this.minZ;
		float maxz = (float)this.maxZ;

		for(int _x = 0;_x<8;_x++)
			for(int _y = 0;_y<8;_y++)
				for(int _z = 0;_z<8;_z++)
				{
					if(tes.get(_x, _y, _z))
					{
						setBlockBounds( _x/8f, _y/8f,_z/8f, (_x+1)/8f, (_y+1)/8f, (_z+1)/8f);

						super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);

						//						_x = _x/4*4 + 3;
						//						_y = _y/4*4 + 3;
						//						_z = _z/4*4 + 3;
					}
				}
		tes.updateBounds(this);
	}

	public MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 st, Vec3 ed)
	{
		TileEntitySculpture tile = (TileEntitySculpture)w.getBlockTileEntity(x, y, z);

		int[] pos = tile.rayTrace(st, ed.addVector(-st.xCoord, -st.yCoord,-st.zCoord));
		if(pos == null)return null;

		double[] hitVec = tile.hitVec(st, ed.addVector(-st.xCoord, -st.yCoord,-st.zCoord), pos);		
		return new MovingObjectPosition(x,y,z,pos[3], Vec3.createVectorHelper(hitVec[0],hitVec[1],hitVec[2]));
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileEntitySculpture();
	}

	public int getRenderType()
	{
		return ModMinePainter.instance.block("Sculpture").ri();
	}


	public boolean isOpaqueCube()
	{
		return false;
	}

	public boolean renderAsNormalBlock()
	{
		return false;
	}

	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		//        for (int var4 = 0; var4 < 16; ++var4)
		//        {
		//            par3List.add(new ItemStack(this, 1, var4));
		//        }
	}

	private void rotateSculptures(World w, int x,int y,int z, boolean dir)
	{
		TileEntitySculpture tes = (TileEntitySculpture) w.getBlockTileEntity(x, y, z);
		int yTop,yBottom;
		for(yTop = y ; yTop<255;yTop++)
		{
			if(w.getBlockId(x, yTop, z)!= this.blockID)break;
			TileEntitySculpture tesy = (TileEntitySculpture) w.getBlockTileEntity(x, yTop, z);
			if(tes.hinge != tesy.hinge)break;
		}
		for(yBottom = y ; yBottom>=0;yBottom--)
		{
			if(w.getBlockId(x, yBottom, z)!= this.blockID)break;
			TileEntitySculpture tesy = (TileEntitySculpture) w.getBlockTileEntity(x, yBottom, z);
			if(tes.hinge != tesy.hinge)break;
		}

		for(int i = yBottom +1;i<yTop;i++)
		{
			TileEntitySculpture tesy = (TileEntitySculpture) w.getBlockTileEntity(x, i, z);
			if(tesy != null)
				tesy.rotateAroundHinge(dir);
		}
	}

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer player, int par6face, float par7, float par8, float par9)
	{
		if(this.blockID != par1World.getBlockId(par2, par3, par4))
			if(par1World.blockHasTileEntity(par2, par3, par4))return false;
		TileEntitySculpture tes = (TileEntitySculpture) par1World.getBlockTileEntity(par2, par3, par4);

		//hinge
		if(tes != null && tes.getMode(player.getCurrentEquippedItem()) == -1)
		{
			if(     (tes.hinge == 1&& (par6face == 5 || par6face == 3)) ||
					(tes.hinge == 4&& (par6face == 3 || par6face == 4)) ||
					(tes.hinge == 2&& (par6face == 4 || par6face == 2)) ||
					(tes.hinge == 3&& (par6face == 2 || par6face == 5))
					)rotateSculptures(par1World,par2,par3,par4,player.isSneaking());

			else if((tes.hinge == 1&& (par6face == 2 || par6face == 4)) ||
					(tes.hinge == 4&& (par6face == 5 || par6face == 2)) ||
					(tes.hinge == 2&& (par6face == 3 || par6face == 5)) ||
					(tes.hinge == 3&& (par6face == 4 || par6face == 3))
					)rotateSculptures(par1World,par2,par3,par4,!player.isSneaking());
			else return false;

			par1World.playAuxSFXAtEntity(player, 1003, par2, par3, par4, 0);
			par1World.notifyBlockChange(par2, par3, par4, this.blockID);
			return true;
		}
		//--hinge

		if(!par1World.isRemote)
			return false;

		if(tes == null)
		{
			tes = TileEntitySculpture.full;
			tes.xCoord = par2;
			tes.yCoord = par3;
			tes.zCoord = par4;
			tes.worldObj = par1World;
			tes.blockMetadata = player.getCurrentEquippedItem().getItemDamage() >> 4;
		}


		Vec3 look = player.getLookVec();
		look = look.addVector(look.xCoord * 4, look.yCoord * 4, look.zCoord * 4);
		int[] pos = tes.rayTrace(player.getPosition(1f),look);
		int mode = tes.getMode(player.getCurrentEquippedItem());
		if(mode == -1)return false;

		int id_inhand = player.getCurrentEquippedItem().getItemDamage() & 15;
		int meta_inhand = player.getCurrentEquippedItem().getItemDamage() >> 4;
			int face = 0;
			if(pos != null)face = pos[3];
			pos = tes.selectionBox(pos, mode, tes.getAxis(look), tes.getMinMax());
			int modCount = 0;

			if(pos != null)
				PacketDispatcher.sendPacketToServer(PacketHandler.sendPacket(par2, par3, par4, 
						pos[0],
						pos[1],
						pos[2],
						pos[3],
						pos[4],
						pos[5],
						mode,
						face
						));
			else return false;

			return false;
	}

	public void dropAllScrap(World w,int x,int y,int z,int modCount)
	{
		TileEntitySculpture tes = (TileEntitySculpture) w.getBlockTileEntity(x, y, z);
		if(tes == null)
		{
			Debug.dafuq("cant drop from a non sculpture !");
			return; 
		}

		if(modCount >= 512)
		{
			ItemStack is = new ItemStack(tes.blockId, 1, tes.getBlockMetadata());
			EntityItem entity = new EntityItem(w,x,y,z,is);
			entity.delayBeforeCanPickup = 10;
			w.spawnEntityInWorld(entity);
			modCount %= 512;
		}

		if(modCount >= 64)
		{
			dropScrap(w,x,y,z,modCount/64,"SculptureCover");
			modCount %= 64;
		}
		if(modCount >= 8)
		{
			dropScrap(w,x,y,z,modCount/8,"SculptureBar");
			modCount %= 8;
		}
		if(modCount > 0)
			dropScrap(w,x,y,z,modCount,"SculpturePiece");
	}

	private void dropScrap(World w, int x,int y,int z, int count, String name)
	{
		
		TileEntitySculpture tes = (TileEntitySculpture) w.getBlockTileEntity(x, y, z);
		if(tes == null)
		{
			Debug.dafuq("cant drop from a non sculpture !");
			return; 
		}
		ItemStack is = new ItemStack(ModMinePainter.instance.item(name).item());
		is.stackSize = count;
		is.setItemDamage(w.getBlockMetadata(x, y, z) + (tes.blockId << 4));
		
		this.dropBlockAsItem_do(w, x, y, z, is);
	}

	public static boolean sculptable(World w, int x,int y,int z)
	{
		int blockID = w.getBlockId(x,y,z);
		int blockMeta =  w.getBlockMetadata(x, y, z);
		
		return sculptable(blockID,blockMeta);
	}
	
	public static boolean sculptable(int blockID, int blockMeta)
	{
		if(Block.blocksList[blockID] == null)return false;
		
		if(blockID == Block.grass.blockID)return false;
		if(blockID == Block.cactus.blockID)return false;
		if(blockID == Block.glass.blockID)return true;
		if(blockID == Block.leaves.blockID)return false;

		Block b = Block.blocksList[blockID];

		if(b.hasTileEntity(blockMeta))return false;
		if(!b.renderAsNormalBlock())return false;
		
		if(b.getBlockBoundsMaxX()!=1.0f)return false;
		if(b.getBlockBoundsMaxY()!=1.0f)return false;
		if(b.getBlockBoundsMaxZ()!=1.0f)return false;
		if(b.getBlockBoundsMinX()!=0.0f)return false;
		if(b.getBlockBoundsMinY()!=0.0f)return false;
		if(b.getBlockBoundsMinZ()!=0.0f)return false;
		
		
		return true;
	}
}
