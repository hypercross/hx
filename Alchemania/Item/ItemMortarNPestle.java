package hx.Alchemania.Item;

import hx.Alchemania.Alchemania;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMortarNPestle extends Item{

	public ItemMortarNPestle(int par1) {
		super(par1);
		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabTools);
		setItemName("mortarNPestle");
		setMaxDamage(30);
		setNoRepair();
		setIconIndex(0);
	}

	public boolean hasContainerItem()
	{
		return true;
	}

	public boolean doesContainerItemLeaveCraftingGrid(ItemStack ist)
	{
		return false;
	}

	public ItemStack getContainerItemStack(ItemStack ist)
	{
		int dmg = ist.getItemDamage();

		if(dmg == this.getMaxDamage())
			return new ItemStack(Item.bowlEmpty);
		
		ItemStack tr = ist.copy();
		tr.setItemDamage(dmg + 1);
		return tr;
	}

	public String getTextureFile()
	{
		return Alchemania.MAIN_TEXTURE;
	}
}
