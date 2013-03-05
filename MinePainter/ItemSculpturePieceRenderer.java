package hx.MinePainter;

import org.lwjgl.opengl.GL11;

import hx.utils.Debug;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemSculpturePieceRenderer implements IItemRenderer {
	private static RenderItem renderItem = new RenderItem();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.INVENTORY ||
				type == ItemRenderType.ENTITY ||
				type == ItemRenderType.EQUIPPED;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		
		renderItem.setRenderManager(RenderManager.instance);
		
		ItemStack is = new ItemStack(BlockSculpture.materialBlock[item.getItemDamage()]);
		RenderBlocks rb = (RenderBlocks) (data[0]);
	
		if(type == ItemRenderType.INVENTORY)
		{
			GL11.glPushMatrix();
			GL11.glScaled(1/2f, 1/2f, 1/2f);
			RenderHelper.enableGUIStandardItemLighting();
			renderItem.renderItemIntoGUI(
					Minecraft.getMinecraft().fontRenderer,
					Minecraft.getMinecraft().renderEngine, is, 8, 8);
			GL11.glPopMatrix();
		}else if(type == ItemRenderType.ENTITY)
		{
			EntityItem eis = (EntityItem)data[1];
			eis.func_92013_a(is);
			
			renderItem.doRenderItem(eis, eis.posX, eis.posY, eis.posZ, 0,0);
		}
	}

}
