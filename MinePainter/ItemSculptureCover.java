package hx.MinePainter;

import net.minecraft.item.Item;

public class ItemSculptureCover extends Item{
	public ItemSculptureCover(int par1) {
		super(par1);
		setUnlocalizedName("itemSculptureCover");
	}
	
	public boolean getHasSubtypes()
	{
		return true;
	}
}
