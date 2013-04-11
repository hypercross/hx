package hx.survivalist;

import hx.utils.Debug;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class RecipeDrink{

	private static ArrayList<String> shaped = new  ArrayList<String>();
	private static ArrayList<String> smelt = new  ArrayList<String>();
	private static ArrayList<String> shapeless = new  ArrayList<String>();
	private static ArrayList<Integer> seeds = new ArrayList<Integer>();
	private static ArrayList<String> giveback = new ArrayList<String>();

	static void smelt(String line)
	{
		smelt.add(line);
	}

	static void shapeless(String line)
	{
		shapeless.add(line);
	}
	
	static void shaped(String line)
	{
		shaped.add(line);
	}
	
	static void giveback(String line)
	{
		giveback.add(line);
	}

	static void register()
	{
		try{
			for(String line : smelt)parseSmelting(line);
			for(String line : shapeless)parseShapeless(line);
			for(String line : shaped)parseShaped(line);
			for(String line : giveback)parseGiveback(line);
			int i = 0;
			while(i < seeds.size())
			{
				MinecraftForge.addGrassSeed(new ItemStack(ItemDrink.instance , 1 , seeds.get(i++)), seeds.get(i++));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void parseGiveback(String line) throws Exception {
		try{
			String[] parts = line.split(":");
			ItemDrink.giveback[Integer.parseInt(parts[0])] = fromString(parts[1]);
		}catch(Exception e)
		{
			throw new Exception("problem line: " + line);
		}
	}

	static ItemStack fromString(String line) throws Exception
	{
		Scanner s = new Scanner(line);

		int id = 0, dmg = 0;
		if(s.hasNextInt())id = s.nextInt();
		else{
			for(int i = 0;i<ItemDrink.usedNum;i++)
			{
				String name = ItemDrink.names[i];
				if(name == null)return null;
				if(name.equals(line))return toDrink("1 " + String.valueOf(i));
			}
		}
		if(s.hasNextInt())dmg = s.nextInt();

		return new ItemStack(id,1,dmg);
	}

	static ItemStack toDrink(String line) throws Exception
	{
		try{
			Scanner s = new Scanner(line);
			int amount = s.nextInt();
			int type = s.nextInt();
			return new ItemStack(ItemDrink.instance, amount, 4*ItemDrink.numDrink + type);
		}catch(Exception e)
		{
			throw new Exception("problem line: " + line);
		}
	}

	static void parseSmelting(String line) throws Exception
	{
		try{
			String[] parts = line.split("=");
			ItemStack is = fromString(parts[0].trim());
			FurnaceRecipes.smelting().addSmelting(is.itemID, is.getItemDamage(), toDrink(parts[1].trim()), 0.1f);
		}catch(Exception e)
		{
			throw new Exception("problem line: " + line);
		}
	}

	static void parseShapeless(String line) throws Exception
	{
		try{
			String[] parts = line.split("=");
			String[] inputs = parts[0].split("\\+");

			Object[] stacks = new Object[inputs.length];
			int i = 0;
			for(String input : inputs)
				stacks[i++] = fromString(input.trim());

			GameRegistry.addShapelessRecipe(toDrink(parts[1].trim()), stacks);
		}catch(Exception e)
		{
			throw new Exception("problem line: " + line);
		}
	}
	
	static void parseShaped(String line) throws Exception{
		try{
			String[] parts = line.split("=");
			String[] lines = parts[0].split(";");
			String[] inputs = lines[lines.length-1].split("\\+");
			
			Object[] stacks = new Object[inputs.length];
			int i = 0;
			for(String input : inputs)
				stacks[i++] = fromString(input.trim());
			
			Object[] params = new Object[lines.length - 1 + stacks.length * 2];
			i = 0;
			for(;i<lines.length;i++)params[i] = lines[i].replaceAll("_", " ");
			i--;
			int j = 0;
			for(char c = 'a'; ; c ++)
			{
				params[i++] = c;
				params[i++] = stacks[j++];
				if(j>=stacks.length)break;
			}

			GameRegistry.addShapedRecipe(toDrink(parts[1].trim()), params);
		}catch(Exception e)
		{
			throw new Exception("problem line: " + line);
		}
	}

	public static void drop(int index, Integer valueOf) {
		seeds .add(index + ItemDrink.numDrink * 4);
		seeds.add(valueOf);
	}
}
