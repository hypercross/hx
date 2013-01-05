package hx.Alchemania;

import hx.Alchemania.Effect.AlchemaniaEffect;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class PowderPillRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting var1, World var2) {
		boolean hasIng = false;
		boolean hasSlime = false;
		
		for(int i =0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			if(is == null)continue;
			
			if(is.itemID == Alchemania.ingredientPowder.shiftedIndex)
			{
				if(AlchemaniaEffect.parseEffects(is) == null)continue;
				if(hasIng)return false;
				hasIng = true;
			}
			else if(is.itemID == Item.slimeBall.shiftedIndex)
			{
				if(hasSlime)return false;
				hasSlime = true;
			}
			
		}
		
		return hasSlime && hasIng;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		for(int i =0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			if(is == null)continue;
			
			if(is.itemID == Alchemania.ingredientPowder.shiftedIndex)
			{
				ItemStack nis = new ItemStack(Alchemania.pill);
				AlchemaniaEffect[] effs = AlchemaniaEffect.parseEffects(is);
				for(AlchemaniaEffect ae : effs)
					ae.solidify();
				AlchemaniaEffect.writeEffects(nis, effs);
				return nis;
			}
		}
		return null;
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
