package hx.Alchemania;

import net.minecraftforge.client.MinecraftForgeClient;
import hx.Alchemania.Block.BlockAlchemyFurnaceRenderer;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	public void registerRenderings()
	{
		 MinecraftForgeClient.preloadTexture(Alchemania.MAIN_TEXTURE);
		
		Alchemania.alchemyFurnaceRI = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new BlockAlchemyFurnaceRenderer());
        
	}
}
