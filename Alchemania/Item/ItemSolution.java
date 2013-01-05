package hx.Alchemania.Item;

import hx.Alchemania.Effect.AlchemaniaEffect;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSolution extends ItemIngredientPowder{

	public ItemSolution(int par1) {
		super(par1);
		maxStackSize = 1;
		setItemName("solution");
		setContainerItem(Item.glassBottle);
	}

	public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 32;
    }
	
	public ItemStack onFoodEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		ItemStack is = super.onFoodEaten(par1ItemStack, par2World, par3EntityPlayer);
		if (!par3EntityPlayer.capabilities.isCreativeMode)
			return new ItemStack(Item.glassBottle);
		else return is;
	}
	
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.drink;
    }

	public int getIconFromDamage(int par1)
    {
        return 140;
    }

    @SideOnly(Side.CLIENT)
    public int getIconFromDamageForRenderPass(int par1, int par2)
    {
        return par2 == 0 ? 141 : super.getIconFromDamageForRenderPass(par1, par2);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return par2 > 0 ? 16777215 : super.getColorFromItemStack(par1ItemStack, par2);
    }
}
