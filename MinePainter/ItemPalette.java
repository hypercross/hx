package hx.MinePainter;

import hx.utils.Debug;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemPalette extends Item{
	
	public static ItemPalette instance;
	private Icon[] colors = new Icon[6]; 

	public ItemPalette(int par1) {
		super(par1);
		setUnlocalizedName("itemPalette");
		setCreativeTab(CreativeTabs.tabTools);
		this.setMaxStackSize(1);
		instance = this;
	}

	public void updateIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon("MinePainter:palette");
        
        for(int i = 0;i<6;i++)colors[i] = par1IconRegister.registerIcon("MinePainter:palette" + i);
    }
	
	public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
	
	public int getRenderPasses(int metadata)
	{
		return 7;
	}
	
	public Icon getIcon(ItemStack is, int renderPass)
	{
		if(renderPass == 0)return iconIndex;
		return colors[renderPass - 1];
	}
	
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
	{
		int[] colors = getColors(par1ItemStack);
		
		if(par2 == 0)return super.getColorFromItemStack(par1ItemStack, par2);
		return colors[par2 - 1];
	}
	
	public static int[] getColors(ItemStack is)
	{
		NBTTagCompound nbt = is.getTagCompound();
		if(nbt == null) is.setTagCompound(nbt = new NBTTagCompound());
		
		NBTTagCompound palette = nbt.getCompoundTag("palette");
		int[] colors = palette.getIntArray("colors");
		if(colors.length == 0)return new int[]{0xffffff,0xffffff,0xffffff,0xffffff,0xffffff,0xffffff};
		
		return colors;
	}
	
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
    {
		setColors(is, shift(getColors(is)));
        return is;
    } 
	
	public static int[] shift(int[] colors)
	{
		int t = colors[0];
		for(int i = 1; i < colors.length; i ++)
			colors[i-1] = colors[i];
		colors[colors.length-1]=t;
		
		return colors;
	}
	
	public static void setColors(ItemStack is, int[] colors)
	{
		NBTTagCompound nbt = is.getTagCompound();
		if(nbt == null) is.setTagCompound(nbt = new NBTTagCompound());
		
		NBTTagCompound palette = nbt.getCompoundTag("palette");
		palette.setIntArray("colors", colors);
		nbt.setCompoundTag("palette", palette);
	}
	
	public void addInformation(ItemStack is, EntityPlayer ep, List list, boolean help)
	{
		int color = getColors(is)[0];
		list.add("Alpha : " 	+ ((color >> 24) & 0xff));
		list.add("\u00a7cRed : " 	+ ((color >> 16) & 0xff));
		list.add("\u00a7aGreen : " 	+ ((color >>  8) & 0xff));
		list.add("\u00a79Blue : " 	+ ((color >>  0) & 0xff));
	}
}
