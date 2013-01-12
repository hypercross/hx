package hx.MinePainter;

import hx.utils.HyperMod;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;

@Mod(modid = "mod_MinePainter", name = "MinePainter", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class ModMinePainter extends HyperMod{

	public ModMinePainter()
	{
		this.BLOCK_BASE_ID = 3500;
		this.ITEM_BASE_ID  = 9700;
		this.MAIN_TEXTURE = "/hx/MinePainter/img.png";
		addBlocks(new String[]{"Canvas", "Sculpture"});
		addItems(new String[]{"Canvas"});
	}
	
	@Instance("mod_MinePainter")
	public static ModMinePainter instance;
	
	@PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }
	
	@Init
    public void load(FMLInitializationEvent event)
    {
        super.load(event);
    }
}
