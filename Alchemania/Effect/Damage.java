package hx.Alchemania.Effect;

import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class Damage extends AlchemaniaEffect{

	public String getName()
	{
		return "Damage";
	}
	
	protected void applyEffect(EntityLiving entity, AlchemaniaEffect eff)
	{
		entity.attackEntityFrom(DamageSource.magic, eff.grade);
	}
	
	public int color()
	{
		return Potion.harm.getLiquidColor();
	}
}
