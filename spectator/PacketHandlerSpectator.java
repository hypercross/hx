package hx.spectator;

import hx.utils.PacketHandler;

public class PacketHandlerSpectator extends PacketHandler{

	@Override
	public void registerPackeTypes() {
		packet_prototypes.add(new FileRequest());
		packet_prototypes.add(new FileSegment());
		packet_prototypes.add(new FileHeader());
	}

}
