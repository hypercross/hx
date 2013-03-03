package hx.MinePainter;

import hx.utils.Debug;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockSculpture extends BlockContainer{
	
	private Block[] materialBlock = {Block.stone, Block.dirt, Block.sand, Block.blockSteel, Block.blockDiamond, Block.blockGold, Block.blockLapis, Block.blockEmerald,
									Block.planks, Block.brick, Block.glass, Block.ice, Block.glowStone, Block.netherBrick, Block.stoneBrick, Block.obsidian};

	public BlockSculpture(int id) {
		super(id, Material.rock);
		setBlockName("blockSculpture");
		setCreativeTab(CreativeTabs.tabDecorations);
		this.setRequiresSelfNotify();
		this.setHardness(10f);
	}
	
	public float getBlockHardness(World par1World, int par2, int par3, int par4)
    {
        return materialBlock[par1World.getBlockMetadata(par2, par3, par4)].getBlockHardness(par1World, par2, par3, par4);
    }
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
		TileEntitySculpture tes = (TileEntitySculpture) par1World.getBlockTileEntity(par2, par3, par4);
		tes.updateBounds(this);
        return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
    }
	
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return materialBlock[par1].blockID;
	}

	public void addCollidingBlockToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
	{
		TileEntitySculpture tes = (TileEntitySculpture) par1World.getBlockTileEntity(par2, par3, par4);
		
		for(int _x = 0;_x<8;_x++)
			for(int _y = 0;_y<8;_y++)
				for(int _z = 0;_z<8;_z++)
				{
					if(tes.get(_x, _y, _z))
					{
						setBlockBounds( _x/4 * 0.50f, _y/4 * 0.50f, _z/4 * 0.5f,
								(_x/4 +1) * 0.5f, (_y/4 +1) * 0.5f, (_z/4 +1) * 0.5f);

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
		pos = tes.selectionBox(pos, tes.getMode(player.getCurrentEquippedItem()), tes.getAxis(look), tes.getMinMax());
		
		if(pos != null)
		{	
			for(int x=pos[0];x<pos[3];x++)
				for(int y=pos[1];y<pos[4];y++)
					for(int z=pos[2];z<pos[5];z++)
						tes.del(x, y, z);
		}
		else return false;
		
//		par1World.notifyBlockChange(par2, par3, par4, this.blockID);
		
		return true;
	}
	
	public Block materialBlock(int meta)
	{
		return materialBlock[meta];
	}
}
