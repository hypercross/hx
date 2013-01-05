package hx.Lockit;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.src.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockLock extends BlockContainer
{
    protected boolean keyStayInLock = true;

    public BlockLock(int id)
    {
        super(id, Material.anvil);
        this.setCreativeTab(CreativeTabs.tabDecorations).setHardness(0.5F)
        .setStepSound(Block.soundStoneFootstep).setBlockName("lock")
        .setRequiresSelfNotify();
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        // TODO Auto-generated method stub
        return new TileEntityLock();
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int metadata)
    {
        int face = metadata >= 6 ? metadata - 6 : metadata;
        int lock = metadata >= 6 ? 1 : 0;

        if (side == face)
        {
            return lock;
        }

        return 2;
    }

    @Override
    public String getTextureFile()
    {
        return ModLockit.instance.MAIN_TEXTURE;
    }

    @Override
    public int getRenderType()
    {
        return ModLockit.instance.block("Lock").ri();
    }

    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
    {
        ((TileEntityLock)(par1World.getBlockTileEntity(par2, par3, par4))).setNewKey();
        Vec3 vec = par5EntityLiving.getLookVec();
        double dp = 1f;
        int face = 0;

        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            Vec3 newVec = Vec3.createVectorHelper(dir.offsetX, dir.offsetY, dir.offsetZ);

            if (newVec.dotProduct(vec) < dp)
            {
                face = i;
                dp = newVec.dotProduct(vec);
            }
        }

        par1World.setBlockMetadataWithNotify(par2, par3, par4, face);
    }

    protected boolean isLocked(World par1World, int par2, int par3, int par4)
    {
        int meta = par1World.getBlockMetadata(par2, par3, par4);
        return meta >= 6;
    }

    protected void switchLocked(World par1World, int par2, int par3, int par4)
    {
        int meta = par1World.getBlockMetadata(par2, par3, par4);
        par1World.setBlockMetadataWithNotify(par2, par3, par4, isLocked(par1World, par2, par3, par4) ? meta - 6 : meta + 6);
    }

    private void spawnKey(World par1World, int par2, int par3, int par4, EntityLiving par5EntityPlayer)
    {
        if (par1World.isRemote)
        {
            return;
        }

        ItemStack keystack = new ItemStack(ModLockit.instance.item("Key").item());
        TileEntityLock eLock = ((TileEntityLock)(par1World.getBlockTileEntity(par2, par3, par4)));
        ItemKey.setCode(keystack, eLock.key);
        ItemKey.setName(keystack, eLock.keyName);
        ItemKey.setColor(keystack, eLock.keyColor);
        keystack.setRepairCost(-4);

        if (par5EntityPlayer instanceof EntityPlayer &&
                ((EntityPlayer)par5EntityPlayer).inventory.getCurrentItem() != null &&
                ((EntityPlayer)par5EntityPlayer).inventory.getCurrentItem().itemID == ModLockit.instance.item("KeyChain").item().shiftedIndex)
        {
            boolean success = ItemKeyChain.appendKey(((EntityPlayer)par5EntityPlayer).inventory.getCurrentItem(), keystack);

            if (!success)
            {
                EntityItem keyentity = new EntityItem(par1World, par5EntityPlayer.posX, par5EntityPlayer.posY + 0.5F, par5EntityPlayer.posZ, keystack);
                par1World.spawnEntityInWorld(keyentity);
            }
        }
        else
        {
            EntityItem keyentity = new EntityItem(par1World, par5EntityPlayer.posX, par5EntityPlayer.posY + 0.5F, par5EntityPlayer.posZ, keystack);
            par1World.spawnEntityInWorld(keyentity);
        }
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "random.click", 0.3F, 0.6F);

        if (par1World.isRemote)
        {
            return true;
        }

        TileEntityLock eLock = ((TileEntityLock)(par1World.getBlockTileEntity(par2, par3, par4)));
        boolean isLocked = this.isLocked(par1World, par2, par3, par4);

        if (!isLocked)
        {
            if (keyStayInLock)
            {
                this.spawnKey(par1World, par2, par3, par4, par5EntityPlayer);
            }
            else if (eLock.hasKey)
            {
                this.spawnKey(par1World, par2, par3, par4, par5EntityPlayer);
                eLock.hasKey = false;
            }
            else
            {
                if (par5EntityPlayer.inventory.getCurrentItem() == null)
                {
                    return true;
                }
                else if (par5EntityPlayer.inventory.getCurrentItem().itemID == ModLockit.instance.item("SkeletonKey").item().shiftedIndex)
                {
                }
                else if (par5EntityPlayer.inventory.getCurrentItem().itemID == ModLockit.instance.item("KeyChain").item().shiftedIndex)
                {
                    int num = 36;
                    int i = 0;

                    for (; i < num; i++)
                    {
                        if (ItemKeyChain.keycode(par5EntityPlayer.inventory.getCurrentItem(), i) == eLock.key)
                        {
                            break;
                        }
                    }

                    if (i == num)
                    {
                        return true;
                    }
                }
                else if (par5EntityPlayer.inventory.getCurrentItem().itemID == ModLockit.instance.item("Key").item().shiftedIndex)
                {
                    ItemStack key = par5EntityPlayer.inventory.getCurrentItem();

                    if (ItemKey.keycode(key) != eLock.key)
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            boolean keyStayInLock = this.keyStayInLock || par5EntityPlayer.isSneaking();

            if (par5EntityPlayer.inventory.getCurrentItem() == null)
            {
                return true;
            }
            else if (par5EntityPlayer.inventory.getCurrentItem().itemID == ModLockit.instance.item("SkeletonKey").item().shiftedIndex)
            {
                if (keyStayInLock)
                {
                    eLock.keyName = null;
                    par5EntityPlayer.inventory.decrStackSize(par5EntityPlayer.inventory.currentItem, 1);
                }
            }
            else if (par5EntityPlayer.inventory.getCurrentItem().itemID == ModLockit.instance.item("KeyChain").item().shiftedIndex)
            {
                int num = 36;
                int i = 0;

                for (; i < num; i++)
                {
                    if (ItemKeyChain.keycode(par5EntityPlayer.inventory.getCurrentItem(), i) == eLock.key)
                    {
                        break;
                    }
                }

                if (i == num)
                {
                    return true;
                }

                if (keyStayInLock)
                {
                    eLock.keyName = ItemKeyChain.keyname(par5EntityPlayer.inventory.getCurrentItem(), i);
                    eLock.keyColor = ItemKeyChain.keycolor(par5EntityPlayer.inventory.getCurrentItem(), i);
                    ItemKeyChain.removeKey(par5EntityPlayer.inventory.getCurrentItem(), eLock.key);

                    if (ItemKeyChain.empty(par5EntityPlayer.inventory.getCurrentItem()))
                        par5EntityPlayer.inventory.setInventorySlotContents(
                                par5EntityPlayer.inventory.currentItem, null);
                }
            }
            else if (par5EntityPlayer.inventory.getCurrentItem().itemID == ModLockit.instance.item("Key").item().shiftedIndex)
            {
                ItemStack key = par5EntityPlayer.inventory.getCurrentItem();

                if (ItemKey.keycode(key) != eLock.key)
                {
                    return true;
                }

                if (keyStayInLock)
                {
                    eLock.keyName = ItemKey.keyname(key);
                    eLock.keyColor = ItemKey.getColor(key);
                    par5EntityPlayer.inventory.decrStackSize(par5EntityPlayer.inventory.currentItem, 1);
                }
            }
            else
            {
                return true;
            }

            if ((!this.keyStayInLock) && par5EntityPlayer.isSneaking())
            {
                eLock.hasKey = true;
            }
        }

        this.switchLocked(par1World, par2, par3, par4);
        return true;
    }
}
