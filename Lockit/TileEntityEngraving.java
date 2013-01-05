package hx.Lockit;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityEngraving extends TileEntitySign
{
    private static int[] ang = { 180, 0, 90, 270 };

    public int verAngle(int meta)
    {
        return (1 - (meta >> 2)) * 90;
    }

    public int horAngle(int meta)
    {
        return ang[(meta & 3)] ;
    }
}
