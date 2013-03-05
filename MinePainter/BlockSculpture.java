package hx.MinePainter;

import hx.utils.Debug;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockSculpture extends BlockContainer{
	
	private Block[] materialBlock = {Block.stone, Block.dirt, Block.sand, Block.blockSteel, Block.blockDiamond, Block.blockGold, Block.blockLapis, Block.blockEmerald,
									Block.planks, Block.brick, Block.glass, Block.ice, Block.glowStone, Block.netherBrick, Block.stoneBrick, Block.obsidian};
	
	public boolean onSelect = false;
	public static boolean createEmpty = false;

	public BlockSculpture(int id) {
		super(id, Material.rock);
		setBlockName("blockSculpture");
		setCreativeTab(CreativeTabs.tabDecorations);
		this.setRequiresSelfNotify();
		this.setHardness(10f);
	}
	
	public int getBlockTextureFromSideAndMetadata(int par1, int par2)
	{
		return materialBlock[par2].getBlockTextureFromSide(par1);
	}
	
	public float getBlockHardness(World par1World, int par2, int par3, int par4)
    {
        return materialBlock[par1World.getBlockMetadata(par2, par3, par4)].getBlockHardness(par1World, par2, par3, par4);
    }
	
	public float getAmbientOcclusionLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return 1.0F;
    }
	
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		if(onSelect)return;
		TileEntitySculpture tes = (TileEntitySculpture) par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
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
		return materialBlock[par1].blockID;
	}
	
	public int idPicked(World par1World, int par2, int par3, int par4)
	{
		return materialBlock[par1World.getBlockId(par2, par3, par4)].blockID;
	}

	public void addCollidingBlockToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
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
						setBlockBounds( Math.max(_x/4 * 0.50f, minx),
										Math.max(_y/4 * 0.50f, miny), 
										Math.max(_z/4 * 0.50f, minz),
										Math.min((_x/4 +1) * 0.5f, maxx),
										Math.min((_y/4 +1) * 0.5f, maxy),
										Math.min((_z/4 +1) * 0.5f, maxz));

						AxisAlignedBB var8 = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);

				        if (var8 != null && par5AxisAlignedBB.intersectsWith(var8))
				        {
				            par6List.add(var8);
				        }
				        
				        _x = _x/4*4 + 3;
				        _y = _y/4*4 + 3;
				        _z = _z/4*4 + 3;
					}
				}
		tes.updateBounds(this);
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
	
	public int idDropped(int par1)
    {
        return materialBlock[par1].blockID;
    }
	
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
//        for (int var4 = 0; var4 < 16; ++var4)
//        {
//            par3List.add(new ItemStack(this, 1, var4));
//        }
    }
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if(par1World.isRemote)
			return false;
		TileEntitySculpture tes = (TileEntitySculpture) par1World.getBlockTileEntity(par2, par3, par4);
		
		Vec3 look = player.getLookVec();
		look = look.addVector(look.xCoord * 4, look.yCoord * 4, look.zCoord * 4);
		int[] pos = tes.rayTrace(player.getPosition(1f).addVector(0, player.getEyeHeight(), 0),look);
		int mode = tes.getMode(player.getCurrentEquippedItem());
		int face = 0;
		if(pos != null)face = pos[3];
		pos = tes.selectionBox(pos, mode, tes.getAxis(look), tes.getMinMax());
		int modCount = 0;
		
		if(pos != null)
		{	
			if(mode < 3)
				for(int x=pos[0];x<pos[3];x++)
					for(int y=pos[1];y<pos[4];y++)
						for(int z=pos[2];z<pos[5];z++)
						{
							if(tes.get(x,y,z))modCount++;
							tes.del(x, y, z);
						}
			
			else
				for(int x=pos[0];x<pos[3];x++)
					for(int y=pos[1];y<pos[4];y++)
						for(int z=pos[2];z<pos[5];z++)
						{
							if(tes.invalid(x, y, z))
							{
								int ox = par2 + Facing.offsetsXForSide[face];
								int oy = par3 + Facing.offsetsYForSide[face];
								int oz = par4 + Facing.offsetsZForSide[face];
								
								if(par1World.isAirBlock(ox, oy, oz))
								{
									createEmpty = true;
									par1World.setBlockAndMetadataWithUpdate( ox,oy,oz, this.blockID, tes.getBlockMetadata(),false);
									TileEntitySculpture another = (TileEntitySculpture) par1World.getBlockTileEntity(ox,oy,oz);
									another.clear();
								}
								if(par1World.getBlockId(ox,oy,oz) != this.blockID)continue;
								
								TileEntitySculpture another = (TileEntitySculpture) par1World.getBlockTileEntity(ox,oy,oz);
								another.set(x , y , z );
								modCount++;
							}else{
								tes.set(x, y, z);
								modCount++;
							}
						}
		}
		else return false;
		
		if(mode < 3)
		{
			if(modCount >= 64)
			{
				dropScrap(par1World,par2,par3,par4,modCount/64,"SculptureCover");
				modCount %= 64;
			}
			if(modCount >= 8)
			{
				dropScrap(par1World,par2,par3,par4,modCount/8,"SculptureBar");
				modCount %= 8;
			}
			if(modCount > 0)
				dropScrap(par1World,par2,par3,par4,modCount,"SculpturePiece");			
			
			if(tes.isEmpty())par1World.setBlock(par2, par3, par4, 0);
		}else
			player.getCurrentEquippedItem().stackSize--;

		par1World.notifyBlockChange(par2, par3, par4, this.blockID);
		
		return true;
	}
	
	private void dropScrap(World w, int x,int y,int z, int count, String name)
	{
		ItemStack is = new ItemStack(ModMinePainter.instance.item(name).item());
		is.stackSize = count;
		EntityItem entity = new EntityItem(w, x,y,z, is);
		entity.delayBeforeCanPickup = 10;
		w.spawnEntityInWorld(entity);
	}
	
	public Block materialBlock(int meta)
	{
		return materialBlock[meta];
	}
}
