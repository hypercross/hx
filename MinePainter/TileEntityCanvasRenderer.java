package hx.MinePainter;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCanvasRenderer extends TileEntitySpecialRenderer
{
	private int[] ang = { 180, 0, 90, 270 };
	private float scale = 1/16f;
	
	private int verAngle(int meta)
    {
        return (1 - (meta >> 2)) * 90;
    }

    private int horAngle(int meta)
    {
        return ang[(meta & 3)] ;
    }

    public TileEntityCanvasRenderer()
    {
    }
    
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y,
			double z, float param) {
		
		TileEntityCanvas canvas = (TileEntityCanvas)entity;
		int meta = canvas.getBlockMetadata();
		
		GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5d, y + 0.5d, z + 0.5d);
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glRotatef(horAngle(meta), 0, 1, 0);
        GL11.glRotatef(verAngle(meta), 1, 0, 0);
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glTranslated(0, 0, -0.499d);
        GL11.glScalef(scale, scale, scale);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        
        for(int i =-8;i<8;i++)
        {
        	GL11.glBegin(GL11.GL_QUAD_STRIP);
        	for(int j =-8;j<=8;j++)
        		drawSquareAt(i,j,canvas.image.rgb_at(i + 8, j + 8));
        	GL11.glEnd();
        }
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        
        GL11.glPopMatrix();
	}

	private void drawSquareAt(double x,double y,float[] rgb)
	{
		if(rgb == null)
		{
			GL11.glEnd();
			GL11.glBegin(GL11.GL_QUAD_STRIP);
		}
		else
			GL11.glColor3f(rgb[0],rgb[1],rgb[2]);
		
		GL11.glVertex3d(x,y,0);
		GL11.glVertex3d(x+1,y,0);
	}
}
