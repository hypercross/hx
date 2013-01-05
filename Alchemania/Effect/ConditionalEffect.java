package hx.Alchemania.Effect;

import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;

public class ConditionalEffect extends AlchemaniaEffect {
	
	protected AlchemaniaEffect positive,negative;
	
	public ConditionalEffect(AlchemaniaEffect nested, AlchemaniaEffect negative)
	{
		positive = nested;
		this.negative = negative;
	}
	
	public String getName()
	{
		return positive.getName();
	}
	
	protected boolean isValid(EntityLiving entity, AlchemaniaEffect eff)
	{
		return true;
	}
	
	protected void applyEffect(EntityLiving entity, AlchemaniaEffect eff)
	{
		if(isValid(entity,eff))
			positive.applyEffect(entity,eff);
		else if(negative != null)
			negative.applyEffect(entity,eff);
	}
	
	public int color()
	{
		return positive.color();
	}
}
