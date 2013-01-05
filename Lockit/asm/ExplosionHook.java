package hx.Lockit.asm;

import java.net.URLClassLoader;

import hx.utils.MethodInjectorTransformer;
import hx.utils.Obfuscated;

public class ExplosionHook extends MethodInjectorTransformer
{
    @Obfuscated("xx")
    public String explosionClassPath = "net.minecraft.world.Explosion";

    @Obfuscated("yc")
    public String worldClassPath	  = "net.minecraft.world.World";

    @Obfuscated("a")
    public String doExplosionBName = "doExplosionB";

    @Obfuscated("k")
    public String worldObjName		= "worldObj";

    @Override
    protected void setup(URLClassLoader ucl) throws Exception
    {
        Class explosion = ucl.loadClass(explosionClassPath);
        Class world     = ucl.loadClass(worldClassPath);
        Class eH		= ucl.loadClass("hx.Lockit.EventHandler");
        inject(explosion.getMethod(doExplosionBName, boolean.class),
                eH.getMethod("resolveExplosion", explosion, world));
        reportField(worldObjName);
    }
}
