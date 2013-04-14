package hx.MinePainter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemStoneChisel extends Item{

	public ItemStoneChisel(int par1) {
		super(par1);
		setUnlocalizedName("itemStoneChisel");
		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setMaxDamage(64);
	}
	
	public void updateIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon("MinePainter:stone_chisel");
    }
}
