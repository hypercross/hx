package hx.MinePainter;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBrush extends Item{

	public static ItemBrush instance;
	
	public ItemBrush(int par1) {
		super(par1);
		setUnlocalizedName("itemBrush");
		setCreativeTab(CreativeTabs.tabTools);
		this.setMaxStackSize(1);
		this.setMaxDamage(240);
		instance = this;
	}
	
	public void updateIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon("MinePainter:brush");
    }

}
