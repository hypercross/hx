package hx.survivalist;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event;

public class BeforeEntityFinishUseItemEvent extends Event {

	public EntityPlayer ep;
	public BeforeEntityFinishUseItemEvent(EntityPlayer ep)
	{
		this.ep = ep;
	}

}
