package hx.survivalist;

import hx.utils.Configurable;
import hx.utils.HyperMod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import net.minecraft.block.Block;
import net.minecraft.command.CommandHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "mod_survivalist", name = "Survivalist", version = "0.0.6")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
channels = {"factor_update", "effect_enforcing"}, packetHandler = SurvivalistPacketHandler.class)
public class ModSurvivalist extends HyperMod{

	@Configurable
	public boolean debug_mode;

	@Configurable("max value")
	public int stamina_max = 300;

	@Configurable("max value")
	public int thirst_max = 8000;

	@Configurable("max value")
	public int health_max = 72000;

	@Configurable("max value")
	public int sleep_max = 36000;
	
	@Configurable("factor switch")
	public boolean enable_stamina = true;
	
	@Configurable("factor switch")
	public boolean enable_thirst = true;
	
	@Configurable("factor switch")
	public boolean enable_sleep = true;
	
	@Configurable("factor switch")
	public boolean enable_health = true;

	public ModSurvivalist()
	{
		this.BLOCK_BASE_ID = 3510;
		this.ITEM_BASE_ID  = 9720;
		this.addItems("Drink", "WoodenBed");
		this.addBlocks("WoodenBed", "Table");
	}

	@Instance("mod_survivalist")
	public static ModSurvivalist instance;

	@SidedProxy(clientSide="hx.survivalist.ClientProxy", serverSide = "hx.survivalist.Proxy")
	public static Proxy proxy;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);

		File drinks = new File(event.getModConfigurationDirectory(), "survivalist_drinks.cfg");

		try
		{
			if(!drinks.exists())
			{
				InputStream resStreamIn = getClass().getResourceAsStream("/hx/survivalist/survivalist_drinks.cfg");
				OutputStream resStreamOut = new FileOutputStream(drinks);
				int readBytes;
				byte[] buffer = new byte[4096];
				while ((readBytes = resStreamIn.read(buffer)) > 0) {
					resStreamOut.write(buffer, 0, readBytes);
				}
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(drinks), "UTF8"));

			int index = -1;
			int eff_id = 0;
			while(true)
			{
				String line = in.readLine();
				if(line == null)break;
				line = line.replace('\t', ' ');
				if(line.trim().startsWith("#"))continue;
				if(line.trim().isEmpty())continue;

				String[] parts = line.split(",");

				for(String part : parts)
				{
					String[] field_n_value = part.split(":");

					if(field_n_value.length > 2)throw new Exception("malformated drink config !");

					if(field_n_value.length == 2)
					{
						String field = field_n_value[0].trim();
						String value = field_n_value[1].trim();

						if(field.equals("color"))ItemDrink.color[index] = Integer.parseInt(value.substring(2), 16);
						else if(field.equals("name"))
						{
							ItemDrink.names[++index] = value;
							ItemDrink.usedNum++;
							eff_id = 0;
						}else if(field.equals("shaped"))
						{
							if(!value.contains("="))value += "= 1 " + index;
							else value += " " + index;
							RecipeDrink.shaped(value);
						}else if(field.equals("shapeless"))
						{
							if(!value.contains("="))value += "= 1 " + index;
							else value += " " + index;
							RecipeDrink.shapeless(value);
						}else if(field.equals("smelt"))
						{
							if(!value.contains("="))value += "= 1 " + index;
							else value += " " + index;
							RecipeDrink.smelt(value);
						}else if(field.equals("giveback"))
						{
							if(value.contains("*"))
							{
								ItemDrink.cascadedCrafting[index] = true;
								value = value.replaceAll("\\*", "").trim();
							}
							RecipeDrink.giveback(index + ":" + value);
						}
						else if(field.equals("icon"))
						{
							if(value.contains("*"))
							{
								ItemDrink.drawCustomContainer[index] = true;
								value = value.replaceAll("\\*", "");
							}
							if(value.equals("true"))value = ItemDrink.names[index].replaceAll("\\s+", "_").toLowerCase();
							ItemDrink.useCustomIcons[index] = value.replaceAll("\\s+", "_").toLowerCase();
						}else if(field.equals("eaten"))
						{
							ItemDrink.eaten[index] = Boolean.valueOf(value);
						}else if(field.equals("dropped"))
						{
							RecipeDrink.drop(index, Integer.valueOf(value));
						}
					}else
						ItemDrink.effects[index][eff_id++] = UseEffect.fromString(field_n_value[0]);
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Init
	public void load(FMLInitializationEvent event)
	{
		super.load(event);
		proxy.doYourWork();

		TickRegistry.registerTickHandler(new FactorWorldTickHandler(true), Side.SERVER);
		GameRegistry.registerPlayerTracker(new PlayerStatTracker());
		MinecraftForge.EVENT_BUS.register(new EventHandler());

		RecipeDrink.register();
		GameRegistry.addRecipe(new ItemStack(ItemWoodenBed.instance), "XXY", 'X', new ItemStack(Block.planks), 'Y', new ItemStack(Block.cloth));
		GameRegistry.addRecipe(new ItemStack(BlockTable.instance), " Y ", "XXX","X X", 'X', new ItemStack(Block.planks), 'Y', new ItemStack(Item.dyePowder,1,1));

	}

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		CommandHandler commandManager = (CommandHandler)event.getServer().getCommandManager();
		commandManager.registerCommand(new CommandSetStat());
	}
}
