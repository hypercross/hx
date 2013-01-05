package hx.utils;

import java.util.HashMap;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
public class ItemLoader
{
    private int itemID;
    private Item theItem;
    private String name;
    private HyperMod mod;
    //	//accessor
    //============================================================================

    public int id()
    {
        return itemID;
    }

    public Item item()
    {
        return theItem;
    }

    //constructor
    //============================================================================

    public ItemLoader(HyperMod mod, String name)
    {
        this.mod = mod;
        this.name = name;
    }

    //steps
    //============================================================================

    public void preInit(Configuration config)
    {
        itemID = config.getItem(name, mod.ITEM_BASE_ID + (mod.ITEM_LOADED_ID++)).getInt();
        FMLLog.getLogger().finest("Using " + itemID + " for item " + name);
    }

    public void load()
    {
        try
        {
            Class itemClass = Class.forName(mod.getClass().getPackage().getName() + "." + "Item" + name);
            theItem = (Item) itemClass.getConstructor(int.class).newInstance(itemID);
            String dispName = "";

            for (String word : name.split("(?<!^)(?=[A-Z])"))
            {
                dispName += word + " ";
            }

            dispName = dispName.substring(0, dispName.length() - 1);
            LanguageRegistry.addName(theItem, dispName);
            FMLLog.getLogger().finest("Item " + name + " registered.");
        }
        catch (Exception e)
        {
            FMLLog.getLogger().severe("Item " + name + " class NOT FOUND!");
        }
    }
}
