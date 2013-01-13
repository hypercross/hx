package hx.MinePainter;

import hx.utils.Debug;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockSculptureRenderer implements ISimpleBlockRenderingHandler{

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		
		renderer.renderBlockAsItem(((BlockSculpture)block).materialBlock(metadata),1,1);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {

		TileEntitySculpture tes = (TileEntitySculpture) world.getBlockTileEntity(x, y, z);
		IBlockAccess iba = renderer.blockAccess;
		renderer.blockAccess = tes;
		
		BlockSculpture sculpture = (BlockSculpture) block;
		
		for(int _x = 0; _x < 8; _x ++)
			for(int _y = 0; _y < 8; _y ++)
				for(int _z = 0; _z < 8; _z ++)
				{
					if(!tes.get(_x, _y, _z))continue;
					
					tes.bias(_x, _y, _z);
					renderer.setRenderMinMax(_x/8f, _y/8f, _z/8f, (_x+1)/8f, (_y+1)/8f, (_z+1)/8f);
					renderer.renderStandardBlock( sculpture.materialBlock(tes.getBlockMetadata()) , x, y, z);
				}
		
		renderer.blockAccess = iba;
		
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return ModMinePainter.instance.block("Sculpture").ri();
	}
}
