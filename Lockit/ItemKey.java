package hx.Lockit;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemKey extends Item
{
    public ItemKey(int id)
    {
        super(id);
        // Constructor Configuration
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabMisc);
        setIconIndex(17);
        setItemName("itemKey");
    }

    public String getTextureFile()
    {
        return ModLockit.instance.MAIN_TEXTURE;
    }

    public static int getColor(ItemStack par1ItemStack)
    {
        NBTTagCompound var2 = par1ItemStack.getTagCompound();

        if (var2 == null)
        {
            return 0x888888;
        }
        else
        {
            NBTTagCompound var3 = var2.getCompoundTag("display");
            return var3 == null ? -1 : (var3.hasKey("color") ? var3.getInteger("color") : -1);
        }
    }

    public static void setColor(ItemStack par1ItemStack, int par2)
    {
        NBTTagCompound var3 = par1ItemStack.getTagCompound();

        if (var3 == null)
        {
            var3 = new NBTTagCompound();
            par1ItemStack.setTagCompound(var3);
        }

        NBTTagCompound var4 = var3.getCompoundTag("display");

        if (!var3.hasKey("display"))
        {
            var3.setCompoundTag("display", var4);
        }

        if (par2 != -1)
        {
            var4.setInteger("color", par2);
        }
        else
        {
            var4.removeTag("color");
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    public int getIconFromDamageForRenderPass(int par1, int par2)
    {
        return par2 == 1 ? this.iconIndex - 1 : super.getIconFromDamageForRenderPass(par1, par2);
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        if (par2 == 0)
        {
            return 0xffffff;
        }

        int var3 = this.getColor(par1ItemStack);

        if (var3 < 0)
        {
            var3 = 0x888888;
        }

        return var3;
    }

    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        if (keycode(par1ItemStack) == -1)
        {
            return;
        }

        par3List.add("#" + keycode(par1ItemStack));
    }

    public static int keycode(ItemStack key)
    {
        if (key.stackTagCompound == null)
        {
            key.stackTagCompound = new NBTTagCompound();
        }

        if (!key.stackTagCompound.hasKey("lock"))
        {
            key.stackTagCompound.setCompoundTag("lock", new NBTTagCompound());
        }

        if (key.stackTagCompound.getCompoundTag("lock").hasKey("key"))
        {
            return key.stackTagCompound.getCompoundTag("lock").getInteger("key");
        }

        return -1;
    }

    public static String keyname(ItemStack key)
    {
        if (key.stackTagCompound == null)
        {
            key.stackTagCompound = new NBTTagCompound();
        }

        if (!key.stackTagCompound.hasKey("display"))
        {
            key.stackTagCompound.setCompoundTag("display", new NBTTagCompound());
        }

        if (key.stackTagCompound.getCompoundTag("display").hasKey("Name"))
        {
            return key.stackTagCompound.getCompoundTag("display").getString("Name");
        }

        return null;
    }

    public static void setCode(ItemStack key, int i)
    {
        if (key.stackTagCompound == null)
        {
            key.stackTagCompound = new NBTTagCompound();
        }

        if (!key.stackTagCompound.hasKey("lock"))
        {
            key.stackTagCompound.setCompoundTag("lock", new NBTTagCompound());
        }

        key.stackTagCompound.getCompoundTag("lock").setInteger("key", i);
    }

    public static void setName(ItemStack key, String name)
    {
        if (key.stackTagCompound == null)
        {
            key.stackTagCompound = new NBTTagCompound();
        }

        if (!key.stackTagCompound.hasKey("display"))
        {
            key.stackTagCompound.setCompoundTag("display", new NBTTagCompound());
        }

        if (name != "" && name != null)
        {
            key.stackTagCompound.getCompoundTag("display").setString("Name", name);
        }
        else
        {
            key.stackTagCompound.getCompoundTag("display").removeTag("Name");
        }
    }
}