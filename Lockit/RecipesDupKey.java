package hx.Lockit;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesDupKey implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting var1, World var2)
    {
        int hasKey = 0;
        boolean hasIron = false;

        for (int i = 0; i < var1.getSizeInventory(); i++)
        {
            ItemStack stack = var1.getStackInSlot(i);

            if (stack == null)
            {
                continue;
            }

            if (stack.getItem().shiftedIndex == ModLockit.instance.item("Key").item().shiftedIndex)
            {
                hasKey ++;
                continue;
            }

            if (stack.getItem().shiftedIndex == Item.ingotIron.shiftedIndex)
            {
                hasIron = true;
                continue;
            }

            return false;
        }

        return hasKey == 1 && hasIron;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1)
    {
        ItemStack key = null ;
        int ironCount = 0;

        for (int i = 0; i < var1.getSizeInventory(); i++)
        {
            ItemStack stack = var1.getStackInSlot(i);

            if (stack == null)
            {
                continue;
            }

            if (stack.getItem().shiftedIndex == ModLockit.instance.item("Key").item().shiftedIndex)
            {
                key = stack.copy();
            }
            else if (stack.getItem().shiftedIndex == Item.ingotIron.shiftedIndex)
            {
                ironCount++;
            }
        }

        key.stackSize = ironCount + 1;
        return key;
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
