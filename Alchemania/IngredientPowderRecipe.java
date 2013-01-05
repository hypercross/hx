package hx.Alchemania;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.TreeMap;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import hx.Alchemania.Effect.AlchemaniaEffect;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;

public class IngredientPowderRecipe implements IRecipe {

	private static void rageQuit()
	{
		System.err.println("Error during parsing!");
	}
	
	public static void load(FMLPreInitializationEvent event)
	{
		File file = new File(event.getModConfigurationDirectory(),"Alchemania_ingredients.cfg");
		
		try {
			if(!file.exists())file.createNewFile();
			Scanner sc = new Scanner(new FileInputStream(file));
			
			int id=0, meta=0;
			while(sc.hasNextLine())
			{
				String line = sc.nextLine().trim();
				if(line.startsWith("#"))continue;
				if(line.isEmpty())continue;
				
				String[] params = line.split(" ");
				if(params.length == 1)
				{
					String[] idnmeta = params[0].split(":");
					id = Integer.parseInt(idnmeta[0]);
					if(idnmeta.length>1)
						meta = Integer.parseInt(idnmeta[1]);
					else meta = 0;
					
					if(id == 0)
					{
						rageQuit();
						sc.close();
						return;
					}
				}else if (params.length == 4)
				{
					byte effid = 0;
					for(;effid<AlchemaniaEffect.effects.length;effid++)
					{
						AlchemaniaEffect ae = AlchemaniaEffect.effects[effid];
						if(ae == null)continue;
						if(ae.getName().equals(params[0]))
						{
							put(id,meta,effid,
									Byte.parseByte(params[1]),
									Float.parseFloat(params[2]),
									Float.parseFloat(params[3]));
						}
					}
					
				}else 
				{
					rageQuit();
					sc.close();
					return;
				}
			}

			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		put(Block.mushroomBrown.blockID,0,
//				AlchemaniaEffect.DAMAGE,(byte)9,0,.9f);
//		
//		put(Block.mushroomRed.blockID,0,
//				AlchemaniaEffect.SPEED,(byte)9,50f,.9f);
	}
	
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
	
	private static TreeMap<ItemKey, EffectSet> itemEffects = new TreeMap<ItemKey, EffectSet>();

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
			ItemStack ing = getIngredientStack(is,0f);
			if(ing == is)continue;
			else if(hasIng)return false;
			hasIng = true;
		}
		
		
		return hasMortar && hasIng;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		for(int i =0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			if(is == null)continue;
			ItemStack ing = getIngredientStack(is,0f);
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
			es.effects[i].duration	= duration;
			es.effects[i].purity	= purity;
			break;
		}
		
	}

	private ItemStack getIngredientStack(ItemStack is, float inpurity)
	{
		EffectSet es = new EffectSet();
		
		if(is.itemID != Alchemania.pill.shiftedIndex)
		{
			ItemKey key = new ItemKey();
			key.id = is.itemID;
			key.meta = is.getItemDamage();
			
			es = itemEffects.get(key);
			if(es == null)return is;
		}else
		{
			es.effects = AlchemaniaEffect.parseEffects(is);
		}
		
		byte i = 0;
		while(i<es.effects.length && es.effects[i]!=null)i++;
		
		ItemStack newis = new ItemStack(Alchemania.ingredientPowder);
		newis.setTagCompound(new NBTTagCompound());
		NBTTagCompound alcNBT = new NBTTagCompound();
		
		alcNBT.setByte("num", i);
		for(int j=0;j<i;j++)
		{
			NBTTagCompound effNBT = new NBTTagCompound();
			AlchemaniaEffect ae = es.effects[j].copy();
			ae.powderize(inpurity);
			ae.writeTo(effNBT);
			alcNBT.setCompoundTag(String.valueOf(j), effNBT);
		}
		
		newis.stackTagCompound.setCompoundTag("AME", alcNBT);
		
		return newis;
	}
}
