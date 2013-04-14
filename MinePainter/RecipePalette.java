package hx.MinePainter;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipePalette implements IRecipe{

	private int mix(int num, int... colors)
	{
		int color = 0;
		for(int b = 0 ; b < 32; b += 8)
		{
			for(int i = 0 ; i < num; i ++)
			{
				color += ((((colors[i] >> b) & 0xff)/num) << b); 
			}
		}
		
		return color;
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		
		int hasColor = 0;
		int hasPalette = 0;
		
		for(int i = 0 ; i < inv.getSizeInventory(); i ++)
		{
			ItemStack is = inv.getStackInSlot(i);
			
			if(is == null)continue;
			else if(is.itemID == Item.dyePowder.itemID)hasColor ++;
			else if(is.itemID == ItemPalette.instance.itemID)hasPalette ++;
			else if(is.itemID == Item.slimeBall.itemID)hasColor ++;
			else return false;
		}
		
		return hasColor > 0 && hasPalette == 1;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		
		int[] colors = new int[64];
		int[] palette = null;
		ItemStack paletteStack = new ItemStack(ItemPalette.instance);
		int s = 0;
		
		for(int i = 0 ; i < inv.getSizeInventory(); i ++)
		{
			ItemStack is = inv.getStackInSlot(i);
			
			if(is == null)continue;
			else if(is.itemID == Item.dyePowder.itemID)colors[s++] = ItemDye.dyeColors[is.getItemDamage() & 15] | 0xff000000;
			else if(is.itemID == Item.slimeBall.itemID)colors[s++] = 0xffffff;
			else if(is.itemID == ItemPalette.instance.itemID)
				{ 
					palette = ItemPalette.getColors(is).clone();
					for(int j = 0 ; j < i; j ++)
						colors[s++] = palette[0];
				}
		}
		
		int mixed = mix(s, colors);
		palette[0] = mixed;
		ItemPalette.setColors(paletteStack, palette);
		return paletteStack;
	}

	@Override
	public int getRecipeSize() {
		return 0;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
