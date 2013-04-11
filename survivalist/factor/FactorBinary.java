package hx.survivalist.factor;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import hx.survivalist.PlayerFactor;

public class FactorBinary extends PlayerFactor{
	
	public static HashMap<String, Icon> icons = new HashMap<String, Icon>();

	public FactorBinary(String name) {
		super(name, 10);
		value = 5;
	}

	@Override
	public int getFullColor() {
		return 0;
	}

	@Override
	public int getEmptyColor() {
		return 0;
	}

	@Override
	public int getLowSaturationColor() {
		return 0;
	}

	@Override
	public Icon getIcon() {
		return icons.get(this.name);
	}
	
	public int getSaturation()
	{
		return 0;
	}

	@Override
	public void onTick(EntityPlayer ep) {
	}

	public boolean enabled()
	{
		return false;
	}
}
