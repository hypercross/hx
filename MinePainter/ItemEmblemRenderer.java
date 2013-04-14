package hx.MinePainter;

import hx.utils.Debug;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemEmblemRenderer implements IItemRenderer {
	private static RenderItem ri = new RenderItem();
	private static MPImage img = new MPImage();

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
		getImage(item);
		
		ItemEmblem ie = (ItemEmblem) ModMinePainter.instance.item("Emblem").item();
		ie.setBg(false);
		ri.renderItemIntoGUI(
				Minecraft.getMinecraft().fontRenderer,
				Minecraft.getMinecraft().renderEngine, item, 0,0);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glPushMatrix();
		GL11.glTranslated(4,4,0);
		GL11.glScaled(0.5f,0.5f,0.5f);
		
		for(int i = 0;i<16;i++)
			for(int j = 0;j<16;j++)drawPixel(i,j);
		
		GL11.glPopMatrix();
		
		ie.setBg(true);
		ri.renderItemIntoGUI(
				Minecraft.getMinecraft().fontRenderer,
				Minecraft.getMinecraft().renderEngine, item, 0,0);
		
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		ie.setBg(false);
	}

	private void getImage (ItemStack is)
	{
		if(!img.fromByteArray(ItemEmblem.checkNBT(is).getByteArray("data")))img.fill(-1);
	}
	
	private void drawPixel(int x,int y)
	{
		float[] color = img.rgba_at(x, y);
		if(color == null)return ;
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(color[0],color[1],color[2], color[3]);

		GL11.glVertex3d(x, y, 0);
		GL11.glVertex3d(x, y+1, 0);
		GL11.glVertex3d(x+1, y+1, 0);
		GL11.glVertex3d(x+1, y, 0);

		GL11.glEnd();
	}
}
