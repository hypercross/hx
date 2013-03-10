package hx.MinePainter;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import hx.utils.HyperMod;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;

@Mod(modid = "mod_MinePainter", name = "MinePainter", version = "0.0.2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class ModMinePainter extends HyperMod{

	public ModMinePainter()
	{
		this.BLOCK_BASE_ID = 3500;
		this.ITEM_BASE_ID  = 9700;
		addBlocks("Canvas", "Sculpture");
		addItems("Canvas","SculpturePiece", "SculptureCover", "SculptureBar", "Schematic");
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
        GameRegistry.addRecipe(new RecipeSculptureScrap());
        
        if(item("Canvas").item() != null)
        GameRegistry.addRecipe(new ItemStack(item("Canvas").item()),
        		"XXX","XXX",
        		'X', new ItemStack(Block.cloth));
        		
        
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}
