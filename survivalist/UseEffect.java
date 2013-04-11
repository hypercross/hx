package hx.survivalist;

import hx.utils.Debug;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class UseEffect {

	private static String[] potionNames = new String[]
			{
		"",
		"moveSpeed",
		"moveSlowdown",
		"digSpeed",
		"digSlowdown",
		"damageBoost",
		"heal",
		"harm",
		"jump",
		"confusion",
		"regeneration",
		"resistance",
		"fireResistance",
		"waterBreathing",
		"invisibility",
		"blindness",
		"nightVision",
		"hunger",
		"weakness",
		"poison",
		"wither"
			};

	public String stat;
	public int saturation;
	public int reserve;
	public int value;

	public int potionId = 0;
	public boolean potionIncremental = false;
	public int potionAmplifier = 1;
	public int potionDuration = 0;

	public int conditionChance = 100;	
	public String conditionStat;
	public int potionConditionId = 0;
	public int conditionMin = Integer.MIN_VALUE;
	public int conditionMax = Integer.MAX_VALUE;

	public static int toId(String name)
	{
		for(int i = 0;i<potionNames.length;i++)
			if(potionNames[i].equals(name))
			{
				return i;
			}
		return 0;
	}

	public static UseEffect fromString(String string)
	{
		string = string.trim();
		string = string.replaceAll("\\s+", ",");
		String[] values = string.split(",");
		if(values.length < 3)return null;

		UseEffect effect = new UseEffect();
		effect.stat = values[2];
		if(values[1].equals("saturation"))
			effect.saturation = Integer.parseInt(values[0]);
		else if(values[1].equals("value"))
			effect.value = Integer.parseInt(values[0]);
		else if(values[1].equals("reserve"))
			effect.reserve = Integer.parseInt(values[0]);
		else if(values[1].equals("potion"))
		{
			effect.potionDuration = Integer.parseInt(values[0]);
			effect.potionId = toId(effect.stat);
		}

		for(int i = 3;i<values.length;i++)
		{
			if(values[i].equals("incremental"))effect.potionIncremental = true;
			else if(values[i].startsWith("lv."))effect.potionAmplifier = Integer.parseInt(values[i].substring(3));
			else if(values[i].endsWith("%"))
				effect.conditionChance = Integer.parseInt(values[i].substring(0,values[i].length() - 1));
			else if(values[i].contains("<"))
			{
				String[] parts = values[i].split("<");
				int id = toId(parts[0]);
				if(id > 0)effect.potionConditionId = id;
				else effect.conditionStat = parts[0];
				effect.conditionMax = Integer.parseInt(parts[1]);
			}else if(values[i].contains(">"))
			{
				String[] parts = values[i].split(">");
				int id = toId(parts[0]);
				if(id > 0)effect.potionConditionId = id;
				else effect.conditionStat = parts[0];
				effect.conditionMin = Integer.parseInt(parts[1]);
			}
		}

		return effect;
	}

	public String toString()
	{
		String effs = effectToString() + " ";
		if(conditionChance < 100)effs += " " + conditionChance + "%";
		if(conditionStat!= null)effs += conditionStat;
		if(potionConditionId>0)effs += potionNames[potionConditionId];
		
		if(conditionMax != Integer.MAX_VALUE)effs+= "<" + conditionMax;
		if(conditionMin != Integer.MIN_VALUE)effs+= ">" + conditionMin;
		
		return effs;
	}
	
	public String effectToString()
	{
		if(saturation != 0)return saturation + " saturation " + stat;
		if(value != 0)return value + " value " + stat;
		if(reserve != 0)return reserve + " reserve " + stat;
		return stat + " " + potionDuration + " lv." + potionAmplifier + " " + (potionIncremental ? "inc" : "");
		//return "unknown effect";
	}

	public void applyOn(EntityPlayer ep)
	{
		if(this.potionConditionId>0)
		{
			int level = 0;
			PotionEffect pe = ep.getActivePotionEffect(Potion.potionTypes[potionConditionId]);
			if(pe != null)level = pe.getAmplifier() + 1;

			if(level >= conditionMax)return;
			if(level <= conditionMin)return;
		}else if(this.conditionStat != null)
		{
			int level = PlayerStat.getStat(ep).getFactor(conditionStat).value;
			if(level>=conditionMax)return;
			if(level<=conditionMin)return;
		}

		boolean hit = ep.worldObj.rand.nextInt(100) < conditionChance;
		if(!hit)return;

		if(stat.equals("food"))
		{
			ep.getFoodStats().addStats(value, saturation);
			return;
		}		

		if(potionId != 0)
		{
			if(ep.worldObj.isRemote)return;

			int lv = this.potionAmplifier;
			if(potionIncremental)
			{
				PotionEffect current = ep.getActivePotionEffect(Potion.potionTypes[potionId]);
				if(current != null) lv += current.getAmplifier() + 1;
			}

			if(lv >0 )
				ep.addPotionEffect(new PotionEffect(potionId,potionDuration,lv-1));
			else ep.removePotionEffect(potionId);

			return;
		}

		PlayerFactor thirst = PlayerStat.getStat(ep).getFactor(this.stat);
		thirst.value += this.value;
		thirst.saturation += this.saturation;
		if((thirst.reserve > 0) != this.reserve > 0)thirst.reserve += this.reserve;
		else if(thirst.reserve > 0 )thirst.reserve = Math.max(thirst.reserve, this.reserve);
		else thirst.reserve = Math.min(thirst.reserve, this.reserve);
	}
}
