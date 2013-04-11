package hx.MinePainter;

import java.lang.reflect.Field;

import hx.utils.Debug;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class BlockSculptureRenderer implements ISimpleBlockRenderingHandler{
	
	public static BlockSculptureRenderer instance;
	
	private static int RAW_BUFFER_INDEX = 22;
	private static int RAW_BUFFER = 12;
	
	public int[] inv_minmax;
	
	private int x,y,z; //working block coordinates
	private int _x,_y,_z; //subblock coordinates
	
	private float mm = -0.1f, mM = 0.1f, Mm = 0.9f, MM = 1.1f;
	

	public BlockSculptureRenderer()
	{
		if(FMLClientHandler.instance().hasOptifine())
		{
			RAW_BUFFER_INDEX = 16;
			RAW_BUFFER = 6;
		}
		instance = this;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {

		renderer.setRenderBounds(inv_minmax[0]/8f,inv_minmax[1]/8f,inv_minmax[2]/8f,
				inv_minmax[3]/8f,inv_minmax[4]/8f,inv_minmax[5]/8f);
		Tessellator var4 = Tessellator.instance;
		BlockSculpture par1Block = BlockSculpture.instance;
		int par2 = metadata;
		
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        var4.startDrawingQuads();
        var4.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderBottomFace(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getBlockTextureFromSideAndMetadata(0, par2));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderTopFace(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getBlockTextureFromSideAndMetadata(1, par2));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderEastFace(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getBlockTextureFromSideAndMetadata(2, par2));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderWestFace(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getBlockTextureFromSideAndMetadata(3, par2));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderNorthFace(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getBlockTextureFromSideAndMetadata(4, par2));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderSouthFace(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getBlockTextureFromSideAndMetadata(5, par2));
        var4.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	private double[] rotate (double x, double y, double z, double angle)
	{
		return new double[] { x*Math.cos(angle) + z * Math.sin(angle), y, z * Math.sin(angle) + z * Math.cos(angle) };
	}
	
	public boolean updateDisplayList(TileEntitySculpture tes, RenderBlocks renderer,Block block)
	{	
		
		if(tes.displayList == -1)tes.displayList = GLAllocation.generateDisplayLists(1);
		if(Tessellator.instance.isDrawing)return true;
		if(!tes.tessellationTranslated)return true;
		
		GL11.glNewList(tes.displayList, GL11.GL_COMPILE);		
		Tessellator.instance.startDrawingQuads();
		
		Tessellator.instance.setTranslation(tes.tessellationX,tes.tessellationY,tes.tessellationZ);
		
		IBlockAccess iba = renderer.blockAccess;
		renderer.blockAccess = tes;
		renderer.setRenderBounds(0,0,0,1,1,1);
		BlockSculpture sculpture = (BlockSculpture) block;
		BlockSculpture.renderBlockMeta = tes.blockMeta;
		
		blockCoord(tes.xCoord,tes.yCoord,tes.zCoord);
		
		for(int _x = 0; _x < 8; _x ++)
			for(int _y = 0; _y < 8; _y ++)
				for(int _z = 0; _z < 8; _z ++)
				{
					if(!tes.get(_x, _y, _z))continue;
					
					tes.bias(_x, _y, _z);

					int startIndex = ObfuscationReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, RAW_BUFFER_INDEX);
					renderer.renderStandardBlock( sculpture , x, y, z);
					int endIndex = ObfuscationReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, RAW_BUFFER_INDEX);
					
					subBlockCoord(_x,_y,_z);
					translateTessellator(startIndex,endIndex);
				}
		
		renderer.blockAccess = iba;
		
		if(tes.hinge > 0)
		{
			renderer.renderAllFaces = true;
			if(tes.hinge == 1)	   renderer.setRenderBounds(Mm,	mM,	mm,	MM,	Mm,	mM);
			else if(tes.hinge == 4)renderer.setRenderBounds(Mm,	mM,	Mm,	MM,	Mm,	MM);
			else if(tes.hinge == 2)renderer.setRenderBounds(mm,	mM,	Mm,	mM,	Mm,	MM);
			else if(tes.hinge == 3)renderer.setRenderBounds(mm,	mM,	mm,	mM,	Mm,	mM);
			
			renderer.renderStandardBlock(Block.planks, tes.xCoord,tes.yCoord,tes.zCoord);
		}
		
		Tessellator.instance.draw();
		Tessellator.instance.setTranslation(0, 0, 0);
		GL11.glEndList();
		
		return false;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		
		TileEntitySculpture tes = (TileEntitySculpture) world.getBlockTileEntity(x, y, z);
		if(tes.displayList >= 0)GL11.glCallList(tes.displayList);
		tes.tessellationX = Tessellator.instance.xOffset;
		tes.tessellationY = Tessellator.instance.yOffset;
		tes.tessellationZ = Tessellator.instance.zOffset;
		tes.tessellationTranslated = true;
		int light = block.getMixedBrightnessForBlock(world, x, y,z);
		if(tes.light != light)tes.needUpdate =true;
		tes.light = light;
		
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return ModMinePainter.instance.block("Sculpture").ri();
	}
	
	private int getFace(int[] buffer, int i)
	{
		float[] x = new float[3],y = new float[3],z = new float[3];
		for(int j=0;j<3;j++)
		{
			x[j] = (float) (Float.intBitsToFloat(buffer[i + 0])  - Tessellator.instance.xOffset);
			y[j] = (float) (Float.intBitsToFloat(buffer[i + 1])  - Tessellator.instance.yOffset);
			z[j] = (float) (Float.intBitsToFloat(buffer[i + 2])  - Tessellator.instance.zOffset);
			i+=8;
		}
		
		float xd = (y[1] - y[0])*(z[2] - z[1]) - (z[1] - z[0])*(y[2] - y[1]);
		if(xd > 0)return 5; else if(xd < 0)return 4;
		
		float yd = (z[1] - z[0])*(x[2] - x[1]) - (x[1] - x[0])*(z[2] - z[1]);
		if(yd > 0)return 1; else if(yd < 0)return 0;
		
		float zd = (x[1] - x[0])*(y[2] - y[1]) - (y[1] - y[0])*(x[2] - x[1]);
		if(zd > 0)return 3; else return 2;
	}
	
	private void translateTessellator(int start, int end)
	{
		int[] buffer = ObfuscationReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, RAW_BUFFER);
		
		float umin = Float.MAX_VALUE;
		float vmin = Float.MAX_VALUE;
		float umax = Float.MIN_VALUE;
		float vmax = Float.MIN_VALUE;
		
		float uWidth = umax-umin;
		float vWidth = vmax-vmin;
		float face = 0;
		
		int vert = 0;
		for(int i = start;i<end;i+=8)
		{
			if(vert % 4 == 0)
			{
				face = getFace(buffer, i);
				
				umin = Float.MAX_VALUE;
				vmin = Float.MAX_VALUE;
				umax = Float.MIN_VALUE;
				vmax = Float.MIN_VALUE;
				
				for(int j = 0;j<4;j++)
				{
					float u = Float.intBitsToFloat(buffer[i + j*8 + 3]);
					float v = Float.intBitsToFloat(buffer[i + j*8 + 4]);

					if(u>umax)umax = u;
					if(u<umin)umin = u;
					if(v>vmax)vmax = v;
					if(v<vmin)vmin = v;
				}
				
				uWidth = umax-umin;
				vWidth = vmax-vmin;
			}
			
			float x = (float) (Float.intBitsToFloat(buffer[i + 0])  - Tessellator.instance.xOffset);
			float y = (float) (Float.intBitsToFloat(buffer[i + 1])  - Tessellator.instance.yOffset);
			float z = (float) (Float.intBitsToFloat(buffer[i + 2])  - Tessellator.instance.zOffset);
			
			x-= this.x;
			y-= this.y;
			z-= this.z;
			
			x/=8f;
			y/=8f;
			z/=8f;
			
			x+= this.x;
			y+= this.y;
			z+= this.z;
			
			x+= _x/8f;
			y+= _y/8f;
			z+= _z/8f;
			
			buffer[i + 0] = Float.floatToIntBits((float)(x + Tessellator.instance.xOffset));
			buffer[i + 1] = Float.floatToIntBits((float)(y + Tessellator.instance.yOffset));
			buffer[i + 2] = Float.floatToIntBits((float)(z + Tessellator.instance.zOffset));
			
			float u = Float.intBitsToFloat(buffer[i + 3]);
			float v = Float.intBitsToFloat(buffer[i + 4]);
			
			boolean uright = u > umin;
			
			u-=umin;
			v-=vmin;
			
			u/=8f;
			v/=8f;
			
			if(face == 0 || face == 1)
			{
				u+= _x*uWidth/8f;
				v+= _z*vWidth/8f;
			}else if(face == 2 || face == 3)
			{
				u+= _x*uWidth/8f;
				v+= (7-_y)*vWidth/8f;
			}else
			{
				u += _z*uWidth/8f;
				v+= (7-_y)*vWidth/8f;
			}
			
			if(face == 2 || face == 5)
			{
				if(uright)u-=uWidth/8f;
				else u+=uWidth/8f;
			}
			u+=umin;
			v+=vmin;
			
			buffer[i + 3] = Float.floatToIntBits(u);
			buffer[i + 4] = Float.floatToIntBits(v);
			
			vert ++;
			vert%=24;
		}
		
		Field f;
		try {
			f = Tessellator.class.getDeclaredFields()[RAW_BUFFER];
			f.setAccessible(true);
			f.set(Tessellator.instance, buffer);
		} catch (Exception e) {
			Debug.dafuq();
		}
	}
	
	private void blockCoord(int x,int y,int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	private void subBlockCoord(int x,int y,int z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
}
