package hx.survivalist;

import java.lang.reflect.Field;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class FactorWorldTickHandler implements IScheduledTickHandler{
	
	int light = -100;
	
	boolean isServer;
	
	public FactorWorldTickHandler(boolean b) {
		isServer = b;
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		EntityPlayer ep = (EntityPlayer)tickData[0];
		
		if(isServer)
		{
			if(ep.isPlayerSleeping())
			{
				light = ep.worldObj.skylightSubtracted;
				ep.worldObj.skylightSubtracted = 4;
			}
			return;
		}
		
		if(ep.getItemInUse() != null)
		{
			ItemStack itemstack = ep.inventory.getCurrentItem();

			if (itemstack == ep.getItemInUse())
			{
				if (ep.getItemInUseCount() == 1)
				{
					MinecraftForge.EVENT_BUS.post(new BeforeEntityFinishUseItemEvent(ep));
				}
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		EntityPlayer ep = (EntityPlayer)tickData[0];

		if(isServer)
		{
			if(light != -100)
			{
				ep.worldObj.skylightSubtracted = light;
				light = -100;
			}
			return;
		}
		PlayerStat.getStat(ep).onTick(ep);
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "Factor World Tick";
	}

	@Override
	public int nextTickSpacing() {
		return 0;
	}

}
