package hx.Alchemania.Effect;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class Feed extends AlchemaniaEffect{

	public String getName()
	{
		return "Feed";
	}
	
	protected void applyEffect(EntityLiving entity, AlchemaniaEffect eff)
	{
		if(! (entity instanceof EntityPlayer))return;
		EntityPlayer ep = (EntityPlayer) entity;
		
		ep.getFoodStats().addStats(eff.grade, 0);
	}
	
	public int color()
	{
		return 0xFAB84D;
	}
}
