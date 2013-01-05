package hx.Lockit.asm;

import java.net.URLClassLoader;

import hx.utils.*;

public class PistonHook extends  MethodInjectorTransformer
{
    @Obfuscated("aoa")
    public String pistonPath = "net.minecraft.block.BlockPistonBase";

    @Obfuscated("yc")
    public String worldPath  = "net.minecraft.world.World";

    @Obfuscated("a")
    public String canPushName = "canPushBlock";

    @Override
    protected void setup(URLClassLoader ucl) throws Exception
    {
        Class piston = ucl.loadClass(pistonPath);
        Class world  = ucl.loadClass(worldPath);
        Class eH	 = ucl.loadClass("hx.Lockit.EventHandler");
        inject(
                piston.getDeclaredMethod(canPushName,
                        int.class, world, int.class, int.class, int.class, boolean.class),
                eH.getMethod("canPushBlock",
                        int.class, world, int.class, int.class, int.class, boolean.class));
        reportParam(0, int.class);
        reportParam(1, world);
        reportParam(2, int.class);
        reportParam(3, int.class);
        reportParam(4, int.class);
        reportParam(5, boolean.class);
    }
}
