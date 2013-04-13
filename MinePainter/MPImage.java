package hx.MinePainter;

import hx.utils.Debug;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class MPImage {
	BufferedImage img = new BufferedImage(16,16, BufferedImage.TYPE_4BYTE_ABGR);
	Graphics g = img.createGraphics();
	
	public MPImage()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		//fill(-1);
	}
	
	public MPImage(byte[] array)
	{
		this.fromByteArray(array);
	}
	
	public void fill(byte b)
	{
		Debug.dafuq();
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		Arrays.fill(pixels, b);
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
	
	public boolean fromByteArray(byte[] data)
	{
		DataInputStream inputStream = 
				new DataInputStream(new ByteArrayInputStream(data));

		try
		{
			BufferedImage newimg = ImageIO.read(inputStream);
			if(newimg == null)throw new Exception("oops cant read img");
			
			img = newimg;
			g = img.createGraphics();
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public byte[] toByteArray()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		
		try {
			ImageIO.write(img, "png", bos);
		} catch (IOException e) {
		}
		
		return bos.toByteArray();
	}
	
	public int at(int x,int y)
	{
		while(x<0)x+=16;
		while(y<0)y+=16;
		x %= 16;
		y %= 16;
		
		return img.getRGB(x, y);
	}
	
	public float[] rgba_at(int x,int y)
	{	
		int color = at(x,y);
		
		return new float[]{ red(color), green(color), blue(color), alpha(color)};
	}
	
	private float red(int x)
	{
		return ((x >> 16) & 0xff) / 256f;
	}
	
	private float green(int x)
	{
		return ((x >> 8) & 0xff) / 256f;
	}
	
	private float blue(int x)
	{
		return (x & 0xff)/ 256f;
	}
	
	private float alpha(int x)
	{
		return ((x >> 24) & 0xff)/256f;
	}
	
	public void set(int x,int y, int color)
	{
		while(x<0)x+=16;
		while(y<0)y+=16;
		x %= 16;
		y %= 16;
		
		img.setRGB(x, y, color);
	}
}
