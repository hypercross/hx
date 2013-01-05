package hx.Alchemania.Item;

import hx.Alchemania.Alchemania;
import hx.Alchemania.Effect.AlchemaniaEffect;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemIngredientPowder extends ItemFood{

	public ItemIngredientPowder(int id) {
		super(id, 0,0,false);
		maxStackSize = 64;
        //setCreativeTab(CreativeTabs.tabMisc);
        setIconIndex(78);
        setItemName("ingredientPowder");
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}
	
	public void addInformation(ItemStack is, EntityPlayer ep, List list, boolean adv)
	{
		AlchemaniaEffect[] effs = AlchemaniaEffect.parseEffects(is);
		if(effs == null)return;
		for (AlchemaniaEffect ae : effs)
		{
			list.add(ae.getDispString());
		}
	}
	
	public ItemStack onFoodEaten(ItemStack is, World par2World, EntityPlayer par3EntityPlayer)
    {
		AlchemaniaEffect[] effs = AlchemaniaEffect.parseEffects(is);
		if (!par3EntityPlayer.capabilities.isCreativeMode)
			is.stackSize--;
		if(effs==null)
		{
			par2World.playSoundAtEntity(par3EntityPlayer, "random.burp", 0.5F, par2World.rand.nextFloat() * 0.1F + 0.9F);
			return is;
		}
        
        boolean effective = false;
        for(AlchemaniaEffect ae : effs)
        	effective |= ae.applyEffect(par3EntityPlayer);
        
        if(!effective)
    		par2World.playSoundAtEntity(par3EntityPlayer, "random.burp", 0.5F, par2World.rand.nextFloat() * 0.1F + 0.9F);
        
        return is;
    }
	
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 16;
    }
	
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack is, int par2)
    {
        int i = 0;
        AlchemaniaEffect[] effs = AlchemaniaEffect.parseEffects(is);
        if(effs == null)return super.getColorFromItemStack(is, par2);
        int l = effs.length;
        for(AlchemaniaEffect ae : effs)
        {
        	i += ae.color()/l;
        }
        
        return i;
    }
}
