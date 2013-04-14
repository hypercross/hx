package hx.MinePainter;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import hx.utils.Debug;
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
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "mod_MinePainter", name = "MinePainter", version = "0.1.2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
channels={"ModMinePainter"}, packetHandler = PacketHandler.class)
public class ModMinePainter extends HyperMod{

	public ModMinePainter()
	{
		this.BLOCK_BASE_ID = 3500;
		this.ITEM_BASE_ID  = 9700;
		addBlocks("Canvas", "Sculpture");
		addItems("Canvas","SculpturePiece", "SculptureCover", "SculptureBar", "Schematic", 
				"IronChisel", "DiamondChisel", "StoneChisel", "Hinge", "Palette", "Brush", "BrushSmall");
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
        		
        if(item("IronChisel").item() != null)
        	GameRegistry.addRecipe(new ItemStack(item("IronChisel").item()),
            		"X "," Y",
            		'X', new ItemStack(Item.ingotIron),
            		'Y', new ItemStack(Item.stick));
        
        if(item("StoneChisel").item() != null)
        	GameRegistry.addRecipe(new ItemStack(item("StoneChisel").item()),
            		"X "," Y",
            		'X', new ItemStack(Block.cobblestone),
            		'Y', new ItemStack(Item.stick));
        
        if(item("DiamondChisel").item() != null)
        	GameRegistry.addRecipe(new ItemStack(item("DiamondChisel").item()),
            		"X "," Y",
            		'X', new ItemStack(Item.diamond),
            		'Y', new ItemStack(Item.stick));
        
        if(item("Schematic").item() != null)
        	GameRegistry.addShapelessRecipe(new ItemStack(item("Schematic").item()), 
        			new Object[]{ new ItemStack(Item.paper), new ItemStack(Item.paper), new ItemStack(Item.paper), 
        		new ItemStack(Item.dyePowder, 3, 4)});
        
        if(item("Hinge").item() != null)
        	GameRegistry.addRecipe(new ItemStack(item("Hinge").item()),
            		"X","Y","X",
            		'X', new ItemStack(Item.ingotIron),
            		'Y', new ItemStack(Item.stick));
        
        if(FMLCommonHandler.instance().getSide().isClient())
        {
        	MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
        }
        
        if(ItemPalette.instance != null && item("StoneChisel").item() != null)
        {
        	GameRegistry.addShapelessRecipe(new ItemStack(ItemPalette.instance), 
        			new Object[]{new ItemStack(Block.planks), new ItemStack(item("StoneChisel").item())});
        	GameRegistry.addRecipe(new RecipePalette());
        }
        
        if(ItemBrush.instance != null)
        {
        	GameRegistry.addRecipe(new ItemStack(ItemBrush.instance), 
        			"XX ", "XY ", "  Z",
        			'X', new ItemStack(Block.cloth),
        			'Y', new ItemStack(Item.stick),
        			'Z', new ItemStack(Item.dyePowder,1, 1));
        }
        
        if(ItemBrushSmall.instance != null)
        {
        	GameRegistry.addRecipe(new ItemStack(ItemBrushSmall.instance), 
        			"X  ", " Y ", "  Z",
        			'X', new ItemStack(Block.cloth),
        			'Y', new ItemStack(Item.stick),
        			'Z', new ItemStack(Item.dyePowder,1, 1));
        }
        
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}
