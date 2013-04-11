package hx.soundbox;

import hx.utils.PacketHandler;

public class SoundBoxPacketHandler extends PacketHandler{

	@Override
	public void registerPackeTypes() {
		this.packet_prototypes.add(new PacketPlay());
	}

	public static void sendPlayPacket(int x,int y,int z)
	{
		
	}
}
