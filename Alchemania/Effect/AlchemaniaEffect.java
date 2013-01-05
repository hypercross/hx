package hx.Alchemania.Effect;

import hx.Alchemania.Alchemania;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.StringUtils;

public class AlchemaniaEffect {
	
	public static AlchemaniaEffect[] effects;
	private static Random rnd = new Random();
	
	public static byte
	DAMAGE	= 0,
	SPEED  	= 1,
	SLOW	= 2,
	DIGFAST	= 3,
	DIGSLOW	= 4,
	DMGUP	= 5,
	HEAL	= 6,
	JUMP	= 7,
	NAUSEA	= 8,
	REGEN	= 9,
	RESIST	= 10,
	FIRERST	= 11,
	BREATH	= 12,
	INVSBLE	= 13,
	BLIND	= 14,
	NIGHTV	= 15,
	HUNGER 	= 16,
	WEAK	= 17,
	POISON	= 18,
	FEED	= 19,
	BLESS	= 20,
	CURSE	= 21,
	
	
	CREEPER_CIDE 	= 30,
	ENDER_CIDE		= 31;
	
	public static void load()
	{
		effects = new AlchemaniaEffect[64];
		effects[DAMAGE] = new AlchemaniaPotionEffectInstant(Potion.harm,0.5f);
		effects[SPEED]  = new AlchemaniaPotionEffect(Potion.moveSpeed);
		effects[SLOW]  = new AlchemaniaPotionEffect(Potion.moveSlowdown);
		effects[DIGFAST]  = new AlchemaniaPotionEffect(Potion.digSpeed);
		effects[DIGSLOW]  = new AlchemaniaPotionEffect(Potion.digSlowdown);
		effects[DMGUP]	= new  AlchemaniaPotionEffect(Potion.damageBoost);
		effects[HEAL]	= new  AlchemaniaPotionEffectInstant(Potion.heal,0.5f);
		effects[JUMP]	= new  AlchemaniaPotionEffect(Potion.jump);
		effects[NAUSEA]	= new  AlchemaniaPotionEffect(Potion.confusion);
		effects[REGEN]	= new  AlchemaniaPotionEffect(Potion.regeneration);
		effects[RESIST]	= new  AlchemaniaPotionEffect(Potion.resistance);
		effects[FIRERST]	= new  AlchemaniaPotionEffect(Potion.fireResistance);
		effects[BREATH]	= new  AlchemaniaPotionEffect(Potion.waterBreathing);
		effects[INVSBLE]	= new  AlchemaniaPotionEffect(Potion.invisibility);
		effects[BLIND]	= new  AlchemaniaPotionEffect(Potion.blindness);
		effects[NIGHTV]	= new  AlchemaniaPotionEffect(Potion.nightVision);
		effects[HUNGER]	= new  AlchemaniaPotionEffect(Potion.hunger);
		effects[WEAK]	= new  AlchemaniaPotionEffect(Potion.weakness);
		effects[POISON]	= new  AlchemaniaPotionEffect(Potion.poison);
		effects[FEED]	= new Feed();
		effects[BLESS]	= new Bless();
		effects[CURSE]	= new Curse();
		
		effects[CREEPER_CIDE] = new ConditionalEffectPesticide(
				EntityCreeper.class, 
				new AlchemaniaPotionEffect(Potion.harm),
				null);
		
		effects[ENDER_CIDE] = new ConditionalEffectPesticide(
				EntityEnderman.class, 
				new AlchemaniaPotionEffect(Potion.harm),
				null);
	}
	
	public byte grade = 0;	//effect amount, from 0 to 127
	public float duration = 0f;	//effect duration, from 0 to inf
	public float purity = 0f;	//effective chance, 0 to 1
	public byte type = 0;		//effect id
	public byte saturation = 0;	//improvable amount by brewing, 
	
	public AlchemaniaEffect copy() {
		AlchemaniaEffect eff = new AlchemaniaEffect();
		eff.grade 		= this.grade;
		eff.duration 	= this.duration;
		eff.purity 		= this.purity;
		eff.type		= this.type;
		eff.saturation	= this.saturation;
		return eff;
	}

	public void distill()
	{
		float amount = Math.min(purity, (1f - purity)*.5f);
		purity += amount;
	}
	
	public void powderize(float inpurity)
	{
		this.purity *= 1f - inpurity;
		this.grade++;
		this.duration/=2;
		this.saturation=0;
	}
	
	public void solidify()
	{
		this.grade--;
		this.duration*=2;
	}
	
	public void mixWith(AlchemaniaEffect ae)
	{
		float avgPurity = purity*.5f + ae.purity*.5f;
		byte avgGrade = (byte) ((grade*purity*.5f + ae.grade*ae.purity*.5f)/avgPurity);
		this.purity = avgPurity;
		this.grade = avgGrade;
	}
	
	public void prolong()
	{
		float multiplier = (float) Math.sqrt((2 + saturation)/(1 + saturation));
		this.duration *= multiplier;
		saturation ++;
	}
	
	public void intensify()
	{
		if(saturation == 0)this.grade++ ;
		saturation ++;
	}
	
	public void solute()
	{
		this.grade--;
	}
	
	public String getDispString()
	{
		return ("\u00A73"+(int) (purity*100)) + "% " + 
				"\u00A79" + getName() + 
				"\u00A75 LV" + grade +
				(duration > 0 ? 
			   " \u00A7c" + StringUtils.ticksToElapsedTime((int) (duration * 20))
				: "");
	}
	
	public void writeTo(NBTTagCompound nbt)
	{
		nbt.setByte("grade", grade);
		nbt.setFloat("purity", purity);
		nbt.setFloat("duration", duration);
		nbt.setByte("type", type);
		nbt.setByte("saturation", saturation);
	}
	
	public void readFrom(NBTTagCompound nbt)
	{
		grade = nbt.getByte("grade");
		purity = nbt.getFloat("purity");
		duration = nbt.getFloat("duration");
		type = nbt.getByte("type");
		saturation = nbt.getByte("saturation");
	}
	
	private String toSec(float time)
	{
		String str = "";
		float min = time/60f;
		return (int)min + ":"+ (int)time % 60;
	}
	
	public String getName()
	{
		return StatCollector.translateToLocal(effects[this.type].getName()).trim();
	}
	
	public boolean applyEffect(EntityLiving entity)
	{
		byte count = 0;
		for(int i =0;i<grade;i++)
			if(rnd.nextFloat() <= this.purity)count++;
		
		
		if(count > 0)
		{
			AlchemaniaEffect ae = this.copy();
			ae.grade = count;
			effects[this.type].applyEffect(entity,ae);
			return true;
		}
		return false;
	}
	
	protected void applyEffect(EntityLiving entity, AlchemaniaEffect eff)
	{
	}
	
	public int color()
	{
		return effects[this.type].color();
	}
	
	public String toString()
	{
		return getDispString();
	}
	
	public static AlchemaniaEffect[] parseEffects(ItemStack is)
	{
		if(!is.hasTagCompound())return null;
		NBTTagCompound nbt = is.stackTagCompound.getCompoundTag("AME");
		if(nbt == null)return null;
		
		AlchemaniaEffect[] effects;
		byte length = nbt.getByte("num");
		effects = new AlchemaniaEffect[length];
		
		for(int i = 0; i < length;i++)
		{
			effects[i] = new AlchemaniaEffect();
			effects[i].readFrom(nbt.getCompoundTag(String.valueOf(i)));
		}
		
		return effects;
	}
	
	public static void writeEffects(ItemStack is, AlchemaniaEffect[] effs)
	{
		NBTTagCompound alcNBT = new NBTTagCompound();
		
		alcNBT.setByte("num", (byte) effs.length);
		for(int j=0;j<effs.length;j++)
		{
			NBTTagCompound effNBT = new NBTTagCompound();
			effs[j].writeTo(effNBT);
			alcNBT.setCompoundTag(String.valueOf(j), effNBT);
		}
		
		if(!is.hasTagCompound())is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setCompoundTag("AME", alcNBT);
	}
}

