package hx.MinePainter;

import java.lang.reflect.Field;

import hx.utils.Debug;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class BlockSculptureRenderer implements ISimpleBlockRenderingHandler{
	
	private int x,y,z; //working block coordinates
	private int _x,_y,_z; //subblock coordinates

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		
		renderer.renderBlockAsItem(((BlockSculpture)block).materialBlock(metadata),1,1);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {

		TileEntitySculpture tes = (TileEntitySculpture) world.getBlockTileEntity(x, y, z);
		IBlockAccess iba = renderer.blockAccess;
		renderer.blockAccess = tes;
		renderer.setRenderBounds(0,0,0,1,1,1);
		BlockSculpture sculpture = (BlockSculpture) block;
		BlockSculpture.renderBlockMeta = tes.blockMeta;
		
		blockCoord(x,y,z);
		
		for(int _x = 0; _x < 8; _x ++)
			for(int _y = 0; _y < 8; _y ++)
				for(int _z = 0; _z < 8; _z ++)
				{
					if(!tes.get(_x, _y, _z))continue;
					
					tes.bias(_x, _y, _z);

					int startIndex = ObfuscationReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, 22);
					renderer.renderStandardBlock( sculpture , x, y, z);
					int endIndex = ObfuscationReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, 22);
					
					subBlockCoord(_x,_y,_z);
					translateTessellator(startIndex,endIndex);
				}
		
		renderer.blockAccess = iba;
		
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return ModMinePainter.instance.block("Sculpture").ri();
	}
	
	private void translateTessellator(int start, int end)
	{
//		Debug.dafuq("called " + start + " to " + end);
		int[] buffer = ObfuscationReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, 12);
		
		float umin = Float.MAX_VALUE;
		float vmin = Float.MAX_VALUE;
		float umax = Float.MIN_VALUE;
		float vmax = Float.MIN_VALUE;
		
		for(int i = 0;i<4;i++)
		{
			float u = Float.intBitsToFloat(buffer[start + i*8 + 3]);
			float v = Float.intBitsToFloat(buffer[start + i*8 + 4]);
			
			if(u>umax)umax = u;
			if(u<umin)umin = u;
			if(v>vmax)vmax = v;
			if(v<vmin)vmin = v;
		}
		
		float uWidth = umax -umin;
		float vWidth = vmax-vmin;
		
		int vert = 0;
		for(int i = start;i<end;i+=8)
		{
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
			
			u-=umin;
			v-=vmin;
			
			u/=8f;
			v/=8f;
			
			int face = vert/4;
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
			
			u+=umin;
			v+=vmin;
			
			buffer[i + 3] = Float.floatToIntBits(u);
			buffer[i + 4] = Float.floatToIntBits(v);
			
			vert ++;
			vert%=24;
		}
		
		Field f;
		try {
			f = Tessellator.class.getDeclaredField("rawBuffer");
			f.setAccessible(true);
			f.set(Tessellator.instance, buffer);
		} catch (Exception e) {
			//never mind then
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
