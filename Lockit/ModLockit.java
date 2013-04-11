package hx.Lockit;

import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import hx.utils.HyperMod;

public class ModLockit extends HyperMod
{
	public ModLockit()
	{
		this.BLOCK_BASE_ID = 3550;
		this.ITEM_BASE_ID  = 7900;

	}

	@Instance("mod_Lockit")
	public static ModLockit instance;

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
