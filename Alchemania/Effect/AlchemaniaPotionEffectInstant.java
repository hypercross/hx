package hx.Alchemania.Effect;

import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StringUtils;

public class AlchemaniaPotionEffectInstant extends AlchemaniaPotionEffect {

	public AlchemaniaPotionEffectInstant(Potion potion) {
		super(potion);
	}
	
	public AlchemaniaPotionEffectInstant(Potion potion,float slope) {
		super(potion,slope);
	}
	
	@Override
	public void applyEffect(EntityLiving entity, AlchemaniaEffect eff) {
		if(eff.grade<=0)return;
		
		entity.addPotionEffect(
				new PotionEffect(
						potion.getId(), 
						1, 
						(int) ((eff.grade - 1) * slope)));
	}

}
