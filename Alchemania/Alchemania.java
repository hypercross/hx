package hx.Alchemania;

import org.lwjgl.input.Mouse;

import hx.Alchemania.Block.BlockABS;
import hx.Alchemania.Block.BlockAlchemyFurnace;
import hx.Alchemania.Block.TileEntityABS;
import hx.Alchemania.Block.TileEntityAlchemyFurnace;
import hx.Alchemania.Effect.AlchemaniaEffect;
import hx.Alchemania.Item.ContainerHandDispenser;
import hx.Alchemania.Item.GuiHandDispenser;
import hx.Alchemania.Item.ItemFan;
import hx.Alchemania.Item.ItemHandDispensor;
import hx.Alchemania.Item.ItemIngredientPowder;
import hx.Alchemania.Item.ItemMortarNPestle;
import hx.Alchemania.Item.ItemPill;
import hx.Alchemania.Item.ItemSolution;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="Alchemania", name="Alchemania", version="0.0.1")
@NetworkMod
public class Alchemania implements IGuiHandler{
	
	@SidedProxy(clientSide = "hx.Alchemania.ClientProxy", serverSide = "hx.Alchemania.CommonProxy")
    public static CommonProxy proxy;
	public static String MAIN_TEXTURE = "/hx/Alchemania/img.png";
	
	public static Item ingredientPowder;
	public static int ingredientPowderID;
	public static Item mortarNPestle;
	public static int mortarNPestleID;
	public static Item pill;
	public static int pillID;
	public static Item solution;
	public static int solutionID;
	public static Item handDispenser;
	public static int handDispenserID;
	public static Item fan;
	public static int fanID;
	
	public static int alchemyFurnaceID;
	public static int alchemyFurnaceRI;
	public static Block blockAlchemyFurnace;
	
	public static int HandDispenserGUI = 0;
	
	public static int[] dippables = {Item.arrow.shiftedIndex,
									Item.pickaxeSteel.shiftedIndex,
									Item.pickaxeWood.shiftedIndex,
									Item.pickaxeDiamond.shiftedIndex,
									Item.pickaxeStone.shiftedIndex,
									Item.pickaxeGold.shiftedIndex,
									Item.axeSteel.shiftedIndex,
									Item.axeWood.shiftedIndex,
									Item.axeDiamond.shiftedIndex,
									Item.axeStone.shiftedIndex,
									Item.axeGold.shiftedIndex,
									Item.shovelSteel.shiftedIndex,
									Item.shovelWood.shiftedIndex,
									Item.shovelDiamond.shiftedIndex,
									Item.shovelStone.shiftedIndex,
									Item.shovelGold.shiftedIndex,
									Item.swordSteel.shiftedIndex,
									Item.swordWood.shiftedIndex,
									Item.swordDiamond.shiftedIndex,
									Item.swordStone.shiftedIndex,
									Item.swordGold.shiftedIndex,
									Item.hoeSteel.shiftedIndex,
									Item.hoeWood.shiftedIndex,
									Item.hoeDiamond.shiftedIndex,
									Item.hoeStone.shiftedIndex,
									Item.hoeGold.shiftedIndex};
	
	@Instance("Alchemania")
	public static Alchemania instance;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent ev)
	{
		AlchemaniaEffect.load();
		IngredientPowderRecipe.load(ev);
		
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		config.load();
		
		ingredientPowderID 	= config.getItem("Powder", 5301).getInt();
		mortarNPestleID		= config.getItem("Mortar and Pestle", 5302).getInt();
		pillID				= config.getItem("Pill", 5303).getInt();
		solutionID			= config.getItem("Solution", 5304).getInt();
		handDispenserID		= config.getItem("Hand Dispenser", 5305).getInt();
		fanID		= config.getItem("Fan", 5306).getInt();
		alchemyFurnaceID	= config.getBlock("Alchemy Furnace", 601).getInt();
		dippables			= config.get("general", "Tools You Can Apply Solution To", dippables).getIntList();
		
		config.save();
	}
	
	@Init
	public void load(FMLInitializationEvent ev)
	{
		ingredientPowder = new ItemIngredientPowder(ingredientPowderID);
		mortarNPestle = new ItemMortarNPestle(mortarNPestleID);
		pill			= new ItemPill(pillID);
		solution		= new ItemSolution(solutionID);
		handDispenser 	= new ItemHandDispensor(handDispenserID);
		fan				= new ItemFan(fanID);
		blockAlchemyFurnace = new BlockAlchemyFurnace(alchemyFurnaceID);
		
		LanguageRegistry.addName(ingredientPowder, "Ingredient Powder");
		ModLoader.addName(ingredientPowder, "zh_CN", "药粉");
		
		LanguageRegistry.addName(mortarNPestle, "Mortar");
		ModLoader.addName(mortarNPestle, "zh_CN", "药杵和药臼");
		
		LanguageRegistry.addName(pill, "Pill");
		ModLoader.addName(pill, "zh_CN", "药丸");
		
		LanguageRegistry.addName(solution, "Solution");
		ModLoader.addName(solution, "zh_CN", "药剂");
		
		LanguageRegistry.addName(blockAlchemyFurnace, "Alchemy Furnace");
		ModLoader.addName(blockAlchemyFurnace, "zh_CN", "炼药炉");
		
		LanguageRegistry.addName(handDispenser, "Handheld Dispenser");
		ModLoader.addName(handDispenser, "zh_CN", "便携发射器");
		
		LanguageRegistry.addName(fan, "Fan");
		ModLoader.addName(fan, "zh_CN", "扇子");
		
		GameRegistry.registerBlock(blockAlchemyFurnace, "alchemyFurnace");
		GameRegistry.registerTileEntity(TileEntityAlchemyFurnace.class, "alchemyFurnace");
		
		GameRegistry.addRecipe(new IngredientPowderRecipe());
		GameRegistry.addRecipe(new PowderMixRecipe());
		GameRegistry.addRecipe(new PowderPillRecipe());
		GameRegistry.addRecipe(new ApplySolutionRecipe());
		
		GameRegistry.addShapelessRecipe(new ItemStack(mortarNPestle), 
				new Object[]{
			new ItemStack(Item.flint), 
			new ItemStack(Item.flint),
			new ItemStack(Item.bowlEmpty)});
		
		GameRegistry.addShapelessRecipe(new ItemStack(handDispenser), 
				new Object[]{
			new ItemStack(Block.lever), 
			new ItemStack(Block.dispenser)});		
		
		GameRegistry.addRecipe(new ItemStack(blockAlchemyFurnace),
				"XXX","XYX","XXX",
				'X',new ItemStack(Item.goldNugget), 
				'Y', new ItemStack(Block.stoneOvenIdle));
		
		GameRegistry.addRecipe(new ItemStack(fan),
				"XXX","XXX"," Y ",
				'X',new ItemStack(Block.cloth), 
				'Y', new ItemStack(Item.stick));
		
		NetworkRegistry.instance().registerGuiHandler(this, this);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		
		Block.blocksList[117] = null;
		Block cbs = new BlockABS(117);
		cbs.setHardness(0.5F).setLightValue(0.125F).setBlockName("brewingStand").setRequiresSelfNotify();
		TileEntity.addMapping(TileEntityABS.class, "ABS");
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if(side.isClient())
		{
			TickRegistry.registerTickHandler(new GUITickHandler(), Side.CLIENT);
		}
		proxy.registerRenderings();
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if(ID == HandDispenserGUI)
			return new ContainerHandDispenser(player.inventory,player.inventory.getCurrentItem());
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if(ID == HandDispenserGUI)
			return new GuiHandDispenser(player.inventory,null);
		return null;
	}
	
	public static boolean dippable(int id)
	{
		for(int did : dippables)
			if(did == id)return true;
		return false;
	}
}
