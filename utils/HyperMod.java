package hx.utils;

import java.lang.reflect.Field;
import java.util.TreeMap;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions( {"hx.utils"})
public class HyperMod
{
    public int BLOCK_BASE_ID;

    public int ITEM_BASE_ID;

    public String MAIN_TEXTURE = null;

    private TreeMap<String, BlockLoader> blockLoaders = new TreeMap<String, BlockLoader>();
    private TreeMap<String, ItemLoader> itemLoaders = new TreeMap<String, ItemLoader>();

    public void addBlocks(String... names)
    {
        for (String name : names)
        {
            blockLoaders.put(name, new BlockLoader(this, name));
            FMLLog.getLogger().fine("added block..." + name);
        }
    }
    
    public int availableItemId()
    {
    	int id = ITEM_BASE_ID;
    	while(true)
    	{
    		boolean used = false;
    		for(ItemLoader il : itemLoaders.values())
    			if(il.id() == id)
    			{
    				used = true;
    				break;
    			}
    		if(!used)return id;
    		id++;
    	}
    }
    
    public int availableBlockId()
    {
    	int id = BLOCK_BASE_ID;
    	while(true)
    	{
    		boolean used = false;
    		for(BlockLoader bl : blockLoaders.values())
    			if(bl.id() == id)
    			{
    				used = true;
    				break;
    			}
    		if(!used)return id;
    		id++;
    	}
    }

    public BlockLoader block(String name)
    {
        return blockLoaders.get(name);
    }

    public ItemLoader item(String name)
    {
        return itemLoaders.get(name);
    }

    public void addItems(String... items)
    {
        for (String name : items)
        {
            itemLoaders.put(name, new ItemLoader(this, name));
        }
    }

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        for (BlockLoader bl : blockLoaders.values())
        {
            bl.preInit(config);
        }
        
        for (BlockLoader bl : blockLoaders.values())
        	if(bl.blockID == -1)bl.blockID = this.availableBlockId();

        for (ItemLoader il : itemLoaders.values())
        {
            il.preInit(config);
        }
        
        for (ItemLoader il : itemLoaders.values())
        	if(il.itemID == -1)il.itemID = this.availableItemId();

        for (Field f : this.getClass().getFields())
        {
            Configurable anno = f.getAnnotation(Configurable.class);

            if (anno != null)
            {
                try
                {
                    Class type = f.getType();

                    if (type.equals(int.class))
                    {
                        f.set(this, config.get(anno.value(), f.getName(), f.getInt(this)).getInt());
                    }
                    else if (type.equals(double.class))
                    {
                        f.set(this, config.get(anno.value(), f.getName(), f.getDouble(this)).getDouble(f.getDouble(this)));
                    }
                    else if (type.equals(boolean.class))
                    {
                        f.set(this, config.get(anno.value(), f.getName(), f.getBoolean(this)).getBoolean(f.getBoolean(this)));
                    }
                    else if (f.getGenericType().equals(int[].class))
                    {
                        f.set(this, config.get(anno.value(), f.getName(), (int[]) f.get(this)).getIntList());
                    }else if(f.getGenericType().equals(String[].class))
                    {
                    	f.set(this, config.get(anno.value(), f.getName(), (String[])f.get(this)).getStringList());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        config.save();
    }

    @Init
    public void load(FMLInitializationEvent event)
    {
        for (BlockLoader bl : blockLoaders.values())
        {
            bl.load();
        }

        for (ItemLoader il : itemLoaders.values())
        {
            il.load();
        }
        registerRendering();
    }
    
    public void registerRendering()
    {
    	if(FMLCommonHandler.instance().getSide().isServer())return;
    	for (BlockLoader bl : blockLoaders.values())
        {
            bl.registerRenderers();
        }
    	for (ItemLoader il : itemLoaders.values())
    	{
    		il.registerRenderer();
    	}
    	if(MAIN_TEXTURE != null)
    		MinecraftForgeClient.preloadTexture(this.MAIN_TEXTURE);
    }

    public void registerRendering(Object proxy)
    {
        registerRendering();
    }
}