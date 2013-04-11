package hx.survivalist;

import hx.utils.Debug;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeHooks;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class FactorRenderTickHandler implements ITickHandler{

	Minecraft mc;
	Random rand = new Random();
	
	public FactorRenderTickHandler()
	{
		 mc = Minecraft.getMinecraft();
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	private boolean isSkipped()
	{
		if(mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat))
			return true;
		if(mc.playerController == null)return true;
		if(!mc.playerController.shouldDrawHUD())return true;
		return false;
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		
		if(isSkipped())return;
		
		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth,mc.displayHeight);
        int width = scaledresolution.getScaledWidth();
        int height = scaledresolution.getScaledHeight();
        mc.entityRenderer.setupOverlayRendering();
        mc.renderEngine.bindTexture("/gui/items.png");
        
        int x_Right = width / 2 + 93;
        int y_Bottom =  height - 17 ;
        if(mc.currentScreen instanceof GuiChat)y_Bottom -= 16;

        for(IFactor factor : PlayerStat.getStat(mc.thePlayer).factors())
        {
        	if(!factor.enabled())continue;
        	int color = combineColor ( factor.getFullColor(),
        							   factor.getSaturation(),
        							   factor.getLowSaturationColor(),
        							   factor.getLevel() - factor.getSaturation());
        
        	color = combineColor( color, factor.getLevel(), 
        			factor.getEmptyColor(), factor.getMaxLevel() - factor.getLevel());
        
        	
        	int x1 = x_Right;
        	int y1 = y_Bottom;
        	int max =  20;        	
        	int amount = max * factor.getLevel() / factor.getMaxLevel();
        	
        	mc.ingameGUI.drawRect(x1-1, y1 + 16 - 21, x1 + 3, y1 + 17, 0xff000000);
        	mc.ingameGUI.drawRect(x1, y1 + 16 - amount, x1 + 2, y1 + 16, color);   	
        	GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        	if(factor.drained())
        	{
        		y1 += rand.nextInt()% 2;
        	}
        	mc.ingameGUI.drawTexturedModelRectFromIcon(x1 + 3, y1, factor.getIcon(), 16, 16);
        	
        	if(factor.fulled())
        	{
        		GL11.glEnable(GL11.GL_BLEND);
        		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        		mc.ingameGUI.drawTexturedModelRectFromIcon(x1 + 3, y1, factor.getIcon(), 16, 16);
        		GL11.glDisable(GL11.GL_BLEND);
        	}
        	
        	x_Right += 20;
        }
        
        if(!ModSurvivalist.instance.debug_mode)return;
        
        int h = 0;
        for(PlayerFactor factor : PlayerStat.getStat(mc.thePlayer).factors())
        {
        	mc.ingameGUI.drawString(mc.fontRenderer, factor.toString(),
        		0,h,0xffFFFF00);	
        	h+=15;
        }
	}
	
	private int combineColor(int color1, int w1, int color2, int w2)
	{
		if(w1 == 0)return color2;
		if(w2 == 0)return color1;
		if(w1 + w2 == 0)return color1;
		
		int result = 0;
		
		for(int i = 0; i < 32; i+=8)
		{
			int raw1 = (color1 >> i) & 0xff;
			int raw2 = (color2 >> i) & 0xff;
			int raw = ( raw1 * w1 + raw2 * w2 ) / (w1 + w2);
			result += (raw << i);
		}
		
		return result;
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "Factor Renderer";
	}
}
