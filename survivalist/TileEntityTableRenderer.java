package hx.survivalist;

import org.lwjgl.opengl.GL11;

import hx.utils.Debug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTableRenderer extends TileEntitySpecialRenderer{

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1,
			double d2, float f) {
		TileEntityTable tepi = (TileEntityTable) tileentity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float)d0 + 0.25f,(float)d1 + 0.625f,(float)d2 + 0.25f);
		
		for(int i = 0; i< tepi.size();i++)
		{
			if(tepi.get(i) == null)continue;
			EntityItem ei = new EntityItem(tepi.worldObj, d0, d1, d2, tepi.get(i));
			ei.hoverStart = 0;
			
			GL11.glPushMatrix();
			GL11.glTranslatef(0.5f * (i % 2),0,0.5f * (i / 2));
			
			if(Minecraft.isFancyGraphicsEnabled())
				GL11.glRotatef(tepi.yaw[i], 0, -1, 0);
			RenderManager.instance.renderEntityWithPosYaw(ei, 0,0,0, 0, 0);
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
	}

}
