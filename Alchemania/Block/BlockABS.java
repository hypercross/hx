package hx.Alchemania.Block;

import net.minecraft.block.BlockBrewingStand;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.world.World;

public class BlockABS extends BlockBrewingStand{

	public BlockABS(int par1) {
		super(par1);
	}

	public TileEntity createNewTileEntity(World par1World)
    {
        return new TileEntityABS();
    }
}
