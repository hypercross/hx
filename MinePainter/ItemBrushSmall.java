package hx.MinePainter;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBrushSmall extends Item{

	public static ItemBrushSmall instance;
	
	public ItemBrushSmall(int par1) {
		super(par1);
		setUnlocalizedName("itemBrushSmall");
		setCreativeTab(CreativeTabs.tabTools);
		this.setMaxStackSize(1);
		instance = this;
	}
	
	public void updateIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon("MinePainter:brush_small");
    }

}
