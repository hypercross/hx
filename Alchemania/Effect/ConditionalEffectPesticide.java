package hx.Alchemania.Effect;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;

public class ConditionalEffectPesticide extends ConditionalEffect {

	private Class creature;
	
	public String getName()
	{
		return creature.getSimpleName() +"_"+ positive.getName();
	}
	
	
	public ConditionalEffectPesticide(Class creatureClass,AlchemaniaEffect nested,
			AlchemaniaEffect negative) 
	{
		super(nested, negative);
		creature = creatureClass;		
	}

	protected boolean isValid(EntityLiving entity, AlchemaniaEffect eff)
	{
		return creature.isInstance(entity);
	}
	
}
