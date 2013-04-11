package hx.survivalist.factor;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayerMP;
import hx.survivalist.EffectEnforcing;
import hx.survivalist.ItemDrink;
import hx.survivalist.ModSurvivalist;
import hx.survivalist.PlayerFactor;
import hx.survivalist.PlayerStat;
import hx.utils.PacketHelper;
import hx.utils.TickLooper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;

public class FactorThirst extends PlayerFactor{

	private PlayerFactor stamina;
	private TickLooper biomeTick = new TickLooper(10);
	private TickLooper damageTick = new TickLooper(1);

	private int biome_reserve = 0;
	public FactorThirst() {
		super("thirst", ModSurvivalist.instance.thirst_max);
	}

	@Override
	public int getFullColor() {
		return 0xff00ccff;
	}

	@Override
	public int getEmptyColor() {
		return 0xffff0000;
	}

	@Override
	public int getLowSaturationColor() {
		return 0xffccffff;
	}

	@Override
	public void onTick(EntityPlayer ep) {

		//value update
		if(saturation > 0)delta_saturation -- ;
		else delta_value-- ;

		if(ep.isInWater())delta_saturation += 2;

		if(biomeTick.tick())
		{
			float temp = ep.worldObj.getBiomeGenForCoords((int)ep.posX, (int)ep.posZ).getFloatTemperature();
			biome_reserve = temp >= 1.0f ? 8 : 0;
		}

		if(biome_reserve > 0)
		{
			if(this.saturation > 0)delta_saturation -= 1;
			else delta_value -= 1;
			
			biome_reserve--;
		}

		//effects
		if(stamina == null)stamina = PlayerStat.getStat(ep).getFactor("stamina");
		if(this.drained())
		{
			stamina.delta_value-=2;
			if(damageTick.tickWithHault())
			{
				reportForEffect(EffectEnforcing.ON_LOW_THIRST);
				damageTick.hault(100);
			}
		}else if(this.fulled() && stamina.saturation <= 0 && stamina.value < stamina.max)
		{
			stamina.delta_value+=1;
			if(saturation > 0)delta_saturation--;
			else delta_value--;
		}

		if(!this.drained())damageTick.reset();
	}

	@Override
	public Icon getIcon() {
		return ItemDrink.thirst;
	}

	public boolean enabled()
	{
		return ModSurvivalist.instance.enable_thirst;
	}
}
