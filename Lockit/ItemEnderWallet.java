package hx.Lockit;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemEnderWallet extends Item
{
    public ItemEnderWallet(int par1)
    {
        super(par1);
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabMisc);
        setIconIndex(19);
        setItemName("itemEnderWallet");
    }

    public String getTextureFile()
    {
        return ModLockit.instance.MAIN_TEXTURE;
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par2World.isRemote)
        {
            return par1ItemStack;
        }

        par3EntityPlayer.displayGUIChest(new InventoryEnderWallet(par3EntityPlayer));
        return par1ItemStack;
    }
}
