package hx.Lockit;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockHangedLock extends BlockLock
{
    public BlockHangedLock(int id)
    {
        super(id);
        this.blockIndexInTexture = 3;
        keyStayInLock = false;
        this.setBlockName("hangedLock");
        // TODO Auto-generated constructor stub
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public int getRenderType()
    {
        return ModLockit.instance.block("HangedLock").ri();
    }

    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(par5);
        return (dir == NORTH && isBlockPlaceableOnSide(par1World, par2, par3, par4 + 1, NORTH)) ||
                (dir == SOUTH && isBlockPlaceableOnSide(par1World, par2, par3, par4 - 1, SOUTH)) ||
                (dir == WEST  && isBlockPlaceableOnSide(par1World, par2 + 1, par3, par4, WEST)) ||
                (dir == EAST  && isBlockPlaceableOnSide(par1World, par2 - 1, par3, par4, EAST));
    }

    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return (isBlockPlaceableOnSide(par1World, par2 - 1, par3, par4, EAST)) ||
                (isBlockPlaceableOnSide(par1World, par2 + 1, par3, par4, WEST)) ||
                (isBlockPlaceableOnSide(par1World, par2, par3, par4 - 1, SOUTH)) ||
                (isBlockPlaceableOnSide(par1World, par2, par3, par4 + 1, NORTH));
    }

    private boolean isBlockPlaceableOnSide(World par1World, int par2, int par3, int par4, ForgeDirection side)
    {
        return par1World.isBlockSolidOnSide(par2, par3, par4, side) ||
                par1World.getBlockId(par2, par3, par4) == Block.chest.blockID ||
                par1World.getBlockId(par2, par3, par4) == Block.anvil.blockID ||
                par1World.getBlockId(par2, par3, par4) == Block.enchantmentTable.blockID;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        this.func_82534_e(var5);
    }

    private void func_82534_e(int par1)
    {
        int var2 = par1 > 4 ? par1 - 4 : par1;
        boolean var3 = false;
        float var4 = 0.2F;
        float var5 = 0.6F;
        float var6 = 0.2F;
        float var7 = 0.1F;

        if (var3)
        {
            var7 = 0.0625F;
        }

        if (var2 == 1)
        {
            this.setBlockBounds(0.0F, var4, 0.5F - var6, var7, var5, 0.5F + var6);
        }
        else if (var2 == 2)
        {
            this.setBlockBounds(1.0F - var7, var4, 0.5F - var6, 1.0F, var5, 0.5F + var6);
        }
        else if (var2 == 3)
        {
            this.setBlockBounds(0.5F - var6, var4, 0.0F, 0.5F + var6, var5, var7);
        }
        else if (var2 == 4)
        {
            this.setBlockBounds(0.5F - var6, var4, 1.0F - var7, 0.5F + var6, var5, 1.0F);
        }
    }

    public int func_85104_a(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
    {
        int var10 = par1World.getBlockMetadata(par2, par3, par4);
        int var11 = var10 > 4 ? 4 : 0;
        var10 -= var11;
        ForgeDirection dir = ForgeDirection.getOrientation(par5);

        if (dir == NORTH && isBlockPlaceableOnSide(par1World, par2, par3, par4 + 1, NORTH))
        {
            var10 = 4;
        }
        else if (dir == SOUTH && isBlockPlaceableOnSide(par1World, par2, par3, par4 - 1, SOUTH))
        {
            var10 = 3;
        }
        else if (dir == WEST && isBlockPlaceableOnSide(par1World, par2 + 1, par3, par4, WEST))
        {
            var10 = 2;
        }
        else if (dir == EAST && isBlockPlaceableOnSide(par1World, par2 - 1, par3, par4, EAST))
        {
            var10 = 1;
        }
        else
        {
            var10 = this.getOrientation(par1World, par2, par3, par4);
        }

        return var10 + var11;
    }

    private int getOrientation(World par1World, int par2, int par3, int par4)
    {
        if (isBlockPlaceableOnSide(par1World, par2 - 1, par3, par4, EAST))
        {
            return 1;
        }

        if (isBlockPlaceableOnSide(par1World, par2 + 1, par3, par4, WEST))
        {
            return 2;
        }

        if (isBlockPlaceableOnSide(par1World, par2, par3, par4 - 1, SOUTH))
        {
            return 3;
        }

        if (isBlockPlaceableOnSide(par1World, par2, par3, par4 + 1, NORTH))
        {
            return 4;
        }

        return 1;
    }

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
    {
        ((TileEntityLock)(par1World.getBlockTileEntity(par2, par3, par4))).setNewKey();
    }

    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (this.canPlaceBlockAt(par1World, par2, par3, par4))
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);
            var6 = var6 > 4 ? var6 - 4 : var6;
            boolean var7 = false;

            if (!isBlockPlaceableOnSide(par1World, par2 - 1, par3, par4, EAST) && var6 == 1)
            {
                var7 = true;
            }

            if (!isBlockPlaceableOnSide(par1World, par2 + 1, par3, par4, WEST) && var6 == 2)
            {
                var7 = true;
            }

            if (!isBlockPlaceableOnSide(par1World, par2, par3, par4 - 1, SOUTH) && var6 == 3)
            {
                var7 = true;
            }

            if (!isBlockPlaceableOnSide(par1World, par2, par3, par4 + 1, NORTH) && var6 == 4)
            {
                var7 = true;
            }

            if (var7)
            {
                this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                par1World.setBlockWithNotify(par2, par3, par4, 0);
            }
        }
        else
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockWithNotify(par2, par3, par4, 0);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        // TODO Auto-generated method stub
        TileEntityLock lock = new TileEntityLock();
        lock.isSolid = false;
        return lock;
    }

    protected boolean isLocked(World par1World, int par2, int par3, int par4)
    {
        int meta = par1World.getBlockMetadata(par2, par3, par4);
        return meta > 4;
    }

    protected void switchLocked(World par1World, int par2, int par3, int par4)
    {
        int meta = par1World.getBlockMetadata(par2, par3, par4);
        par1World.setBlockMetadataWithNotify(par2, par3, par4, isLocked(par1World, par2, par3, par4) ? meta - 4 : meta + 4);
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    public boolean isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return (par1IBlockAccess.getBlockMetadata(par2, par3, par4)) < 5;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side
     */
    public boolean isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        int var6 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        int tran[] = {0, 5, 4, 3, 2};

        if (var6 > 4)
        {
            return false;
        }
        else
        {
            return tran[var6] == par5;
        }
    }

    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        this.func_82536_d(par1World, par2, par3, par4, par6);
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    private void func_82536_d(World par1World, int par2, int par3, int par4, int par5)
    {
        par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);

        if (par5 > 4)
        {
            par5 -= 4;
        }

        if (par5 == 1)
        {
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
        }
        else if (par5 == 2)
        {
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
        }
        else if (par5 == 3)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
        }
        else if (par5 == 4)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
        }
        else
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
        }
    }

    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        boolean val =  super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
        this.func_82536_d(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4));
        return val;
    }
}
