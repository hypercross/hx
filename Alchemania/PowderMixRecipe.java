package hx.Alchemania;

import java.util.ArrayList;
import java.util.Arrays;

import hx.Alchemania.Effect.AlchemaniaEffect;
import hx.Alchemania.Effect.ComparatorEffect;
import hx.Alchemania.Item.ItemIngredientPowder;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class PowderMixRecipe implements IRecipe {

	private boolean isPowder(ItemStack is)
	{
		if(is == null)return false;
		return is.itemID == Alchemania.ingredientPowder.shiftedIndex;
	}
	
	@Override
	public boolean matches(InventoryCrafting var1, World var2) {
			int powder = 0;
			for(int i =0;i<var1.getSizeInventory();i++)
			{
				ItemStack is = var1.getStackInSlot(i);
				if(is ==null)continue;
				if(isPowder(is))powder++;
				else return false;
			}
			return powder == 2;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) { 
		for(int i =0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			if(is ==null)continue;

			for(int j =i+1;j<var1.getSizeInventory();j++)
			{
				ItemStack is2 = var1.getStackInSlot(j);
				if(is2 ==null)continue;
				
				AlchemaniaEffect[] eff1 = AlchemaniaEffect.parseEffects(is);
				AlchemaniaEffect[] eff2 = AlchemaniaEffect.parseEffects(is2);
				AlchemaniaEffect[] eff3 = mix(eff1,eff2);
				
				ItemStack is3 = new ItemStack(Alchemania.ingredientPowder);
				is3.stackSize = 2;
				AlchemaniaEffect.writeEffects(is3, eff3);
				return is3;
			}
		}
		
		return null;
	}
	
	public AlchemaniaEffect[] mix(AlchemaniaEffect[] eff1,AlchemaniaEffect[] eff2)
	{
		if(eff1 == null)return eff2;
		if(eff2 == null)return eff1;
		AlchemaniaEffect[] combine = new AlchemaniaEffect[eff1.length + eff2.length];
		int ind = 0; 
		
		for(AlchemaniaEffect ae1 : eff1)
			combine[ind++] = ae1;
		for(AlchemaniaEffect ae2 : eff2)
			combine[ind++] = ae2;
		
		for(int i=0;i<ind;i++)
		{
			if(combine[i] == null)continue;
			boolean mixed = false;
			for(int j=i+1;j<ind;j++)
			{
				if(combine[j] == null)continue;
				if(combine[i].type == combine[j].type)
				{
					combine[i].mixWith(combine[j]);
					combine[j] = null;
					mixed = true;
					break;
				}
			}
			if(!mixed)combine[i].mixWith(new AlchemaniaEffect());
		}
		
		Arrays.sort(combine,new ComparatorEffect());
		ind = 0;
		while(ind < combine.length && combine[ind]!=null)ind++;
		
		return Arrays.copyOf(combine, ind);
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
