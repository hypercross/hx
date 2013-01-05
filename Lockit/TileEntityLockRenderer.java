package hx.Lockit;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.*;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
@SideOnly(Side.CLIENT)

public class TileEntityLockRenderer extends TileEntitySpecialRenderer
{
    private ModelLock model = new ModelLock();
    private ModelLockUnlocked model2 = new ModelLockUnlocked();

    @Override
    public void renderTileEntityAt(TileEntity var1, double var2, double var4,
            double var6, float var8)
    {
        TileEntityLock lock = (TileEntityLock)var1;

        if (lock.isSolid)
        {
            return;
        }

        GL11.glPushMatrix();
        bindTextureByName("/hx/Lockit/img/lock.png");
        GL11.glTranslated(var2 + 0.5D , var4 + 0.5D, var6 + 0.5d);
        int facing = lock.getBlockMetadata();
        boolean unlocked = false;

        if (facing > 4)
        {
            facing -= 4;
            unlocked = true;
        }

        switch (facing)
        {
            case 3:
                GL11.glRotatef(90, 0, 1, 0);
                break;

            case 1:
                GL11.glRotatef(180, 0, 1, 0);
                break;

            case 4:
                GL11.glRotatef(270, 0, 1, 0);
                break;
        }

        GL11.glTranslatef(0.46f, 0, 0);
        GL11.glPushMatrix();
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (unlocked)
        {
            model.render(0.04f);
        }
        else
        {
            model2.render(0.04f);
        }

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
}
