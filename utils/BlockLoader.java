package hx.utils;

import java.util.HashMap;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.common.Configuration;

public class BlockLoader
{
    public int blockID, blockRI;
    private Block theBlock;
    private String name;
    private HyperMod mod;

    //accessor
    //============================================================================

    public int id()
    {
        return blockID;
    }

    public int ri()
    {
        return blockRI;
    }

    public Block block()
    {
        return theBlock;
    }

    //constructor
    //============================================================================

    public BlockLoader(HyperMod mod, String name)
    {
        this.mod = mod;
        this.name = name;
    }

    //loading steps
    //============================================================================

    public void preInit(Configuration config)
    {
        blockID = config.getBlock(name, mod.availableBlockId()).getInt();
        FMLLog.getLogger().finest("Using " + blockID + " for block " + name);
    }

    public void load()
    {
        try
        {
        	if(blockID<=0)
        	{
        		FMLLog.getLogger().fine("Block " + name + "disabled through config.");
        		return;
        	}
        	String pathname = mod.getClass().getName();
        	pathname = pathname.substring(0,pathname.lastIndexOf("."));
        	FMLLog.getLogger().fine(pathname + "." + "Block" + name);
            Class blockClass = Class.forName(pathname + "." + "Block" + this.name);
            theBlock = (Block) blockClass.getConstructor(int.class).newInstance(blockID);
            GameRegistry.registerBlock(theBlock);
            String dispName = "";

            for (String word : name.split("(?<!^)(?=[A-Z])"))
            {
                dispName += word + " ";
            }

            dispName = dispName.substring(0, dispName.length() - 1);
            LanguageRegistry.addName(theBlock, dispName);
            FMLLog.getLogger().finest("Block " + name + " registered.");

            try
            {
                Class tileEntityClass = Class.forName(pathname + "." + "TileEntity" + name);
                GameRegistry.registerTileEntity(tileEntityClass, "tileEntity" + name);
                FMLLog.getLogger().finest("Tile entity " + name + " registered.");
            }
            catch (Exception e)
            {
                FMLLog.getLogger().fine("Tile Entity Definition Not Found for " + name);
            }
        }
        catch (Exception e)
        {
            FMLLog.getLogger().severe("Block class NOT FOUND for " + name + "!");
            
            e.printStackTrace();
        }
    }
    
    public void registerRenderers()
    {
    	if(blockID<=0)return;
    	String pathname = mod.getClass().getName();
    	pathname = pathname.substring(0,pathname.lastIndexOf("."));
    	if(FMLCommonHandler.instance().getSide().isServer())return;
    	 try
         {
             Class tileEntity = Class.forName(pathname + "." + "TileEntity" + name);
             Class tileEntityRenderer = Class.forName(pathname + "." + "TileEntity" + name + "Renderer");
             ClientRegistry.bindTileEntitySpecialRenderer(tileEntity, (TileEntitySpecialRenderer) tileEntityRenderer.newInstance());
             FMLLog.getLogger().finest("Tile entity renderer " + name + " registered.");
         }
         catch (Exception e)
         {
             FMLLog.getLogger().fine("No Tile Entity or its renderer for " + name);
         }

         try
         {
             Class blockRenderer = Class.forName(pathname + "." + "Block" + name + "Renderer");
             this.blockRI = RenderingRegistry.getNextAvailableRenderId();
             RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler) blockRenderer.newInstance());
             FMLLog.getLogger().finest("Block renderer" + name + " registered.");
         }
         catch (Exception e)
         {
             FMLLog.getLogger().fine("No special renderer for " + name);
         }
    }

    public void registerRenderers(Object proxy)
    {
    	if(blockID<=0)return ;
        if (!proxy.getClass().getName().endsWith("ClientProxy"))
        {
            return;
        }
        registerRenderers();
    }
}
