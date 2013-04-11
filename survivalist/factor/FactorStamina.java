package hx.survivalist.factor;

import cpw.mods.fml.common.network.PacketDispatcher;
import hx.survivalist.ItemDrink;
import hx.survivalist.ModSurvivalist;
import hx.survivalist.PlayerFactor;
import hx.utils.Debug;
import hx.utils.PacketHelper;
import hx.utils.TickLooper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;

public class FactorStamina extends PlayerFactor{

	private TickLooper potionEffectTick = new TickLooper(1);
	private TickLooper biomeTick = new TickLooper(10);
	private int biome_reserve = 0;
	public FactorStamina() {
		super("stamina", ModSurvivalist.instance.stamina_max);
		rate = 3;
	}

	@Override
	public int getFullColor() {
		return 0xff33cc00;
	}

	@Override
	public int getEmptyColor() {
		return 0xffccff00;
	}

	@Override
	public int getLowSaturationColor() {
		return getFullColor();
	}

	public void onReset()
	{
		super.onReset();
		saturation = 0;
	}

	@Override
	public void onTick(EntityPlayer ep) {

		//value updates
		if(saturation > 0)
			delta_saturation--;
		else delta_value+=1;

		if(biomeTick.tick())
		{
			float temp = ep.worldObj.getBiomeGenForCoords((int)ep.posX, (int)ep.posZ).getFloatTemperature();
			if(temp <= 0.2f)biome_reserve -= 5;
			if(temp <= 0.0f)biome_reserve -= 2;
		}

		if(biome_reserve != 0)
		{
			delta_value --;
			biome_reserve++;
		}

		if(ep.isSprinting())
		{
			saturation = Math.max(15, saturation);
			delta_value-=2;
		}

		if(ep.isSwingInProgress)
		{
			saturation = Math.max(10,saturation);
			delta_value -= 1;
		}

		//effects
		if(this.drained())ep.setSprinting(false);

		//potion effects
		if(potionEffectTick.tickWithHault())
			if(this.drained())
			{
				PacketDispatcher.sendPacketToServer(PacketHelper.toPacket("effect_enforcing", 0));
				saturation = Math.max(20, saturation);
				potionEffectTick.hault(40);
			}

	}

	public int getSaturation()
	{
		return value;
	}

	@Override
	public Icon getIcon() {
		return ItemDrink.stamina;
	}
	
	public boolean enabled()
	{
		return ModSurvivalist.instance.enable_stamina;
	}
}
