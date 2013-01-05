package hx.Lockit.asm;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
public class LockitCoreContainer extends DummyModContainer
{
    public LockitCoreContainer()
    {
        super(new ModMetadata());
        /* ModMetadata is the same as mcmod.info */
        ModMetadata myMeta = super.getMetadata();
        myMeta.authorList = Arrays.asList(new String[] { "hypercross" });
        myMeta.description = "Lockit coremod hooks";
        myMeta.modId = "Lockit";
        myMeta.version = "1.4.6";
        myMeta.name = "Lockit";
        myMeta.url = "http://minecraftforge.net/wiki/Using_Access_Transformers";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }
}
