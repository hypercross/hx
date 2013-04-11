package hx.MinePainter;

import hx.utils.Debug;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemEmblem extends Item{
	private static Icon shadow;
	private static Icon bg;

	public ItemEmblem(int par1) {
		super(par1);
		setCreativeTab(CreativeTabs.tabDecorations);
		setUnlocalizedName("itemEmblem");
		setMaxStackSize(1);
	}
	
	public void updateIcons(IconRegister par1IconRegister)
    {
        bg = par1IconRegister.registerIcon("MinePainter:emblem_bg");
        shadow = par1IconRegister.registerIcon("MinePainter:emblem_shadow");
    }
	
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int face, float xs, float ys, float zs)
	{
		if(w.isRemote)return false;
		if(w.getBlockId(x, y, z) != ModMinePainter.instance.block("Canvas").blockID)return false;
		
		TileEntityCanvas tec = (TileEntityCanvas)w.getBlockTileEntity(x, y, z);
		checkNBT(is).setByteArray("data", tec.image.toByteArray());
		checkNBT(is).setInteger("hash", tec.image.hashCode());
		
		return true;
	}
	
	public void setBg(boolean isShadow)
	{
		if(isShadow)iconIndex = shadow;
		else iconIndex = bg;
	}
	
	
	public static NBTTagCompound checkNBT(ItemStack is) {
		NBTTagCompound nbt;
		if(!is.hasTagCompound())nbt = is.stackTagCompound = new NBTTagCompound();
		else nbt = is.stackTagCompound;
		
		nbt.setCompoundTag("emblem", nbt.getCompoundTag("emblem"));
		return nbt.getCompoundTag("emblem");
	}
}
