package hx.Lockit;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEngravingRenderer extends TileEntitySpecialRenderer
{
    private float scale = 0.016666668F;

    @Override
    public void renderTileEntityAt(TileEntity entity, double x, double y,
            double z, float param)
    {
        TileEntityEngraving tee = (TileEntityEngraving)entity;
        int meta = tee.getBlockMetadata();
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5d, y + 0.5d, z + 0.5d);
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(180, 1, 0, 0);
        FontRenderer fr = this.getFontRenderer();

        if (param >= 0)
        {
            GL11.glRotatef(tee.horAngle(meta), 0, 1, 0);
            GL11.glRotatef(tee.verAngle(meta), 1, 0, 0);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 28.8f);
        //System.err.println(tee.lineBeingEdited);
        String toRender;

        for (int i = 0; i < 4; i++)
        {
            toRender = tee.signText[i];

            if (!tee.isEditable() && i == tee.lineBeingEdited)
            {
                toRender = ">" + toRender + "<";
            }

            fr.drawString(toRender, -fr.getStringWidth(toRender) / 2, -20 + 8 * i, 0);
        }

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
}
