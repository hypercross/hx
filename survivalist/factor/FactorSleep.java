package hx.survivalist.factor;

import hx.survivalist.BlockWoodenBed;
import hx.survivalist.EffectEnforcing;
import hx.survivalist.ItemDrink;
import hx.survivalist.ModSurvivalist;
import hx.survivalist.PlayerFactor;
import hx.utils.Debug;
import hx.utils.TickLooper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Icon;

public class FactorSleep extends PlayerFactor{

	public TickLooper tick = new TickLooper(1);
	public FactorSleep() {
		super("sleep", ModSurvivalist.instance.sleep_max);
	}

	@Override
	public int getFullColor() {
		return 0xffFF33FF;
	}

	@Override
	public int getEmptyColor() {
		return 0xffFF0000;
	}

	@Override
	public int getLowSaturationColor() {
		return 0xffFFCCFF;
	}

	@Override
	public Icon getIcon() {
		return ItemDrink.sleep;
	}

	@Override
	public void onTick(EntityPlayer ep) {
		//value updates
		if(ep.worldObj.provider.getDimensionName().equals("Nether"));
		else if(!ep.isRiding())
		{
			if(saturation > 0)delta_saturation--;
			else delta_value--;
		}else delta_saturation += 2;

		if(ep.isPlayerFullyAsleep())
		{
			ChunkCoordinates pos = ep.playerLocation;
			int id = ep.worldObj.getBlockId(pos.posX, pos.posY, pos.posZ);

			if(id == Block.bed.blockID)
				this.onReset();
			else if(id == BlockWoodenBed.instance.blockID)
				this.delta_value += this.max/2;
			else Debug.dafuq(id);
		}

		//effects
		if(tick.tickWithHault())
			if(this.drained())
			{
				reportForEffect(EffectEnforcing.ON_LOW_SLEEP);
				tick.hault(20);
			}
	}

	public boolean enabled()
	{
		return ModSurvivalist.instance.enable_sleep;
	}

}
