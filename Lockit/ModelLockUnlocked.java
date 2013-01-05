package hx.Lockit;

public class ModelLockUnlocked extends ModelLock
{
    public ModelLockUnlocked()
    {
        super();
        Shape2.cubeList.clear();
        Shape2.addBox(0F, -4F, 1F, 1, 3, 1);
        Shape2.setRotationPoint(0F, 0F, 0F);
        Shape2.setTextureSize(64, 32);
        Shape2.mirror = true;
    }
}
