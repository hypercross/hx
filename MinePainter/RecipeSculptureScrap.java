package hx.MinePainter;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeSculptureScrap implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting var1, World var2) {

		int count = 0;
		for(int i =0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			if(is == null)continue;
			else if(is.getItem().itemID == ModMinePainter.instance.item("SculptureCover").item().itemID)count += 64 ;
			else if(is.getItem().itemID == ModMinePainter.instance.item("SculptureBar")	 .item().itemID)count += 8 ;
			else if(is.getItem().itemID == ModMinePainter.instance.item("SculpturePiece").item().itemID)count += 1 ;
			else return false;
		}
		
		if(count == 0)return false;
		if(count % 512 == 0 && count/512 <= 64)return true;
		if(count % 64 == 0 && count/64 <= 64)return true;
		if(count % 8 == 0 && count/8 <= 64)return true;
		if(count <= 64)return true;
		
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		int count = 0;
		int meta = -1;
		
		for(int i =0;i<var1.getSizeInventory();i++)
		{
			ItemStack is = var1.getStackInSlot(i);
			if(is == null)continue;
			else if(is.getItem().itemID == ModMinePainter.instance.item("SculptureCover").item().itemID)count += 64 ;
			else if(is.getItem().itemID == ModMinePainter.instance.item("SculptureBar")	 .item().itemID)count += 8 ;
			else if(is.getItem().itemID == ModMinePainter.instance.item("SculpturePiece").item().itemID)count += 1 ;
			else return null;
			
			if(meta > 0 && meta != is.getItemDamage())return null;
			meta = is.getItemDamage();
		}
		
		if(meta == -1)
			return null;
		
		ItemStack is = null;
		
		if(count % 512 == 0)
			is = new ItemStack(BlockSculpture.materialBlock[meta & 15], count/512, meta >> 4);
		
		else if(count % 64 == 0)
			is = new ItemStack(ModMinePainter.instance.item("SculptureCover").item(), count/64);
		
		else if(count % 8 == 0)
			is = new ItemStack(ModMinePainter.instance.item("SculptureBar").item(), count/8);
		
		else is = new ItemStack(ModMinePainter.instance.item("SculpturePiece").item(), count);
		
		is.setItemDamage(meta);
		return is;
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
