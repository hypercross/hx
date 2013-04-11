package hx.MinePainter;

import org.lwjgl.opengl.GL11;

import hx.utils.Debug;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;

public class ItemSculpturePieceRenderer implements IItemRenderer {
	private static RenderItem renderItem = new RenderItem();
	private static EntityItem ei = new EntityItem(null);
	
	int minmax[]={5,5,5,8,8,8};
	int   biasX = 0, biasY = 0;

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.INVENTORY ||
				type == ItemRenderType.ENTITY ||
				type == ItemRenderType.EQUIPPED;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return type == ItemRenderType.ENTITY;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		
		renderItem.setRenderManager(RenderManager.instance);
		int id = item.getItemDamage() & 15;
		int meta = item.getItemDamage() >> 4;
		
		ItemStack is = new ItemStack(BlockSculpture.instance, 1 , id);
		RenderBlocks rb = (RenderBlocks) (data[0]);
	
		BlockSculpture.renderBlockMeta = meta;
		BlockSculptureRenderer.instance.inv_minmax = minmax;
		
		if(type == ItemRenderType.INVENTORY)
		{
			RenderHelper.enableGUIStandardItemLighting();
			renderItem.renderItemIntoGUI(
					Minecraft.getMinecraft().fontRenderer,
					Minecraft.getMinecraft().renderEngine, is, biasX,biasY);
		}else if(type == ItemRenderType.ENTITY)
		{
			EntityItem eis = (EntityItem)data[1];
			GL11.glScalef(.5f,.5f,.5f);
			RenderEngine re = RenderManager.instance.renderEngine; 
			re.bindTexture("/terrain.png");
			rb.renderBlockAsItem(BlockSculpture.instance, id, 1);

		}else
		{
			Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItem((EntityLiving) data[1],
					is, 0);
		}
	}

}
