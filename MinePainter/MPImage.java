package hx.MinePainter;

import hx.utils.Debug;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.passive.EntitySheep;

public class MPImage {
	byte pixels[] = new byte[256];
	
	public MPImage()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		fill((byte) 31);
	}
	
	public void fill(int code)
	{
		for(int i =0;i<256;i++)
			pixels[i] = (byte) code;
	}
	
	public void flood(int x,int y,int before, int after)
	{
		if(x<0 || y<0 || x>15 || y>15)return;
		if(before == -1)before = at(x,y);
		
		if(at(x,y)==before)set(x,y,after);
		else return;
		
		flood(x-1,y,before,after);
		flood(x+1,y,before,after);
		flood(x,y-1,before,after);
		flood(x,y+1,before,after);
	}
	
	public void fromByteArray(byte[] data)
	{
		DataInputStream inputStream = 
				new DataInputStream(new ByteArrayInputStream(data));

		try {
			for(int i = 0; i<128; i ++)
			{
				byte pix = inputStream.readByte();
				pixels[(i << 1)] = (byte) ((pix & 255) >> 4); 
				pixels[(i << 1) + 1] = (byte) (pix & 15);
			}
			
			for(int i = 0; i < 32; i ++)
			{
				byte pix = inputStream.readByte();
				byte index = 1;
				
				for(int j = 0;j<8;j++)
				{
					if((pix & index) != 0)pixels[i*8 + j] |= 16;
					index = (byte) (index << 1);
				}
			}
			
		} catch (IOException e) {
			// hmmm
		}
	}
	
	public byte[] toByteArray()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		
		try {
			for(int i = 0; i < 128; i ++)
			{
				byte b1 = (byte) ((pixels[i << 1] & 15 ) << 4);
				byte b2 = (byte) (pixels[(i<< 1) + 1] & 15);
				dos.writeByte(b1 | b2);
			}
			
			for(int i = 0; i < 32; i ++)
			{
				byte pix = 0;
				byte index = 1;
				
				for(int j =0;j<8;j++)
				{
					if(pixels[i*8 + j] > 15)pix |= index;
					index = (byte)(index << 1);
				}
				dos.writeByte(pix);
			}
			
		} catch (IOException e) {
		}
		
		return bos.toByteArray();
	}
	
	public int at(int x,int y)
	{
		x %= 16;
		y %= 16;
		
		return pixels[(x << 4) + y ];
	}
	
	public float[] rgb_at(int x,int y)
	{
		int code = at(x,y);
		if(code < 16)return null;
		
		return EntitySheep.fleeceColorTable[15 - (code & 15)];
	}
	
	public void set(int x,int y, int color)
	{
		x %= 16;
		y %= 16;
		
		pixels[(x << 4) + y] = (byte) color;
	}
}
