package hx.Alchemania.Item;

import hx.Alchemania.Alchemania;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPill extends ItemIngredientPowder
{

	public ItemPill(int id) {
		super(id);
		setIconIndex(1);
        setItemName("pill");
		}

	public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 32;
    }
	
	public boolean isPotionIngredient()
	{
		return true;
	}
	
	public String getTextureFile()
	{
		return Alchemania.MAIN_TEXTURE;
	}
}
