package hx.Alchemania.Effect;

import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class AlchemaniaPotionEffect extends AlchemaniaEffect{

	protected Potion potion;
	protected float slope = 1f;
	
	public AlchemaniaPotionEffect(Potion potion)
	{
		this.potion = potion;
		slope = 0.5f;
	}
	
	public AlchemaniaPotionEffect(Potion potion, float slope)
	{
		this.potion = potion;
		this.slope = slope;
	}
	
	@Override
	public String getName() {
		return potion.getName();
	}

	@Override
	public void applyEffect(EntityLiving entity, AlchemaniaEffect eff) {
		if(eff.grade<=0)return;
		
		float rawLevel = (eff.grade - 1) * slope;
		int level = (int) rawLevel + 1;
		float fade= level - rawLevel; 
		
		entity.addPotionEffect(
				new FadablePotionEffect(
						potion.getId(), 
						(int) (eff.duration * 20), 
						level,
						fade));
	}

	@Override
	public int color() {
		return potion.getLiquidColor();
	}

}
