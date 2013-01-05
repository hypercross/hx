package hx.Alchemania.Block;

import hx.Alchemania.Alchemania;
import hx.Alchemania.Effect.AlchemaniaEffect;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityAlchemyFurnace extends TileEntityFurnace{
	 /**
     * The ItemStacks that hold the items currently being used in the furnace
     */
    private ItemStack[] furnaceItemStacks = new ItemStack[3];
    public float smeltSpeed = -1;

    public TileEntityAlchemyFurnace()
    {
    	super();
        this.currentItemBurnTime = 200;
    }
    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return this.furnaceItemStacks[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.furnaceItemStacks[par1] != null)
        {
            ItemStack var3;

            if (this.furnaceItemStacks[par1].stackSize <= par2)
            {
                var3 = this.furnaceItemStacks[par1];
                this.furnaceItemStacks[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.furnaceItemStacks[par1].splitStack(par2);

                if (this.furnaceItemStacks[par1].stackSize == 0)
                {
                    this.furnaceItemStacks[par1] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.furnaceItemStacks[par1] != null)
        {
            ItemStack var2 = this.furnaceItemStacks[par1];
            this.furnaceItemStacks[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.furnaceItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return "alchemyFurnace";
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.furnaceItemStacks.length)
            {
                this.furnaceItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        this.furnaceCookTime = par1NBTTagCompound.getShort("CookTime");
        this.currentItemBurnTime = 200;
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("CookTime", (short)this.furnaceCookTime);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.furnaceItemStacks.length; ++var3)
        {
            if (this.furnaceItemStacks[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.furnaceItemStacks[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns an integer between 0 and the passed value representing how close the current item is to being completely
     * cooked
     */
    public int getCookProgressScaled(int par1)
    {
        return this.furnaceCookTime * par1 / 200;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
     * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
     */
    public int getBurnTimeRemainingScaled(int par1)
    {
    	int res = isBurning() ? par1-1: par1/2;
        return res;
    }

    /**
     * Returns true if the furnace is currently burning
     */
    public boolean isBurning()
    {
        int id = worldObj.getBlockId(this.xCoord,yCoord-1,zCoord);
        return id == Block.fire.blockID;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.isBurning() && this.canSmelt())
            {
            	if( willSmelter())
            	{
            		++this.furnaceCookTime;

            		if (this.furnaceCookTime == 200)
            		{
            			this.furnaceCookTime = 0;
            			this.smeltItem();
            			this.onInventoryChanged();
            		}
            	}
            }
            else
            {
                this.furnaceCookTime = 0;
            }
            
        }
        this.furnaceBurnTime = this.isBurning() ? this.currentItemBurnTime : 0;
    }
    
    public void onInventoryChanged()
    {
    	super.onInventoryChanged();
    	if(!canSmelt())return;
    	
    	int grade = -1;
    	AlchemaniaEffect[] effs = AlchemaniaEffect.parseEffects(furnaceItemStacks[0]);
    	for(AlchemaniaEffect eff: effs)if(eff.grade > grade)grade = eff.grade;
    	
    	if(grade>0)smeltSpeed = 1f/grade;
    	else smeltSpeed = 1f;
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canSmelt()
    {
    	int ingredient = 0;
    	int catalyst   = 0;
    	boolean outputEmpty   = false;
    	
        if (this.furnaceItemStacks[0] == null)
            ingredient = 0;
        else if (this.furnaceItemStacks[0].itemID == Alchemania.pill.shiftedIndex)
        	ingredient = 1;
        else if (this.furnaceItemStacks[0].itemID == Alchemania.solution.shiftedIndex)
        	ingredient = 2;
        
        if (this.furnaceItemStacks[1] == null)
        	catalyst = 0;
        else if (this.furnaceItemStacks[1].itemID == Item.redstone.shiftedIndex)
        	catalyst = 1;
        else if (this.furnaceItemStacks[1].itemID == Item.lightStoneDust.shiftedIndex)
        	catalyst = 1;
        else if (this.furnaceItemStacks[1].itemID == Item.dyePowder.shiftedIndex 
        		&& this.furnaceItemStacks[1].getItemDamage() == 4)
        	catalyst = 2;
        
        if (this.furnaceItemStacks[2] == null ) outputEmpty = true;
        
        if(!outputEmpty)return false;
        if(ingredient == 0)return false;
        return ingredient == catalyst;
    }
    
    private boolean willSmelter()
    {
    	if(smeltSpeed<0)onInventoryChanged();
    	return this.worldObj.rand.nextFloat() <= smeltSpeed;
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem()
    {
        if (this.canSmelt())
        {
        	furnaceItemStacks[2] = furnaceItemStacks[0].copy();
        	AlchemaniaEffect[] effs = AlchemaniaEffect.parseEffects(furnaceItemStacks[0]);
        	
        	if(furnaceItemStacks[1].itemID == Item.redstone.shiftedIndex)
        		for(AlchemaniaEffect eff: effs)eff.intensify();
        	else if(furnaceItemStacks[1].itemID == Item.lightStoneDust.shiftedIndex)
        		for(AlchemaniaEffect eff: effs)eff.prolong();        	
        	else if (this.furnaceItemStacks[1].itemID == Item.dyePowder.shiftedIndex 
            		&& this.furnaceItemStacks[1].getItemDamage() == 4)
        		for(AlchemaniaEffect eff: effs)eff.distill();
        	
        	AlchemaniaEffect.writeEffects(furnaceItemStacks[2], effs);
        	
            --this.furnaceItemStacks[0].stackSize;

            if (this.furnaceItemStacks[0].stackSize <= 0)
                this.furnaceItemStacks[0] = null;
            
            --this.furnaceItemStacks[1].stackSize;

            if (this.furnaceItemStacks[1].stackSize <= 0)
                this.furnaceItemStacks[1] = null;
        }
    }
}
