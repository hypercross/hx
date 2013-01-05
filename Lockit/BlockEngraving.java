package hx.Lockit;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockEngraving extends BlockContainer
{
    public BlockEngraving(int par1)
    {
        super(par1, Material.rock);
        //this.setBlockBounds(0, 0, 0, 0,0,0);
        this.setBlockName("engraving");
        this.setHardness(2f);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int meta = par1IBlockAccess.getBlockMetadata(par2, par3, par4);

        if (meta >= 8)
        {
            this.setBlockBounds(0, 0f, 0, 1f, .1f, 1f);
        }
        else if (meta < 4)
        {
            this.setBlockBounds(0, .9f, 0, 1f, 1f, 1f);
        }
        else if (meta == 4)
        {
            this.setBlockBounds(0, 0f, .9f, 1f, 1f, 1f);
        }
        else if (meta == 5)
        {
            this.setBlockBounds(0, 0f, 0, 1f, 1f, .1f);
        }
        else if (meta == 6)
        {
            this.setBlockBounds(.9f, 0f, 0, 1f, 1f, 1f);
        }
        else if (meta == 7)
        {
            this.setBlockBounds(0, 0f, 0, .1f, 1f, 1f);
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        // TODO Auto-generated method stub
        return new TileEntityEngraving();
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int metadata)
    {
        return 255;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public String getTextureFile()
    {
        return ModLockit.instance.MAIN_TEXTURE;
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }

    @Override
    public void onNeighborBlockChange(World w, int x, int y, int z, int id)
    {
        int face = w.getBlockMetadata(x, y, z);

        if (face >= 8)
        {
            face = 1;
        }
        else if (face < 4)
        {
            face = 0;
        }
        else
        {
            face -= 2;
        }

        ForgeDirection dir = ForgeDirection.getOrientation(face);

        if (Block.isNormalCube(w.getBlockId(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ)))
        {
            return;
        }

        w.setBlock(x, y, z, 0);
    }
}
