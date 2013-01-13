package hx.MinePainter;

import hx.utils.Debug;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
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
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
		TileEntitySculpture tes = (TileEntitySculpture) par1World.getBlockTileEntity(par2, par3, par4);
		tes.updateBounds(this);
        return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
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
		
		return true;
	}
	
	public Block materialBlock(int meta)
	{
		return materialBlock[meta];
	}
}
