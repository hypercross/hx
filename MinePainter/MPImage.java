package hx.MinePainter;

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
//		for(int i =0;i<256;i++)
//			pixels[i] = (byte) ( ((i%2) == ((i/16)%2)) ? 1 : 17); 
	}
	
	public void fromByteArray(byte[] data)
	{
		DataInputStream inputStream = 
				new DataInputStream(new ByteArrayInputStream(data));

		try {
			for(int i = 0; i<128; i ++)
			{
				byte pix = inputStream.readByte();
				pixels[(i << 1)] = (byte) (pix >> 4); 
				pixels[(i << 1) + 1] = (byte) (pix & 15);
				
				if(inputStream.readBoolean())
					pixels[i << 1] |= 16;
				
				if(inputStream.readBoolean())
					pixels[(i << 1) + 1] |= 16;
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
				byte b1 = (byte) (pixels[i << 1] << 4);
				byte b2 = (byte) (pixels[(i<< 1) + 1] & 15);
				
				dos.writeByte(b1 | b2);
				dos.writeBoolean(pixels[i << 1] > 15);
				dos.writeBoolean(pixels[(i<< 1) + 1] > 15);
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
