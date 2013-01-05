package hx.Lockit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.*;

public class InventoryKeychain implements IInventory
{
    private ItemStack[] inventoryContents = new ItemStack[36];
    public NBTTagCompound chain, lock;
    public ItemStack chainStack;

    public InventoryKeychain(ItemStack chainStack)
    {
        this.chainStack = chainStack;
        NBTTagCompound chainNBT = chainStack.stackTagCompound.getCompoundTag("lock");
        chain = chainStack.stackTagCompound;
        lock = chainNBT;

        for (int i = 0; i < inventoryContents.length; i++)
        {
            if (!chainNBT.hasKey("key" + i))
            {
                continue;
            }

            ItemStack keystack = new ItemStack(ModLockit.instance.item("Key").item());
            keystack.setRepairCost(-4);
            ItemKey.setCode(keystack, chainNBT.getInteger("key" + i));
            ItemKey.setName(keystack, chainNBT.getString("keyName" + i));

            if (chainNBT.hasKey("keyColor" + i))
            {
                ItemKey.setColor(keystack, chainNBT.getInteger("keyColor" + i));
            }

            inventoryContents[i] = keystack;
        }
    }

    @Override
    public int getSizeInventory()
    {
        // TODO Auto-generated method stub
        return inventoryContents.length;
    }

    @Override
    public ItemStack getStackInSlot(int var1)
    {
        // TODO Auto-generated method stub
        return inventoryContents[var1];
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.inventoryContents[par1] != null)
        {
            ItemStack var3;

            if (this.inventoryContents[par1].stackSize <= par2)
            {
                var3 = this.inventoryContents[par1];
                this.inventoryContents[par1] = null;
                this.onInventoryChanged();
                return var3;
            }
            else
            {
                var3 = this.inventoryContents[par1].splitStack(par2);

                if (this.inventoryContents[par1].stackSize == 0)
                {
                    this.inventoryContents[par1] = null;
                }

                this.onInventoryChanged();
                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1)
    {
        lock.removeTag("key" + var1);
        lock.removeTag("keyName" + var1);
        lock.removeTag("keyColor" + var1);

        if (inventoryContents[var1] == null)
        {
            return null;
        }

        if (inventoryContents[var1].itemID == ModLockit.instance.item("Key").item().shiftedIndex)
        {
            if (!inventoryContents[var1].hasTagCompound())
            {
                return null;
            }

            NBTTagCompound nbt = inventoryContents[var1].stackTagCompound.getCompoundTag("lock");
            lock.setInteger("key" + var1, nbt.getInteger("key"));

            if (ItemKey.getColor(inventoryContents[var1]) != -1)
            {
                lock.setInteger("keyColor" + var1, ItemKey.getColor(inventoryContents[var1]));
            }

            if (inventoryContents[var1].hasDisplayName())
                lock.setString("keyName" + var1,
                        inventoryContents[var1].getTagCompound().getCompoundTag("display").getString("Name"));

            return null;
        }

        return inventoryContents[var1];
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2)
    {
        inventoryContents[var1] = var2;
        getStackInSlotOnClosing(var1);
        this.onInventoryChanged();
    }

    @Override
    public String getInvName()
    {
        // TODO Auto-generated method stub
        return chainStack.getDisplayName();
    }

    @Override
    public int getInventoryStackLimit()
    {
        // TODO Auto-generated method stub
        return 64;
    }

    @Override
    public void onInventoryChanged()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1)
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void openChest()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void closeChest()
    {
    }
}
