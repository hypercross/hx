package hx.MinePainter;

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
		
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {

		TileEntitySculpture tes = (TileEntitySculpture) world.getBlockTileEntity(x, y, z);
		IBlockAccess iba = renderer.blockAccess;
		renderer.blockAccess = tes;
		
		for(int _x = 0; _x < 8; _x ++)
			for(int _y = 0; _y < 8; _y ++)
				for(int _z = 0; _z < 8; _z ++)
				{
					if(!tes.get(_x, _y, _z))continue;
					
					tes.bias(_x, _y, _z);
					renderer.setRenderMinMax(_x/8f, _y/8f, _z/8f, (_x+1)/8f, (_y+1)/8f, (_z+1)/8f);
					renderer.renderStandardBlock(Block.cobblestone, x, y, z);
				}
		
		renderer.blockAccess = iba;
		
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return ModMinePainter.instance.block("Sculpture").ri();
	}
}
