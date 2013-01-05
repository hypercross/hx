package hx.Alchemania.Item;

import java.util.Random;

import hx.Alchemania.Alchemania;
import hx.Alchemania.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;


public class ItemHandDispensor extends Item{

	ItemStack[] content;
	
	public ItemHandDispensor(int par1) {
		super(par1);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
		setItemName("handDispenser");
		setIconIndex(5);
	}
	
	 public String getTextureFile()
	 {
		return Alchemania.MAIN_TEXTURE;
		 
	 }
	
	  public int getMaxItemUseDuration(ItemStack par1ItemStack)
	  {
	    return 1;
	  }

	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep)
    {
        if(ep.isSneaking() && !w.isRemote)
        {	
        	ep.openGui(Alchemania.instance, Alchemania.HandDispenserGUI, w, 0,0,0);
        }else if(!w.isRemote)
        {
        	InventoryHandDispenser ihd = new InventoryHandDispenser(is);
        	 
        	int dispID = ihd.getRandomStackFromInventory();
        	if(dispID<0)
        	{
        		w.playAuxSFX(1001, (int)ep.posX, (int)ep.posY, (int)ep.posZ, 0);
        	}else
        	{
        		
        		ItemStack toDisp = ihd.getStackInSlot(dispID);
        		
        		IBehaviorDispenseItem idi = (IBehaviorDispenseItem)
        				BlockDispenser.dispenseBehaviorRegistry.func_82594_a(toDisp.getItem());
        		if(toDisp.itemID == Block.tnt.blockID)
        			idi = new BehaviorTNTDispense();

        		EventHandler.dispenser = ep;
        		toDisp = idi.dispense(new PlayerBlockSource(ep), toDisp);
        		ihd.setInventorySlotContents(dispID, toDisp.stackSize>0 ? toDisp : null);
        	}
        }
        return is;
    }
	
	private static class PlayerBlockSource implements IBlockSource
	{
		EntityPlayer ep;
		public PlayerBlockSource(EntityPlayer ep)
		{
			this.ep = ep;
		}
		
		@Override
		public World getWorld() {
			return ep.worldObj;
		}

		@Override
		public double getX() {
			return ep.posX - Math.sin(ep.rotationYaw / 180.0f * Math.PI);
		}

		@Override
		public double getY() {
			return ep.posY + ep.getEyeHeight();
		}

		@Override
		public double getZ() {
			return ep.posZ + Math.cos(ep.rotationYaw / 180.0f * Math.PI);
		}

		@Override
		public int getXInt() {
			return (int) getX();
		}

		@Override
		public int getYInt() {
			return (int) getY();
		}

		@Override
		public int getZInt() {
			return (int) getZ();
		}

		@Override
		public int func_82620_h() {
			return 1;
		}

		@Override
		public TileEntity func_82619_j() {
			return null;
		}
		
	}

	public static class InventoryHandDispenser
    implements IInventory
  {
    ItemStack handDispenser;
    ItemStack[] items;
    Random random = new Random();

    InventoryHandDispenser(ItemStack ist)
    {
      this.handDispenser = ist;
      unpackInventory();
    }

    void unpackInventory() {
      this.items = new ItemStack[9];
      if (this.handDispenser.stackTagCompound == null)
        return;
      NBTTagList list = this.handDispenser.stackTagCompound.getTagList("contents");

      for (int i = 0; i < list.tagCount(); i++) {
        NBTTagCompound item = (NBTTagCompound)list.tagAt(i);

        int slt = item.getByte("Slot");
        if (slt < 9)
          this.items[slt] = ItemStack.loadItemStackFromNBT(item);
      }
    }

    void packInventory() {
      if (this.handDispenser.stackTagCompound == null) {
        this.handDispenser.setTagCompound(new NBTTagCompound());
      }

      NBTTagList contents = new NBTTagList();
      for (int i = 0; i < 9; i++)
        if (this.items[i] != null) {
          NBTTagCompound cpd = new NBTTagCompound();
          this.items[i].writeToNBT(cpd);
          cpd.setByte("Slot", (byte)i);
          contents.appendTag(cpd);
        }
      this.handDispenser.stackTagCompound.setTag("contents", contents);
    }
    public int getSizeInventory() {
      return 9;
    }
    public ItemStack getStackInSlot(int slot) {
      return this.items[slot];
    }

    public ItemStack decrStackSize(int slot, int num)
    {
      if (this.items[slot] == null) return null;
      if (this.items[slot].stackSize <= num) {
        ItemStack tr = this.items[slot]; this.items[slot] = null;
        onInventoryChanged();
        return tr;
      }
      ItemStack tr = this.items[slot].splitStack(num);
      if (this.items[slot].stackSize == 0)
        this.items[slot] = null;
      onInventoryChanged();
      return tr;
    }

    public ItemStack getStackInSlotOnClosing(int slot) {
      if (this.items[slot] == null) return null;
      ItemStack tr = this.items[slot];
      this.items[slot] = null;
      return tr;
    }

    public void setInventorySlotContents(int slot, ItemStack ist) {
      this.items[slot] = ist;
      if ((ist != null) && (ist.stackSize > getInventoryStackLimit()))
        ist.stackSize = getInventoryStackLimit();
      onInventoryChanged();
    }
    public String getInvName() { return "Hand Dispenser"; } 
    public int getInventoryStackLimit() { return 64; } 
    public void onInventoryChanged() {
      packInventory();
    }
    public boolean isUseableByPlayer(EntityPlayer pl) { return true; }


    public void openChest()
    {
    }

    public void closeChest()
    {
    }
    
    public int getRandomStackFromInventory()
    {
        int var1 = -1;
        int var2 = 1;

        for (int var3 = 0; var3 < this.items.length; ++var3)
        {
            if (this.items[var3] != null && this.random.nextInt(var2++) == 0)
            {
                var1 = var3;
            }
        }

        return var1;
    }
  }
}
