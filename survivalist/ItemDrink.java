package hx.survivalist;

import hx.utils.Debug;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemDrink extends Item{


	public static ItemDrink instance;
	public static Icon thirst, stamina, health, sleep; 

	static int numDrink = 128;

	public  static int usedNum = 0;	
	public static UseEffect[][] effects = new UseEffect[numDrink][20];
	public static int[] color = new int[numDrink];
	public static String[] names = new String[numDrink];

	public static String[] useCustomIcons = new String[numDrink];
	public static Icon[] custom_icon = new Icon[numDrink];
	public static boolean[] drawCustomContainer = new boolean[numDrink];

	public static final boolean[] eaten = new boolean[numDrink];
	public static final ItemStack[] giveback = new ItemStack[numDrink];
	public static boolean[] cascadedCrafting = new boolean[numDrink]; 

	public ItemDrink(int par1) {
		super(par1);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabBrewing);
		setUnlocalizedName("itemDrink");

		instance = this;
	}

	public int getNumEffects(ItemStack is)
	{
		int drinkID = is.getItemDamage() % numDrink;

		int count  =0;
		for(UseEffect eff : effects[drinkID])
		{
			if(eff == null)return count;
			count ++ ;
		}

		return count;
	}

	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 32;
	}

	public EnumAction getItemUseAction(ItemStack is)
	{
		return eaten[is.getItemDamage() % numDrink] ? EnumAction.eat : EnumAction.drink;
	}

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		if(getNumEffects(par1ItemStack) == 0)return par1ItemStack;

		if(par1ItemStack.getItemDamage() < numDrink)
		{
			return givenBack(par1ItemStack);
		}

		par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer ep, int count)
	{
		if(count > 0)return;

		if(getItemUseAction(is) == EnumAction.eat)return;

		int drinkID = is.getItemDamage();
		int drinkAmount = drinkID / numDrink;
		drinkID = drinkID % numDrink;

		if(drinkAmount == 0);
		else
		{
			for(UseEffect effect : effects[drinkID])
			{
				if(effect == null)break;
				effect.applyOn(ep);
			}
			drinkAmount-=1;
			is.setItemDamage(drinkAmount * numDrink + drinkID);
		}

		if(is.getItemDamage() < numDrink)
		{
			ItemStack newis = givenBack(is);
			if(newis != null && newis.stackSize<1)newis = null;
			ep.setCurrentItemOrArmor(0, newis);
		}
	}

	public boolean hasContainerItem()
	{
		return true;
	}

	public ItemStack givenBack(ItemStack is)
	{
		ItemStack nis = giveBack(is);
		if(nis.itemID == 0)
		{
			is.stackSize--;
			return is;
		}
		//		Debug.dafuq(nis);
		return nis;
	}

	private ItemStack giveBack(ItemStack is)
	{
		if(is.getItemDamage() >= numDrink && cascadedCrafting[is.getItemDamage() % numDrink])
		{
			is.setItemDamage(is.getItemDamage() - numDrink);
			return is;
		}
		
		if(giveback[is.getItemDamage() % numDrink] != null)return giveback[is.getItemDamage() % numDrink].copy();
		if(getNumEffects(is)>0 && !eaten[is.getItemDamage() % numDrink])
			return new ItemStack(Item.glassBottle);

		is.stackSize--;
		return is;
	}

	public int getMaxDamage()
	{
		int dmg = super.getMaxDamage();
		this.setMaxDamage(0);
		return dmg;
	}

	public ItemStack getContainerItemStack(ItemStack is)
	{
		ItemStack stack = givenBack(is);
		if(stack.stackSize > 0)return stack;

		this.setMaxDamage(1);
		return new ItemStack(this,1,numDrink-1);
	}

	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		if(eaten[par1ItemStack.getItemDamage() % numDrink])
			par2World.playSoundAtEntity(par3EntityPlayer, "random.burp", 0.5F, par2World.rand.nextFloat() * 0.1F + 0.9F);
		else 
		{
			onPlayerStoppedUsing(par1ItemStack, par2World, par3EntityPlayer, 0);
			return par1ItemStack;
		}

		for(UseEffect effect : effects[par1ItemStack.getItemDamage() % numDrink])
		{
			if(effect == null)break;
			effect.applyOn(par3EntityPlayer);
		}

		return givenBack(par1ItemStack);
	}

	public Icon getIconFromDamage(int par1)
	{
		return getIconFromDamageForRenderPass(par1, 0);
	}

	public Icon getIconFromDamageForRenderPass(int par1, int par2)
	{
		if(par2 != 0 && drawCustomContainer[par1 % numDrink] && giveback[ par1 % numDrink] != null)
		{
			return getIconFromDamageForRenderPass(giveback[ par1 % numDrink].getItemDamage(), par2);
		}
		if(useCustomIcons[par1 % numDrink] != null)
			return custom_icon[par1 % numDrink];
		return Item.potion.getIconFromDamageForRenderPass(0, par2);
	}

	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
	{
		if(par2 > 0)return 16777215;

		int drinkID = par1ItemStack.getItemDamage() % numDrink;		

		return color[drinkID] > 0 ? color[drinkID] : 0xffffff;
	}

	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	public String getItemDisplayName(ItemStack par1ItemStack)
	{
		return names[par1ItemStack.getItemDamage() % numDrink];
	}

	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		if(getNumEffects(par1ItemStack) == 0)return;
		if(getItemUseAction(par1ItemStack) == EnumAction.eat)return;

		int id = par1ItemStack.getItemDamage() % numDrink;
		int amount = par1ItemStack.getItemDamage() / numDrink;

		par3List.add("Uses:" + amount);

		if(!par4)return;
		for(UseEffect eff : effects[id])
		{
			if(eff == null)break;
			par3List.add(eff.toString());
		}
	}

	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		if(par2CreativeTabs != null && par2CreativeTabs.equals(CreativeTabs.tabBrewing))
		{
			for(int i = 0;i<usedNum;i++)
				par3List.add(new ItemStack(this.itemID, 1, 4 * numDrink + i));
		}
	}

	public void updateIcons(IconRegister par1IconRegister)
	{
		thirst = par1IconRegister.registerIcon("survivalist:thirst");
		stamina = par1IconRegister.registerIcon("survivalist:stamina");
		health = par1IconRegister.registerIcon("survivalist:health");
		sleep = par1IconRegister.registerIcon("survivalist:sleep");

		for(int i = 0;i< usedNum ; i++)if(this.useCustomIcons[i] != null)
			custom_icon[i] = par1IconRegister.registerIcon("survivalist:" + this.useCustomIcons[i]);
	}

	public boolean isLookingAtWater(EntityPlayer ep)
	{
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(ep.worldObj, ep, true);

		if (mop == null) return false;
		if(mop.typeOfHit != EnumMovingObjectType.TILE)return false;

		int i = mop.blockX;
		int j = mop.blockY;
		int k = mop.blockZ;

		if(!ep.worldObj.canMineBlock(ep, i,j,k))return false;
		if(ep.worldObj.getBiomeGenForCoords(i, k).biomeName.equals("Ocean"))return false;
		if(ep.worldObj.getBlockMaterial(i, j, k)!=Material.water)return false;

		return true;
	}
}
