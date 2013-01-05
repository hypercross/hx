package hx.Alchemania.Effect;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.PotionEffect;

public class FadablePotionEffect extends PotionEffect{

	public int fadeTime;
	
	public FadablePotionEffect(int par1, int par2, int par3, float fade) {
		super(par1, par2, par3);
		fadeTime = (int) (par2 * fade);
	}

	public boolean onUpdate(EntityLiving par1EntityLiving)
	{
		if(this.getDuration() < fadeTime)
		{
			fadeTime = 0;
			int amp = Math.max(this.getAmplifier() -1, 0);
			ObfuscationReflectionHelper.setPrivateValue(PotionEffect.class, this,  (Integer)amp, 2);
			
			if(!ObfuscationReflectionHelper.obfuscation)
				ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, par1EntityLiving, true, "potionsNeedUpdate");
			else
				ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, par1EntityLiving, true, "h");
		}
		return super.onUpdate(par1EntityLiving);
	}
}
