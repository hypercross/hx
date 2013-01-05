package hx.Lockit.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions( {"hx.Lockit.asm"})
public class LockitLoadingPlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getLibraryRequestClass()
    {
        return null;
    }

    @Override
    public String[] getASMTransformerClass()
    {
        // TODO Auto-generated method stub
        return new String[] {"hx.Lockit.asm.ExplosionHook", "hx.Lockit.asm.PistonHook"};
    }

    @Override
    public String getModContainerClass()
    {
        // TODO Auto-generated method stub
        return "hx.Lockit.asm.LockitCoreContainer";
    }

    @Override
    public String getSetupClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // TODO Auto-generated method stub
    }
}
