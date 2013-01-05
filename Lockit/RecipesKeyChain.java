package hx.Lockit;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.*;
import net.minecraft.world.World;

public class RecipesKeyChain implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting var1, World var2)
    {
        int num = 0;

        for (int i = 0; i < var1.getSizeInventory(); i++)
        {
            ItemStack stack = var1.getStackInSlot(i);

            if (stack == null)
            {
                continue;
            }

            num++;

            if (stack.getItem().shiftedIndex == ModLockit.instance.item("Key").item().shiftedIndex)
            {
                continue;
            }

            if (stack.getItem().shiftedIndex == ModLockit.instance.item("KeyChain").item().shiftedIndex)
            {
                continue;
            }

            return false;
        }

        return num > 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1)
    {
        ItemStack keyChain = new ItemStack(ModLockit.instance.item("KeyChain").item());
        keyChain.setRepairCost(-4);

        for (int i = 0; i < var1.getSizeInventory(); i++)
        {
            ItemStack stack = var1.getStackInSlot(i);

            if (stack == null)
            {
                continue;
            }

            if (stack.getItem().shiftedIndex == ModLockit.instance.item("Key").item().shiftedIndex)
            {
                ItemKeyChain.appendKey(keyChain, stack);
            }
            else if (stack.getItem().shiftedIndex == ModLockit.instance.item("KeyChain").item().shiftedIndex)
            {
                NBTTagCompound nbt = stack.getTagCompound();

                if (nbt == null)
                {
                    continue;
                }

                NBTTagCompound lock = nbt.getCompoundTag("lock");

                if (lock == null)
                {
                    continue;
                }

                int num = 36;

                for (int j = 0; j < num; j++)
                {
                    if (lock.hasKey("key" + j))
                    {
                        String name = lock.hasKey("keyName" + j) ? lock.getString("keyName" + j) : null;
                        int keycode = lock.getInteger("key" + j);
                        int color   = ItemKeyChain.keycolor(keyChain, j);
                        ItemKeyChain.appendKey(keyChain, keycode, name, color);
                    }
                }
            }
        }

        return keyChain;
    }

    @Override
    public int getRecipeSize()
    {
        // TODO Auto-generated method stub
        return 10;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
