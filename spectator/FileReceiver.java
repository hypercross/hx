package hx.spectator;

import java.io.File;
import java.io.IOException;

import net.minecraft.server.MinecraftServer;

public class FileReceiver {

	byte[] buffer;
	int numReceived;
	char[] file_name;
	
	public FileReceiver(int size, char[] file_name)
	{
		buffer = new byte[size];
		numReceived = 0;
		this.file_name = file_name;
	}
	
	public void receive(byte[] data)
	{
		System.arraycopy(data, 0, buffer, numReceived, data.length);
		numReceived += data.length;
	}
	
	public boolean finished()
	{
		return numReceived >= buffer.length;
	}

	public void save() {
		try {
			getSaveFile().getParentFile().mkdirs();
			getSaveFile().createNewFile();
			FileIO.saveTo(getSaveFile(), buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String stat()
	{
		return "need " + buffer.length + " has " + numReceived;
	}
	
	public File getSaveFile ()
	{
		return MinecraftServer.getServer().getFile("/spectator/" + new String(file_name));
	}
}
