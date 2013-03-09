package hx.MinePainter;

import net.minecraft.item.Item;

public class ItemSculptureBar extends Item{

	public ItemSculptureBar(int par1) {
		super(par1);
		setItemName("itemSculptureBar");
	}

	public boolean getHasSubtypes()
	{
		return true;
	}
}
