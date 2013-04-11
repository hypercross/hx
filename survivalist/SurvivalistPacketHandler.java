package hx.survivalist;

import cpw.mods.fml.common.FMLCommonHandler;
import hx.utils.PacketHandler;

public class SurvivalistPacketHandler extends PacketHandler{

	@Override
	public void registerPackeTypes() {
		this.packet_prototypes.add(new FactorUpdate());
		this.packet_prototypes.add(new EffectEnforcing());
	}

}
