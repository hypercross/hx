package hx.survivalist.factor;

import java.util.Random;

import hx.survivalist.EffectEnforcing;
import hx.survivalist.ItemDrink;
import hx.survivalist.ModSurvivalist;
import hx.survivalist.PlayerFactor;
import hx.survivalist.PlayerStat;
import hx.utils.TickLooper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;

public class FactorHealth extends PlayerFactor{

	private static int AVERAGE ;
	private static int BETTER ;
	private static int WORSE ;

	private static Random rand = new Random();
	
	TickLooper tick = new TickLooper(20);	
	private PlayerFactor sleep, thirst;
	
	public FactorHealth() {
		super("health", ModSurvivalist.instance.health_max);
		value = max/2 + 1;
		AVERAGE = max/2+1;
		BETTER = max *3 / 4+1;
		WORSE = max /4+1;
	}

	@Override
	public int getFullColor() {
		return 0xffFF0000;
	}

	@Override
	public int getEmptyColor() {
		return 0xffFF0000;
	}

	@Override
	public int getLowSaturationColor() {
		return 0xffFF0000;
	}

	@Override
	public Icon getIcon() {
		return ItemDrink.health;
	}

	@Override
	public void onTick(EntityPlayer ep) {
		if(!tick.tick())return;
		
		if(sleep == null)
		{
			sleep = PlayerStat.getStat(ep).getFactor("sleep");
			thirst= PlayerStat.getStat(ep).getFactor("thirst");
		}
		
		//update values
		boolean goodDrink = thirst.getLevel() * 3  > thirst.getMaxLevel() * 2;
		goodDrink = goodDrink && thirst.getSaturation() >= thirst.getLevel() / 2;
		
		boolean badDrink = thirst.getLevel() < thirst.getMaxLevel() / 3;
		
		boolean goodSleep = sleep.getLevel() * 3  > sleep.getMaxLevel() * 2;
		goodSleep = goodSleep && sleep.getSaturation() > 0;
		
		boolean badSleep = sleep.getLevel() < sleep.getMaxLevel() / 3;
		
		boolean goodFood = ep.getFoodStats().getFoodLevel() >= 14;
		goodFood &= ep.getFoodStats().getSaturationLevel() > 0;
		
		boolean badFood = ep.getFoodStats().getFoodLevel() < 7;
		
		if(badSleep)delta_value--;
		if(goodSleep && goodDrink && goodFood)delta_value +=2 ;
		else if(badSleep  || badDrink  || badFood)delta_value -= 2;
		else if( value >= AVERAGE)delta_value --;
		else delta_value ++ ;
		
		//effects
		if(rand.nextBoolean() && rand.nextBoolean())
		{
			if(value >= BETTER)
				reportForEffect(EffectEnforcing.ON_HIGH_HEALTH);
			else if( value <= WORSE)
				reportForEffect(EffectEnforcing.ON_LOW_HEALTH);
		}
		
		
	}

	public int getSaturation()
	{
		return value;
	}
	
	public void onReset()
	{
	}
	
	public boolean enabled()
	{
		return ModSurvivalist.instance.enable_health;
	}
}
