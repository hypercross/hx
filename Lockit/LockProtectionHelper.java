package hx.Lockit;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class LockProtectionHelper
{
    public static LockProtectionHelper instance;
    public static int lockID;
    public static int hlockID;
    private static int[] XOFF = { -1, 1, 0, 0, 0, 0};
    private static int[] YOFF = {0, 0, -1, 1, 0, 0};
    private static int[] ZOFF = {0, 0, 0, 0, -1, 1};

    public LockProtectionHelper()
    {
        lockID = ModLockit.instance.block("Lock").id();
        hlockID = ModLockit.instance.block("HangedLock").id();
        instance = this;
    }

    public static boolean canBuild(int x, int y, int z, World w)
    {
        return getLockLevel(x, y, z, w) < LockLevel.Unbroken;
    }

    public static boolean canBuildLock(int x, int y, int z, World w)
    {
        return getLockLevel(x, y, z, w) < LockLevel.NearLock;
    }

    public static boolean canUseBlock(int x, int y, int z, World w)
    {
        int id = w.getBlockId(x, y, z);

        if (id == lockID || id == hlockID)
        {
            return true;
        }

        return getLockLevel(x, y, z, w) < LockLevel.Locked;
    }

    public static void resolveExplosion(Explosion exp, World w)
    {
        if (exp instanceof Explosion)
        {
            Explosion explosion = (Explosion)exp;

            for (int i = explosion.affectedBlockPositions.size() - 1; i >= 0; i--)
            {
                ChunkPosition position = (ChunkPosition)(explosion.affectedBlockPositions.get(i));

                if (!canBuild(position.x, position.y, position.z, (World)w))
                {
                    explosion.affectedBlockPositions.remove(i);
                }
            }
        }
    }

    public static class LockLevel
    {
        public static final int LockCore = 4;
        public static final int Locked = 3;		//cant use
        public static final int Unbroken = 2;	//cant build
        public static final int NearLock = 1;	//cant build certain things
        public static final int NotLocked = 0;	//no protection
    }

    public static int getLockLevel(int x, int y, int z, World w)
    {
        int current = getIndirectLockLevel(x, y, z, w);
        int update = 0;

        if (w.getBlockId(x, y, z) == Block.doorWood.blockID)
        {
            if (w.getBlockId(x, y + 1, z) == Block.doorWood.blockID)
            {
                update = getIndirectLockLevel(x, y + 1, z, w);
            }

            if (w.getBlockId(x, y - 1, z) == Block.doorWood.blockID)
            {
                update = getIndirectLockLevel(x, y - 1, z, w);
            }
        }
        else if (w.getBlockId(x, y, z) == Block.doorSteel.blockID)
        {
            if (w.getBlockId(x, y + 1, z) == Block.doorSteel.blockID)
            {
                update = getIndirectLockLevel(x, y + 1, z, w);
            }

            if (w.getBlockId(x, y - 1, z) == Block.doorSteel.blockID)
            {
                update = getIndirectLockLevel(x, y - 1, z, w);
            }
        }
        else if (w.getBlockId(x, y, z) == Block.chest.blockID)
        {
            if (w.getBlockId(x + 1, y, z) == Block.chest.blockID)
            {
                update = getIndirectLockLevel(x + 1, y, z, w);
            }

            if (w.getBlockId(x - 1, y, z) == Block.chest.blockID)
            {
                update = getIndirectLockLevel(x - 1, y, z, w);
            }

            if (w.getBlockId(x, y, z + 1) == Block.chest.blockID)
            {
                update = getIndirectLockLevel(x, y, z + 1, w);
            }

            if (w.getBlockId(x, y, z - 1) == Block.chest.blockID)
            {
                update = getIndirectLockLevel(x, y, z - 1, w);
            }
        }

        return Math.max(current, update);
    }

    private static int getSelfLockLevel(int x, int y, int z, World w)
    {
        if (isLockCenter(x	, y	, z	, w))
        {
            return LockLevel.LockCore;
        }

        if (isLockedOnSide(x	, y	, z	, w))
        {
            return LockLevel.Locked;
        }

        if (isLockOnSide(x	, y	, z	, w))
        {
            return LockLevel.NearLock;
        }

        return LockLevel.NotLocked;
    }

    private static int getIndirectLockLevel(int x, int y, int z, World w)
    {
        int current = 0;

        for (int i = -3; i <= 3; i++)
            for (int j = -3; j <= 3; j++)
                for (int k = 0; Math.abs(i) + Math.abs(j) + k <= 3; k++)
                {
                    int dist = Math.abs(i) + Math.abs(j) + k;
                    int update = getSelfLockLevel(x + i, y + j, z + k, w) - dist;

                    if (update > current)
                    {
                        current = update;
                    }

                    update = getSelfLockLevel(x + i, y + j, z - k, w) - dist;

                    if (update > current)
                    {
                        current = update;
                    }
                }

        return current;
    }

    private static boolean isLockedOnSide(int x, int y, int z, World w)
    {
        if (w.getBlockId(x - 1, y, z) == hlockID)
            if (w.getBlockMetadata(x - 1, y, z) == 2 + 4)
            {
                return true;
            }

        if (w.getBlockId(x + 1, y, z) == hlockID)
            if (w.getBlockMetadata(x + 1, y, z) == 1 + 4)
            {
                return true;
            }

        if (w.getBlockId(x  , y, z - 1) == hlockID)
            if (w.getBlockMetadata(x  , y, z - 1) == 4 + 4)
            {
                return true;
            }

        if (w.getBlockId(x  , y, z + 1) == hlockID)
            if (w.getBlockMetadata(x  , y, z + 1) == 3 + 4)
            {
                return true;
            }

        if (w.getBlockId(x, y, z) != hlockID)
        {
            return false;
        }

        if (w.getBlockMetadata(x, y, z) < 5)
        {
            return false;
        }

        return true;
    }

    private static boolean isLockOnSide(int x, int y, int z, World w)
    {
        if (w.getBlockId(x - 1, y, z) == hlockID)
            if (w.getBlockMetadata(x - 1, y, z) == 2)
            {
                return !hasKey(x - 1, y, z, w);
            }

        if (w.getBlockId(x + 1, y, z) == hlockID)
            if (w.getBlockMetadata(x + 1, y, z) == 1)
            {
                return !hasKey(x + 1, y, z, w);
            }

        if (w.getBlockId(x  , y, z - 1) == hlockID)
            if (w.getBlockMetadata(x  , y, z - 1) == 4)
            {
                return !hasKey(x, y, z - 1, w);
            }

        if (w.getBlockId(x  , y, z + 1) == hlockID)
            if (w.getBlockMetadata(x  , y, z + 1) == 3)
            {
                return !hasKey(x, y, z + 1, w);
            }

        if (w.getBlockId(x, y, z) != hlockID)
        {
            return false;
        }

        if (w.getBlockMetadata(x, y, z) > 4)
        {
            return false;
        }

        if (hasKey(x, y, z, w))
        {
            return false;
        }

        return true;
    }

    private static boolean isLockCenter(int x, int y, int z, World w)
    {
        if (w.getBlockId(x, y, z) != lockID)
        {
            return false;
        }

        if (w.getBlockMetadata(x, y, z) < 6)
        {
            return false;
        }

        return true;
    }

    private static boolean hasKey(int x, int y, int z, World w)
    {
        TileEntityLock eLock = ((TileEntityLock)(w.getBlockTileEntity(x, y, z)));
        return eLock.hasKey;
    }

    public static boolean isBuilding(EntityPlayer ep)
    {
        ItemStack is = ep.getCurrentEquippedItem();

        if (is == null)
        {
            return false;
        }

        if (is.getItem() instanceof ItemBlock)
        {
            return true;
        }

        for (int id : ModLockit.bannedItems)
        {
            if (is.getItem().shiftedIndex == id)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isBuildingWith(EntityPlayer ep, int[] ids)
    {
        ItemStack is = ep.getCurrentEquippedItem();

        if (is == null)
        {
            return false;
        }

        for (int id : ids)
            if (is.getItem().shiftedIndex == id)
            {
                return true;
            }

        return false;
    }
}
