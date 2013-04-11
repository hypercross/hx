package hx.soundbox;

import hx.utils.HyperMod;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "mod_soundbox", name = "Sound Box", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class SoundBox extends HyperMod{

		public SoundBox()
		{
			this.BLOCK_BASE_ID = 3520;
			this.ITEM_BASE_ID = 9720;
			this.addBlocks("TransmittedNote");
		}
		
		@Instance("mod_soundbox")
		public static SoundBox instance;
		
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
