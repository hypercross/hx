package hx.Lockit;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemKeyChain extends Item
{
    public ItemKeyChain(int id)
    {
        super(id);
        // Constructor Configuration
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabMisc);
        setIconIndex(18);
        setItemName("itemKeyChain");
    }

    public String getTextureFile()
    {
        return ModLockit.instance.MAIN_TEXTURE;
    }

    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        String cap = "";
        int num = 36;

        for (int i = 0; i < num; i++)
        {
            if (keycode(par1ItemStack, i) != -1)
            {
                cap += keyname(par1ItemStack, i) == null ? "#" + keycode(par1ItemStack, i) : keyname(par1ItemStack, i);

                if (cap.length() > 40)
                {
                    par3List.add(cap);
                    cap = "";
                }
                else
                {
                    cap += ", ";
                }
            }
        }

        if (cap != "")
        {
            par3List.add(cap);
        }
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par2World.isRemote)
        {
            return par1ItemStack;
        }

        if (!par1ItemStack.hasTagCompound())
        {
            return par1ItemStack;
        }

        if (par1ItemStack.stackTagCompound.getCompoundTag("lock") == null)
        {
            return par1ItemStack;
        }

        par3EntityPlayer.openGui(ModLockit.instance, 0, par2World, 0, 0, 0);
        return par1ItemStack;
    }

    public static boolean empty(ItemStack chain)
    {
        int num = 36;

        for (int i = 0; i < num; i++)
        {
            if (keycode(chain, i) != -1)
            {
                return false;
            }
        }

        return true;
    }

    public static int keycode(ItemStack key, int index)
    {
        if (!key.hasTagCompound())
        {
            key.stackTagCompound = new NBTTagCompound();
        }

        if (!key.stackTagCompound.hasKey("lock"))
        {
            key.stackTagCompound.setCompoundTag("lock", new NBTTagCompound());
        }

        if (key.stackTagCompound.getCompoundTag("lock").hasKey("key" + index))
        {
            return key.stackTagCompound.getCompoundTag("lock").getInteger("key" + index);
        }

        return -1;
    }

    public static String keyname(ItemStack key, int index)
    {
        if (!key.hasTagCompound())
        {
            key.stackTagCompound = new NBTTagCompound();
        }

        if (!key.stackTagCompound.hasKey("lock"))
        {
            key.stackTagCompound.setCompoundTag("lock", new NBTTagCompound());
        }

        if (key.stackTagCompound.getCompoundTag("lock").hasKey("keyName" + index))
        {
            return key.stackTagCompound.getCompoundTag("lock").getString("keyName" + index);
        }

        return null;
    }

    public static int keycolor(ItemStack key, int index)
    {
        if (!key.hasTagCompound())
        {
            key.stackTagCompound = new NBTTagCompound();
        }

        if (!key.stackTagCompound.hasKey("lock"))
        {
            key.stackTagCompound.setCompoundTag("lock", new NBTTagCompound());
        }

        if (key.stackTagCompound.getCompoundTag("lock").hasKey("keyColor" + index))
        {
            return key.stackTagCompound.getCompoundTag("lock").getInteger("keyColor" + index);
        }

        return -1;
    }

    public static boolean appendKey(ItemStack chain, ItemStack key)
    {
        int num = 36;
        int i = 0;

        for (; i < num; i++)
            if (keycode(chain, i) == -1)
            {
                break;
            }

        if (i >= num)
        {
            return false;
        }

        chain.stackTagCompound.getCompoundTag("lock").setInteger("key" + i, ItemKey.keycode(key));

        if (ItemKey.keyname(key) != null)
        {
            chain.stackTagCompound.getCompoundTag("lock").setString("keyName" + i, ItemKey.keyname(key));
        }

        if (ItemKey.getColor(key) != -1)
        {
            chain.stackTagCompound.getCompoundTag("lock").setInteger("keyColor" + i, ItemKey.getColor(key));
        }

        return true;
    }

    public static boolean appendKey(ItemStack chain, int keycode, String name, int keyColor)
    {
        int num = 36;
        int i = 0;

        for (; i < num; i++)
            if (keycode(chain, i) == -1)
            {
                break;
            }

        if (i >= num)
        {
            return false;
        }

        chain.stackTagCompound.getCompoundTag("lock").setInteger("key" + i, keycode);

        if (name != null)
        {
            chain.stackTagCompound.getCompoundTag("lock").setString("keyName" + i, name);
        }

        if (keyColor != -1)
        {
            chain.stackTagCompound.getCompoundTag("lock").setInteger("keyColor" + i, keyColor);
        }

        return true;
    }

    public static void removeKey(ItemStack chain, int keycode)
    {
        int num = 36;
        int i = 0;

        for (; i < num; i++)
            if (keycode(chain, i) == keycode)
            {
                break;
            }

        if (i >= num)
        {
            return;
        }

        chain.stackTagCompound.getCompoundTag("lock").removeTag("key" + i);
        chain.stackTagCompound.getCompoundTag("lock").removeTag("keyName" + i);
        chain.stackTagCompound.getCompoundTag("lock").removeTag("keyColor" + i);
    }
}