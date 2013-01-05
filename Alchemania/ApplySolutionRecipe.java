package hx.Alchemania;

import hx.Alchemania.Effect.AlchemaniaEffect;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class ApplySolutionRecipe implements IRecipe{

	private boolean isTool(ItemStack is)
	{
		for(int id : Alchemania.dippables)
		{
			if(is.itemID == id)return true;
		}
		return false;
	}
	
	@Override
	public boolean matches(InventoryCrafting var1, World var2) {
		boolean hasItem = false;
		boolean hasSolution = false;
		
		for(int i=0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			if(is == null)continue;
			
			if(is.itemID == Alchemania.pill.shiftedIndex)return false;
			if(is.itemID == Alchemania.ingredientPowder.shiftedIndex)return false;
			if(is.itemID == Alchemania.solution.shiftedIndex)
			{
				if(hasSolution)return false;
				hasSolution = true;
			}else if(isTool(is))
			{
				if(hasItem)return false;
				hasItem = true;
			}
		}
		
		return hasItem && hasSolution;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		ItemStack item = null;
		ItemStack solution = null;
		
		for(int i=0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			if(is == null)continue;
			
			if(is.itemID == Alchemania.solution.shiftedIndex)
			{
				solution = is;
			}else
			{
				item = is;
			}
		}
		
		ItemStack result = item.copy();
		AlchemaniaEffect[] effs = AlchemaniaEffect.parseEffects(solution);
		AlchemaniaEffect.writeEffects(result, effs);
		return result;
	}

	@Override
	public int getRecipeSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getRecipeOutput() {
		// TODO Auto-generated method stub
		return null;
	}

}
