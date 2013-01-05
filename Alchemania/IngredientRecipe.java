package hx.Alchemania;

import java.util.HashMap;

import hx.Alchemania.Effect.AlchemaniaEffect;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class IngredientRecipe implements IRecipe {
	
	private static class ItemKey implements Comparable<ItemKey>
	{
		int id, meta;

		@Override
		public int compareTo(ItemKey obj) {
			if(id != obj.id)return id - obj.id;
			if(meta != obj.meta)return meta - obj.meta;
			return 0;
		}
		
		@Override
		public boolean equals(Object key)
		{
			return compareTo((ItemKey) key) == 0;
		}
	}
	
	private static class EffectSet
	{
		public AlchemaniaEffect[] effects = new AlchemaniaEffect[4];
	}
	
	private static HashMap<ItemKey, EffectSet> itemEffects = new HashMap<ItemKey, EffectSet>();

	
	public static void load()
	{
		put(Block.mushroomBrown.blockID,0,(byte)1,(byte)1,1,1);
	}
	
	@Override
	public boolean matches(InventoryCrafting var1, World var2) {
		boolean hasMortar = false;
		boolean hasIng = false;
		
		for(int i =0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			if(is == null)continue;
			if(is.itemID == Alchemania.mortarNPestle.shiftedIndex)
			{
				if(hasMortar)return false;
				hasMortar = true;
				continue;
			}
			ItemStack ing = getIngredientStack(is);
			if(ing == is)continue;
			else if(hasIng)return false;
			hasIng = true;
		}
		
		System.err.println(hasMortar + " " + hasIng);
		
		return hasMortar && hasIng;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		for(int i =0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			ItemStack ing = getIngredientStack(is);
			if(ing == is)continue;
			else return ing;
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
	
	private static void put(int id, int meta, byte type, byte grade, float duration, float purity)
	{
		ItemKey key = new ItemKey();
		key.id = id;
		key.meta = meta;
		
		if(!itemEffects.containsKey(key))
			itemEffects.put(key, new EffectSet());
		
		EffectSet es = itemEffects.get(key);
		for(int i =0;i<4;i++)
		{
			if(es.effects[i] != null)continue;
			es.effects[i] = new AlchemaniaEffect();
			es.effects[i].type		= type;
			es.effects[i].grade		= grade;
			es.effects[i].duration	= type;
			es.effects[i].purity	= purity;
		}
		
	}

	private ItemStack getIngredientStack(ItemStack is)
	{
		ItemKey key = new ItemKey();
		key.id = is.itemID;
		key.meta = is.getItemDamage();
		
		System.err.println(key.id + " " + key.meta);
		
		EffectSet es = itemEffects.get(key);
		if(es == null)return is;
		
		byte i = 0;
		while(es.effects[i]!=null)i++;
		
		ItemStack newis = new ItemStack(Alchemania.ingredientPowder);
		newis.setTagCompound(new NBTTagCompound());
		NBTTagCompound alcNBT = new NBTTagCompound();
		
		alcNBT.setByte("num", i);
		for(int j=0;j<i;j++)
		{
			NBTTagCompound effNBT = new NBTTagCompound();
			es.effects[j].writeTo(effNBT);
			alcNBT.setCompoundTag(String.valueOf(j), effNBT);
		}
		
		newis.stackTagCompound.setCompoundTag("AME", alcNBT);
		
		return newis;
	}
}
