package hx.survivalist;

import hx.utils.Debug;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityTable extends TileEntity{

	class ContainerWithALongNameAndDoesNothingItJustHasALongNameItLikesLongNames extends Container
	{

		@Override
		public boolean canInteractWith(EntityPlayer entityplayer) {
			return true;
		}

	}

	private InventoryCrafting ic = new InventoryCrafting(new ContainerWithALongNameAndDoesNothingItJustHasALongNameItLikesLongNames(), 2, 2);
	public float yaw[] = new float[4];

	public ItemStack get(int i )
	{
		return ic.getStackInSlot(i);
	}

	public void set(int i , ItemStack is)
	{
		ic.setInventorySlotContents(i, is);
	}

	public int size()
	{
		return ic.getSizeInventory();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		for(int i = 0;i<ic.getSizeInventory();i++)
		{
			ItemStack is = ic.getStackInSlot(i);
			if(is != null)
			{
				nbt.setCompoundTag("item" + i, is.writeToNBT(new NBTTagCompound()));
				nbt.setFloat("yaw" + i, yaw[i]);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		for(int i = 0;i<ic.getSizeInventory();i++)
		{
			ItemStack is = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("item" + i));
			ic.setInventorySlotContents(i, is);
			yaw[i] = nbt.getFloat("yaw" + i);
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		readFromNBT(pkt.customParam1);
	}

	public boolean interact(EntityPlayer ep, int slot)
	{
		ItemStack inhand = ep.getCurrentEquippedItem();
		ItemStack inslot = ic.getStackInSlot(slot);

		if(inhand == null && inslot == null)return false;

		if(inhand == null || inslot == null)
		{
			if(inslot!=null)
				{
				ep.setCurrentItemOrArmor(0, inslot);
				set(slot,null);
				}
			else ic.setInventorySlotContents(slot, inhand.splitStack(1));
			return true;
		}

		//craft with just inhand and inslot
		ItemStack[] temp = new ItemStack[this.size()];
		for(int i = 0;i<this.size();i++)
		{
			temp[i] = this.get(i);
			if(i != slot)set(i, null);
		}
		set( (slot + 1) % this.size(), inhand);
		ItemStack out = CraftingManager.getInstance().findMatchingRecipe(ic, ep.worldObj);
		if(out != null && inhand.stackSize < 2)
		{
			ep.setCurrentItemOrArmor(0, craftingConsume(inhand));
			for(int i = 0;i<this.size();i++)
			{
				if(i == slot)
				{
					set(i,out);
				}else
				{
					set(i,temp[i]);
				}
			}
			addItemStack(craftingConsume(temp[slot]), true);
			return true;
		}else
		{
			for(int i = 0;i<this.size();i++)set(i,temp[i]);
		}


		//craft with on table
		boolean flag = addItemStack(inhand.splitStack(1), false); 
		if(!flag)
			inhand.stackSize++;

		out = CraftingManager.getInstance().findMatchingRecipe(ic, ep.worldObj);
		if(out != null)
		{
			ItemStack pop = null;
			for(int i = 0;i< size();i++)
			{
				if(i == slot)
				{
					pop = craftingConsume(get(i));
					set(i, out);
				}else
					set(i, craftingConsume(get(i)));
			}
			addItemStack(pop,true);
			return true;
		}
		return flag;
	}

	public ItemStack craftingConsume(ItemStack inhand)
	{
		if(inhand == null)return null;
		inhand = inhand.getItem().getContainerItemStack(inhand);
		if(inhand != null)
			if(inhand.isItemStackDamageable() && inhand.getMaxDamage() < inhand.getItemDamage())inhand = null;

		return inhand;
	}

	public boolean addItemStack(ItemStack is, boolean spawn)
	{
		if(is== null)return true;
		for(int i = 0 ;i < ic.getSizeInventory();i++ )
		{
			if(ic.getStackInSlot(i) == null)
			{
				ic.setInventorySlotContents(i, is);
				return true;
			}
		}

		if(!spawn)return false;

		EntityItem ei = new EntityItem(this.worldObj, this.xCoord, this.yCoord,this.zCoord, is);
		this.worldObj.spawnEntityInWorld(ei);
		return false;
	}
}
