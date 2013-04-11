package hx.survivalist;

import net.minecraft.client.renderer.entity.RenderItem;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends Proxy{

	public void doYourWork()
	{
		TickRegistry.registerTickHandler(new FactorRenderTickHandler(), Side.CLIENT);
		TickRegistry.registerTickHandler(new FactorWorldTickHandler(false), Side.CLIENT);
	}
}
