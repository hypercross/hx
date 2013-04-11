package hx.MinePainter;

import hx.utils.Debug;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemSchematicRenderer implements IItemRenderer{
	private static TileEntitySculpture tes = new TileEntitySculpture();
	private static RenderItem ri = new RenderItem();
	private static ItemStack is = new ItemStack(Block.planks);

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		
		ItemSchematic.checkNBT(item);
		byte[] bytes = item.getTagCompound().getCompoundTag("sculptureInfo").getByteArray("data");
		
		tes.data = bytes;
		
		RenderHelper.enableGUIStandardItemLighting();
		
		GL11.glPushMatrix();
		GL11.glScaled(1/2f,1/2f,1/2f);
		
		for(int x = 0;x<8;x++)
			for(int y = 0;y<8;y++)
				for(int z = 0;z<8;z++)
					if(tes.get(x, y, z))
					{
						int i = 1;
						while(tes.get(x, y, z + i))i++;
						Block.planks.setBlockBounds(x/8f, y/8f, z/8f,
								(x+1)/8f,(y+1)/8f,(z+i)/8f);
						
						ri.renderItemIntoGUI(
								Minecraft.getMinecraft().fontRenderer,
								Minecraft.getMinecraft().renderEngine, is, 8,8);
						z += i-1;
					}
		
		GL11.glPopMatrix();
		Block.planks.setBlockBounds(0, 0, 0, 1,1,1);
		
		ri.renderItemIntoGUI(
				Minecraft.getMinecraft().fontRenderer,
				Minecraft.getMinecraft().renderEngine, item, 0,0);
	}

}
