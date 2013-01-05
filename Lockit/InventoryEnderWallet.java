package hx.Lockit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;

public class InventoryEnderWallet implements IInventory
{
    private ItemStack[] inventoryContents = new ItemStack[9];
    private InventoryEnderChest epinv;

    public InventoryEnderWallet(EntityPlayer ep)
    {
        epinv = ep.getInventoryEnderChest();

        for (int i = 0; i < getSizeInventory(); i++)
        {
            setInventorySlotContents(i, epinv.getStackInSlot(i));
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
        ItemStack is = epinv.decrStackSize(par1, par2);
        inventoryContents[par1] = epinv.getStackInSlot(par1);
        return is;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2)
    {
        inventoryContents[var1] = var2;
        epinv.setInventorySlotContents(var1, var2);
        this.onInventoryChanged();
    }

    @Override
    public String getInvName()
    {
        // TODO Auto-generated method stub
        return ModLockit.instance.item("EnderWallet").item().getItemDisplayName(new ItemStack(ModLockit.instance.item("EnderWallet").item()));
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
        // TODO Auto-generated method stub
    }
}
