package hx.survivalist;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.network.Player;
import hx.utils.IHandledPacket;

public class EffectEnforcing implements IHandledPacket{
	
	public static byte
	ON_LOW_STAMINA = 0,
	ON_LOW_SLEEP   = 1,
	ON_LOW_HEALTH  = 2,
	ON_HIGH_HEALTH = 3,
	ON_LOW_THIRST  = 4
	;

	public byte effectType;
	
	
	@Override
	public boolean isActive(String channel) {
		return channel.equals("effect_enforcing");
	}

	@Override
	public void onPacketReceived(INetworkManager manager, Player player) {
		EntityPlayer ep = (EntityPlayer)player;
		if(ep.capabilities.isCreativeMode)return;
		if(effectType == ON_LOW_STAMINA)
		{
			ep.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 100, 4, true));
			ep.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1, true));
			ep.addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 1, true));
		}else if(effectType == ON_LOW_SLEEP)
		{
			ep.addPotionEffect(new PotionEffect(Potion.confusion.id, 100, 0, true));
			ep.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0, true));
		}else if(effectType == ON_HIGH_HEALTH || effectType == ON_LOW_HEALTH)
		{
			int extend = effectType == ON_HIGH_HEALTH ? 20 : -20;
			
			for(Object effect : ep.getActivePotionEffects())
			{
				PotionEffect pe = (PotionEffect)effect;
				if(!Potion.potionTypes[pe.getPotionID()].isBadEffect())
				{
					pe.duration += extend;
					ep.addPotionEffect(pe);
					break;
				}else 
				{
					pe.duration -= extend;
					ep.addPotionEffect(pe);
					break;
				}
			}
		}else if(effectType == ON_LOW_THIRST)
		{
			ep.attackEntityFrom(DamageSource.starve, 1);
		}
	}

}
