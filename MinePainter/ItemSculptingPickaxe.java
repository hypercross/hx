package hx.MinePainter;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemSculptingPickaxe extends Item{

	public ItemSculptingPickaxe(int id) {
		super(id);
		maxStackSize = 1;
		this.setMaxDamage(240);
		setCreativeTab(CreativeTabs.tabTools);
		setUnlocalizedName("itemSculptingPickaxe");
	}
	
}
