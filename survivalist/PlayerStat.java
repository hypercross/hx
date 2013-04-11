package hx.survivalist;

import hx.survivalist.factor.FactorHealth;
import hx.survivalist.factor.FactorSleep;
import hx.survivalist.factor.FactorStamina;
import hx.survivalist.factor.FactorThirst;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PlayerStat {

	static HashMap<String,PlayerStat> stats = new HashMap<String,PlayerStat>();

	private HashMap <String,PlayerFactor> factors;

	public PlayerStat() {
		factors = new HashMap <String,PlayerFactor>();

		PlayerFactor factor = new FactorHealth();
		factors.put(factor.name, factor);
		
		factor = new FactorSleep();
		factors.put(factor.name, factor);
		
		factor = new FactorStamina();
		factors.put(factor.name, factor);
		
		factor = new FactorThirst();
		factors.put(factor.name, factor);
	}
	
	public static PlayerStat getStat(EntityPlayer ep)
	{
		if(!stats.containsKey(ep.username))
			stats.put(ep.username, new PlayerStat());
		return stats.get(ep.username);
	}

	public PlayerFactor getFactor(String name)
	{
		return factors.get(name);
	}

	public void onTick(EntityPlayer ep )
	{
		for(PlayerFactor pf : factors.values())
		{
			if(!pf.enabled())continue;
			pf.delta_value = 0;
			pf.delta_saturation = 0;
			pf.onTick(ep);
			
			if(pf.rate > 0)
			{
				int mod = 0; 
				if(pf.reserve > 0)mod = Math.min(pf.reserve, pf.rate);
				if(pf.reserve < 0)mod = - Math.min(- pf.reserve, pf.rate);
				
				if(mod > 0 && mod + pf.value > pf.max)mod = Math.max(pf.max - pf.value,0);
				if(mod < 0 && mod + pf.value < 0 )mod = Math.min(0,- pf.value);
				
				pf.reserve -= mod;
				pf.delta_value   += mod;
			}
		}
		for(PlayerFactor pf : factors.values())
		{
			if(!pf.enabled())
			{
				pf.onReset();
				continue;
			}
			pf.value += pf.delta_value;
			pf.saturation += pf.delta_saturation;
			
			if(pf.value > pf.max)pf.value = pf.max;
			if(pf.value < 0)pf.value = 0;
			if(pf.getSaturation() > pf.value)pf.saturation = pf.value;
			pf.checkAndUpdate(ep);
		}
	}

	public Collection<PlayerFactor> factors() {
		return factors.values();
	}
	
	public void fromNBT(NBTTagCompound nbt)
	{
		for(PlayerFactor pf : factors.values())
		{
			if(!pf.enabled())continue;
			NBTTagCompound compound = nbt.getCompoundTag(pf.name);
			pf.value = compound.getInteger("value");
			pf.saturation = compound.getInteger("saturation");
			pf.reserve = compound.getInteger("reserve");
		}
	}
	
	public void toNBT(NBTTagCompound nbt)
	{
		for(PlayerFactor pf : factors.values())
		{
			if(!pf.enabled())continue;
			NBTTagCompound compound = nbt.getCompoundTag(pf.name);
			compound.setInteger("value", pf.value);
			compound.setInteger("saturation", pf.saturation);
			compound.setInteger("reserve", pf.reserve);
			nbt.setCompoundTag(pf.name, compound);
		}
	}

	public static void removePlayer(EntityPlayer player) {
		stats.remove(player.username);
	}
}
