package hx.survivalist;

import net.minecraft.util.Icon;

public interface IFactor {

	public int getLevel();
	
	public int getSaturation();
	
	public int getMaxLevel();
	
	public int getFullColor();
	
	public int getEmptyColor();
	
	public int getLowSaturationColor();
	
	public Icon getIcon();
	
	public boolean drained();
	
	public boolean fulled();
	
	public boolean enabled();
}
