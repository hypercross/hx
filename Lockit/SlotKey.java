package hx.Lockit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotKey extends Slot
{
    public SlotKey(IInventory par1iInventory, int par2, int par3, int par4)
    {
        super(par1iInventory, par2, par3, par4);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        if (par1ItemStack == null)
        {
            return false;
        }

        return par1ItemStack.itemID == ModLockit.instance.item("Key").item().shiftedIndex;
    }

    @Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        if (this.getStack() == null)
        {
            return false;
        }

        return this.getStack().itemID == ModLockit.instance.item("Key").item().shiftedIndex;
    }
}
