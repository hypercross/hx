package hx.Alchemania.Effect;

import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class Heal extends AlchemaniaEffect {
	public String getName()
	{
		return "Heal";
	}
	
	protected void applyEffect(EntityLiving entity, AlchemaniaEffect eff)
	{
		int amount = Math.min(eff.grade, entity.getMaxHealth() - entity.getHealth());
		entity.heal(amount);
	}
	
	public int color()
	{
		return Potion.heal.getLiquidColor();
	}
}
