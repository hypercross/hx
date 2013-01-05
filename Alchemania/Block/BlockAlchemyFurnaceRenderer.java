package hx.Alchemania.Block;

import hx.Alchemania.Alchemania;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockAlchemyFurnaceRenderer implements
		ISimpleBlockRenderingHandler {

	private float[][] boxes = new float[6][6];
	
	
	private float[] centralBox(float height, float radius, float yPos)
	{
		float[] box = new float[6];
		
		box[0] = 0.5f - radius;
		box[1] = yPos - height;
		box[2] = 0.5f - radius;
		
		box[3] = 0.5f + radius;
		box[4] = yPos + height;
		box[5] = 0.5f + radius;
		
		return box;
	}
	
	private float[] rotate(float[] box)
	{
		float[] ans = box.clone();
		
		for(int i =0;i<box.length;i++)
		{
			box[i] -=.5f;
			ans[i] -=.5f;
		}
		
		ans[0] = -box[5];
		ans[2] = box[0];
		ans[3] = -box[2];
		ans[5] = box[3];
		
		
		for(int i =0;i<box.length;i++)
		{
			box[i] +=.5f;
			ans[i] +=.5f;
		}
		return ans;
	}
	
	public BlockAlchemyFurnaceRenderer()
	{
		float[] bar = { 0.4f, 0.45f, 0.8f, 0.6f, 0.55f, 1.0f};
		boxes[0] = bar;
		for(int i =1;i<=3;i++)
			boxes[i] = rotate(boxes[i-1]);
		boxes[4] = centralBox(0.1f,0.2f,0.9f);
		boxes[5] = centralBox(0.4f,0.4f,0.4f);
	}
	
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		
		renderer.renderAllFaces = true;
		
		boolean dir = (world.getBlockMetadata(x, y, z) >=4);
		
		for(int i : (dir ? new int[]{0,2,4,5} : new int[]{1,3,4,5}))
		{
			renderer.setRenderMinMax(boxes[i][0],boxes[i][1],boxes[i][2],
									boxes[i][3],boxes[i][4],boxes[i][5]);
			renderer.renderStandardBlock(block, x,y,z);
		}
		renderer.renderAllFaces = false;
		
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return Alchemania.alchemyFurnaceRI;
	}

}
