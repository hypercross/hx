package hx.soundbox;

import cpw.mods.fml.common.network.PacketDispatcher;
import hx.utils.Debug;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTransmittedNote extends BlockContainer{

	public BlockTransmittedNote(int par1) {
		super(par1, Material.wood);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	public void registerIcons(IconRegister ir)
	{
		this.blockIcon = ir.registerIcon("musicBlock");
	}

	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
	{
		int power = par1World.getBlockPowerInput(par2,par3,par4);
		TileEntityTransmittedNote note = (TileEntityTransmittedNote) par1World.getBlockTileEntity(par2, par3, par4);

		if(note.prevRedstoneState != power)
		{
			note.prevRedstoneState = power;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityTransmittedNote();
	}

}
