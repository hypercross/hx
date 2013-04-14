package hx.MinePainter;

import hx.utils.Debug;

import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityCanvasRenderer extends TileEntitySpecialRenderer
{
	private int[] ang = { 180, 0, 90, 270 };
	private float scale = 1/16f * 504/500f;
	private float zScale = 1/16f;
	
	private int verAngle(int meta)
    {
        return (1 - (meta >> 2)) * 90;
    }

    private int horAngle(int meta)
    {
        return ang[(meta & 3)] ;
    }
    
    private int face(int meta)
    {
    	if(meta < 4) return 0 ; 
    	if(meta > 7) return 1;
    	return meta - 2;
    }
    
    private void rotateH(int[] pos)
    {
    	int x = 7 - pos[2];
    	int y = pos[0];
    	
    	pos[0] = x;
    	pos[2] = y;
    }
    
    private void rotateV(int[] pos)
    {
    	int x = 7 - pos[2];
    	int y = pos[1];
    	
    	pos[1] = x;
    	pos[2] = y;
    }
    
    private int[] toTES(int meta,int x,int y)
    {
    	int[] pos = new int[]{x,7 - y,0};
    	for(int i = verAngle(meta); i%360 != 0; i+= 90)
    		rotateV(pos);
    	
    	for(int i = horAngle(meta) + 180; i%360 != 0; i-= 90)
    		rotateH(pos);    	
    	
    	return pos;
    }
    
    private int[][] depthMap(TileEntitySculpture tes, int meta)
    {
    	int[][] mapping = new int[16][16];
    	ForgeDirection dir = ForgeDirection.getOrientation(face(meta));
    	
    	for(int i =0;i<8;i++)
    		for(int j =0;j<8;j++)
    		{
    			int k = 0;
    			if(tes != null)
    			{
    				int[] pos = toTES(meta, i, j);
    				while(!tes.get(pos[0], pos[1], pos[2]))
    				{
    					pos[0] -= dir.offsetX;
    					pos[1] -= dir.offsetY;
    					pos[2] -= dir.offsetZ;
    					k++;			
    					if(tes.invalid(pos[0], pos[1], pos[2]))break;
    				}
    			}
    			mapping[i][j] = k;
    		}
    	
    	return mapping;
    }

    public TileEntityCanvasRenderer()
    {
    }
    
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y,
			double z, float param) {
		
		TileEntityCanvas canvas = (TileEntityCanvas)entity;
		int meta = canvas.getBlockMetadata();
		
		TileEntitySculpture tes = BlockCanvas.getSculptureOnBack(entity.worldObj,canvas.xCoord,canvas.yCoord,canvas.zCoord,face(meta));
		int[][] depth = depthMap(tes,meta);
		
		bindTextureByName("/terrain.png");
		
		GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5d, y + 0.5d, z + 0.5d);
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glRotatef(horAngle(meta), 0, 1, 0);
        GL11.glRotatef(verAngle(meta), 1, 0, 0);
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glTranslated(0, 0, -0.498d);
        GL11.glScalef(scale, scale, zScale);
        
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        
        for(int i =-8;i<8;i++)
        {
        	for(int j =-8;j<8;j++)
        		drawSquareAt(i,j, depth[(i+8)/2][(j+8)/2]*2,canvas.image.rgba_at(i + 8, j + 8));
        }
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
	}

	private void drawSquareAt(double x,double y,int depth, float[] rgba)
	{
		if(rgba == null)return;
		if(depth == 16)return;
		
		Tessellator tes = Tessellator.instance; 
		
		tes.startDrawingQuads();
		//GL11.glColor3f(rgb[0],rgb[1],rgb[2]);
		tes.setColorRGBA_F(rgba[0],rgba[1],rgba[2],rgba[3]);

		tes.addVertexWithUV(x, y, -depth, 0, 0);
		tes.addVertexWithUV(x+1, y, -depth, 0, 0);
		
		tes.addVertexWithUV(x+1, y+1, -depth, 0, 0);
		tes.addVertexWithUV(x, y+1, -depth, 0, 0);
		
		tes.draw();
	}
}
