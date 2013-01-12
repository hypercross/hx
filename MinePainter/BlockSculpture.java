package hx.MinePainter;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockSculpture extends BlockContainer{

	public BlockSculpture(int id) {
		super(id, Material.rock);
		setBlockName("blockSculpture");
		setCreativeTab(CreativeTabs.tabDecorations);
		this.setRequiresSelfNotify();
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
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		TileEntitySculpture tes = (TileEntitySculpture) par1World.getBlockTileEntity(par2, par3, par4);
		
		Vec3 look = player.getLookVec();
		look = look.addVector(look.xCoord * 4, look.yCoord * 4, look.zCoord * 4);
		int[] pos = tes.rayTrace(player.getPosition(1f),look);
		
		if(pos != null)
		{
			tes.del(pos[0], pos[1], pos[2]);
			par1World.markBlockForRenderUpdate( par2, par3, par4);
		}
		else return false;
		
		return true;
	}
}
