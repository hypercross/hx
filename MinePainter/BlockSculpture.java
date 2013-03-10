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
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockSculpture extends BlockContainer{
	
	public static Block[] materialBlock = {Block.stone, Block.dirt, Block.sand, Block.blockSteel, Block.blockDiamond, Block.blockGold, Block.blockLapis, Block.blockEmerald,
									Block.planks, Block.brick, Block.cloth, Block.glass, Block.sandStone, Block.netherBrick, Block.stoneBrick, Block.obsidian};
	
	public boolean onSelect = false;
	public static boolean createEmpty = false;
	public static int renderBlockMeta = 0;

	public BlockSculpture(int id) {
		super(id, Material.rock);
		setUnlocalizedName("blockSculpture");
		setCreativeTab(CreativeTabs.tabDecorations);
		//this.setRequiresSelfNotify();
		this.setHardness(10f);
	}
	
	public boolean shouldSideBeRendered(IBlockAccess iba, int par2, int par3, int par4, int par5)
    {
		if(iba instanceof TileEntitySculpture)return !((TileEntitySculpture) iba).get(par2, par3, par4);
        return !iba.isBlockOpaqueCube(par2, par3, par4);
    }
	
	public Icon getBlockTextureFromSideAndMetadata(int par1, int par2)
	{
		return materialBlock[par2].getBlockTextureFromSideAndMetadata(par1,renderBlockMeta);
	}
	
	public float getBlockHardness(World par1World, int par2, int par3, int par4)
    {
        return materialBlock[par1World.getBlockMetadata(par2, par3, par4)].getBlockHardness(par1World, par2, par3, par4);
    }
	
//	public float getAmbientOcclusionLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
//    {
//        return 1.0F;
//    }
	
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
		return 0;
	}
	
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
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
		return materialBlock[par1World.getBlockMetadata(par2, par3, par4)].blockID;
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
						setBlockBounds( Math.max(_x/4 * 0.50f, minx),
										Math.max(_y/4 * 0.50f, miny), 
										Math.max(_z/4 * 0.50f, minz),
										Math.min((_x/4 +1) * 0.5f, maxx),
										Math.min((_y/4 +1) * 0.5f, maxy),
										Math.min((_z/4 +1) * 0.5f, maxz));

						super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
				        
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
		
		if(this.blockID != par1World.getBlockId(par2, par3, par4))
				if(par1World.blockHasTileEntity(par2, par3, par4))return false;
		TileEntitySculpture tes = (TileEntitySculpture) par1World.getBlockTileEntity(par2, par3, par4);
		
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
		int[] pos = tes.rayTrace(player.getPosition(1f).addVector(0, player.getEyeHeight(), 0),look);
		int mode = tes.getMode(player.getCurrentEquippedItem());
		if(mode == -1)return false;

		int id_inhand = player.getCurrentEquippedItem().getItemDamage() & 15;
		int meta_inhand = player.getCurrentEquippedItem().getItemDamage() >> 4;
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
									par1World.setBlockAndMetadataWithNotify( ox,oy,oz, this.blockID, id_inhand, 2);
									TileEntitySculpture another = (TileEntitySculpture) par1World.getBlockTileEntity(ox,oy,oz);
									another.clear();
									another.blockMeta = meta_inhand;
									another.needUpdate = true;
								}
								if(par1World.getBlockId(ox,oy,oz) != this.blockID)continue;
								if(par1World.getBlockMetadata(ox, oy, oz)!= id_inhand)continue;
								
								TileEntitySculpture another = (TileEntitySculpture) par1World.getBlockTileEntity(ox,oy,oz);
								another.set(x , y , z );
								modCount++;
							}else if(id_inhand == tes.getBlockMetadata()){
								tes.set(x, y, z);
								modCount++;
							}
						}
		}
		else return false;
		
		if(mode < 3)
		{
			dropAllScrap(par1World,par2,par3,par4,modCount);			
			
			if(tes.isEmpty())
				{
					par1World.setBlockAndMetadataWithNotify(par2, par3, par4, 0,0,3);
					par1World.setBlockTileEntity(par2, par3, par4, null);
				}
		}else if(modCount > 0)
			player.getCurrentEquippedItem().stackSize--;

		par1World.notifyBlockChange(par2, par3, par4, this.blockID);
		
		return true;
	}
	
	public void dropAllScrap(World w,int x,int y,int z,int modCount)
	{
		TileEntitySculpture tes = (TileEntitySculpture) w.getBlockTileEntity(x, y, z);
		
		if(modCount >= 512)
    	{
    		ItemStack is = new ItemStack(this.materialBlock(tes.getBlockMetadata()), 1, tes.blockMeta);
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
		ItemStack is = new ItemStack(ModMinePainter.instance.item(name).item());
		is.stackSize = count;
		is.setItemDamage(w.getBlockMetadata(x, y, z) + (tes.blockMeta << 4));
		EntityItem entity = new EntityItem(w, x,y,z, is);
		entity.delayBeforeCanPickup = 10;
		w.spawnEntityInWorld(entity);
	}
	
	public Block materialBlock(int meta)
	{
		return materialBlock[meta];
	}
}
