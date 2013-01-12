package hx.MinePainter;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSculpture extends BlockContainer{

	public BlockSculpture(int id) {
		super(id, Material.rock);
		setBlockName("blockSculpture");
		setCreativeTab(CreativeTabs.tabDecorations);
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
}
