package hx.survivalist;

import net.minecraft.entity.player.EntityPlayer;

public interface ICarriedEffect {

	public String getTextureFile();
	
	public void onTick(EntityPlayer ep);
}
