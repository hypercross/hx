package hx.Alchemania.Effect;

import java.util.ArrayList;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Curse extends AlchemaniaEffect{

	public String getName()
	{
		return "Curse";
	}
	
	protected void applyEffect(EntityLiving entity, AlchemaniaEffect eff)
	{
		ArrayList<Integer> cureIDs = new ArrayList<Integer>();
		ArrayList<Integer> extendIDs = new ArrayList<Integer>();
		for(Object obj : entity.getActivePotionEffects())
		{
			PotionEffect pe = (PotionEffect) obj;
			if(pe == null)continue;
			
			int id = pe.getPotionID();
			if(pe.getAmplifier() < eff.grade)
			{
				if(!Potion.potionTypes[id].isBadEffect())cureIDs.add(id);
				else extendIDs.add(id);
			}
		}
		
		for(int id : cureIDs)
			entity.removePotionEffect(id);
		for(int id : extendIDs)
		{
			PotionEffect ori = entity.getActivePotionEffect(Potion.potionTypes[id]);
			PotionEffect pe = new PotionEffect(id, 
					(int) (ori.getDuration() + eff.duration*20), 
					ori.getAmplifier());
			entity.addPotionEffect(pe);
		}
	}
	
	public int color()
	{
		return 0x111111;
	}
}
