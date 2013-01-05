package hx.Lockit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;

public class ContainerKeychain extends ContainerChest
{
    private InventoryKeychain inv;
    private int index;

    public ContainerKeychain(InventoryPlayer par1iInventory,	InventoryKeychain par2iInventory)
    {
        super(par1iInventory, par2iInventory);
        inv = par2iInventory;
        index = par1iInventory.currentItem;
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        int var3 = 0;
        int var4;
        int var5;

        for (var4 = 0; var4 < 4; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new SlotKey(par2iInventory, var5 + var4 * 9, 8 + var5 * 18, 18 + var4 * 18));
            }
        }

        for (var4 = 0; var4 < 3; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new SlotKey(par1iInventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 103 + var4 * 18 + var3));
            }
        }

        for (var4 = 0; var4 < 9; ++var4)
        {
            this.addSlotToContainer(new SlotKey(par1iInventory, var4, 8 + var4 * 18, 161 + var3));
        }
    }

    @Override
    public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
    {
        super.onCraftGuiClosed(par1EntityPlayer);

        if (!par1EntityPlayer.worldObj.isRemote)
        {
            for (int i = 0; i < 36; i++)
            {
                ItemStack var2 = inv.getStackInSlotOnClosing(i);

                if (var2 != null)
                {
                    par1EntityPlayer.dropPlayerItem(var2);
                }
            }
        }

        if (ItemKeyChain.empty(inv.chainStack))
        {
            par1EntityPlayer.inventory.setInventorySlotContents(index, null);
        }
        else
        {
            par1EntityPlayer.inventory.setInventorySlotContents(index, inv.chainStack);
        }
    }
}