package hx.Alchemania;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.TickType;

public class GUITickHandler implements ITickHandler{

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		onTickInGame(Minecraft.getMinecraft());
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "AlchemaniaGUITick";
	}
	
	private void onTickInGame(Minecraft mc)
	{
		if(mc.currentScreen instanceof GuiContainer)
		{
			ScaledResolution var13 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            int var14 = var13.getScaledWidth();
            int var15 = var13.getScaledHeight();
            int x = Mouse.getX() * var14 / mc.displayWidth;
            int y = var15 - Mouse.getY() * var15 / mc.displayHeight - 1;
            
            GuiContainer gc = (GuiContainer) mc.currentScreen;
            Slot slot = getHoverSlot(gc,x,y);
            if(slot == null)return;
            if(!slot.getHasStack())return;
            
            ItemStack is = slot.getStack();
            if(!is.hasTagCompound())return;
            
            if(is.itemID == Alchemania.ingredientPowder.shiftedIndex)return;
            if(is.itemID == Alchemania.pill.shiftedIndex)return;
            if(is.itemID == Alchemania.solution.shiftedIndex)return;
            if(!is.stackTagCompound.hasKey("AME"))return;
            
            ArrayList<String> al = new ArrayList<String>();
            Alchemania.ingredientPowder.addInformation(is, null, al, false);
            
            int yoff = 0;
            for(String txt : al)
            {
            	int xs = x + 8;
            	int ys = y - 28 + yoff;
            	int xe = xs + mc.fontRenderer.getStringWidth(txt) + 4;
            	int ye = ys + 12;
            	int color = 0xaa000000;

            	GL11.glDisable(GL11.GL_LIGHTING);
            	GL11.glDisable(GL11.GL_DEPTH_TEST);
            	drawGradientRect(xs,ys,xe,ye,color,color);
            	mc.fontRenderer.drawStringWithShadow(txt, xs + 2, ys + 2, 0xffffff);
            	GL11.glEnable(GL11.GL_DEPTH_TEST);
            	GL11.glEnable(GL11.GL_LIGHTING);
            	
            	yoff-=12;
            }
		}
	}
	
	Slot getHoverSlot(GuiContainer gc,int mx,int my)
	{
		if(!ObfuscationReflectionHelper.obfuscation)
			return (Slot)ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, gc, "theSlot");
		else 
			return (Slot)ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, gc, "p");
	}
	
	protected void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float var7 = (float)(par5 >> 24 & 255) / 255.0F;
        float var8 = (float)(par5 >> 16 & 255) / 255.0F;
        float var9 = (float)(par5 >> 8 & 255) / 255.0F;
        float var10 = (float)(par5 & 255) / 255.0F;
        float var11 = (float)(par6 >> 24 & 255) / 255.0F;
        float var12 = (float)(par6 >> 16 & 255) / 255.0F;
        float var13 = (float)(par6 >> 8 & 255) / 255.0F;
        float var14 = (float)(par6 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator var15 = Tessellator.instance;
        var15.startDrawingQuads();
        var15.setColorRGBA_F(var8, var9, var10, var7);
        var15.addVertex((double)par3, (double)par2, (double)300);
        var15.addVertex((double)par1, (double)par2, (double)300);
        var15.setColorRGBA_F(var12, var13, var14, var11);
        var15.addVertex((double)par1, (double)par4, (double)300);
        var15.addVertex((double)par3, (double)par4, (double)300);
        var15.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
