package hx.Lockit;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import hx.utils.Configurable;
import hx.utils.HyperMod;

@Mod(modid = "mod_Lockit", name = "Lockit", version = "0.1.2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,packetHandler = PacketHandler.class,
channels ={"Lockit_Monument"}
)
public class ModLockit extends HyperMod
{
    @Configurable("Protection")
    public static boolean protectSky = true;

    @Configurable("Range")
    public static int defaultPlotRange = 5;
    
    @Configurable("Range")
    public static int maxPlotRange = 10;

    @Configurable("Range")
    public static int defaultLandmarkRange = 50;
    
    @Configurable("Range")
    public static int maxLandmarkRange = 100;
    
    @Configurable("Range")
    public static int defaultMilestoneRange = 10;
    
    @Configurable("Range")
    public static int maxMilestoneRange = 20;
    
    @Configurable("Display")
    public static int particleDensity = 100;

    @Configurable("Protection")
    public static int bannedItems[] = {
    	Item.sign.shiftedIndex, 
    	Item.cake.shiftedIndex,
    	Item.bed.shiftedIndex,
    	Item.silk.shiftedIndex,
    	Item.bucketEmpty.shiftedIndex,
    	Item.bucketWater.shiftedIndex,
    	Item.bucketLava.shiftedIndex};
    

    @Configurable("Protection")
    public static int lockables[] = {Block.chest.blockID, Block.anvil.blockID, Block.enderChest.blockID};
    
    @Configurable("Protection")
    public static int bannedNearLock[] = {};
    
    public MonumentManager monuments;

    public ModLockit()
    {
        this.BLOCK_BASE_ID = 501;
        this.ITEM_BASE_ID = 5001;
        this.MAIN_TEXTURE = "/hx/Lockit/lock.png";
        addBlocks(new String[] {"Lock", "HangedLock", "Engraving", "Monument"});
        addItems(new String[] {"Key", "KeyChain", "SkeletonKey", "EnderWallet", "Chisel", "QueryTool"});
    }

    // The instance of your mod that Forge uses.
    @Instance("mod_Lockit")
    public static ModLockit instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide = "hx.Lockit.ClientProxy", serverSide = "hx.Lockit.CommonProxy")
    public static CommonProxy proxy;

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Init
    public void load(FMLInitializationEvent event)
    {
        super.load(event);
        GameRegistry.addRecipe(new RecipesKeyChain());
        GameRegistry.addRecipe(new RecipesDupKey());
        GameRegistry.addRecipe(new RecipesKeyDyes());
        NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        
        monuments = new MonumentManager();
        
        GameRegistry.addRecipe(new ItemStack(block("Lock").block()), " x ", "xyx", " x ",'x', new ItemStack(Block.cobblestone),'y', new ItemStack(Item.ingotIron));
        ModLoader.addName(new ItemStack(block("Lock").block()), "zh_CN", "锁");
        GameRegistry.addRecipe(new ItemStack(block("HangedLock").block()), "yxy", "yyy",'x', new ItemStack(Item.ingotIron),'y', new ItemStack(Item.goldNugget));
		ModLoader.addName(new ItemStack(block("HangedLock").block()), "zh_CN", "挂锁");
		ModLoader.addName(new ItemStack(block("Monument").block()), "zh_CN", "碑石");
		ModLoader.addName(new ItemStack(block("Engraving").block()), "zh_CN", "浮刻");
		
		ModLoader.addName(new ItemStack(item("Key").item()), "zh_CN", "钥匙");
		ModLoader.addName(new ItemStack(item("SkeletonKey").item()), "zh_CN", "万能钥匙");
		ModLoader.addName(new ItemStack(item("KeyChain").item()), "zh_CN", "钥匙串");
		GameRegistry.addShapelessRecipe(new ItemStack(item("EnderWallet").item()), new Object[]{new ItemStack(Item.enderPearl),new ItemStack(Item.leather),new ItemStack(Item.silk)});
		ModLoader.addName(new ItemStack(item("EnderWallet").item()), "zh_CN", "末影口袋");
		ModLoader.addName(new ItemStack(item("Chisel").item()), "zh_CN", "凿子");
		
		GameRegistry.addRecipe(new ItemStack(item("Chisel").item()), 
				"X","Y",
				'X',new ItemStack(Block.stone),
				'Y',new ItemStack(Item.stick));
		
		GameRegistry.addRecipe(new ItemStack(block("Monument").block()), 
				"YYY","YXY","XXX",
				'X', new ItemStack(Block.stone),
				'Y', new ItemStack(Block.cobblestone));
		
    }

    @Mod.ServerStopping
    public void ServerStopping(FMLServerStoppingEvent event)
    {
        Keybase.clear();
    }
}