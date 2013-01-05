package hx.Alchemania.Block;

import hx.Alchemania.Alchemania;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockAlchemyFurnace extends BlockContainer {

	public BlockAlchemyFurnace(int par1) {
		super(par1, Material.rock);
		setHardness(0.5f);
		setBlockName("alchemyFurnace");
		setCreativeTab(CreativeTabs.tabDecorations);
		setBlockBounds(0.1f,0f,0.1f,0.9f,0.8f,0.9f);
		this.blockIndexInTexture = 2;
	}
	
	public int getBlockTextureFromSideAndMetadata(int par1, int par2)
	{
		if(par1 == par2)
			return 6;
		if(par1>1)
			return 3;
		return 4;
	}

	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
    {
        Vec3 vec = par5EntityLiving.getLookVec();
        double dp = 1f;
        int face = 0;

        for (int i = 2; i < 6; i++)
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
	
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            TileEntityAlchemyFurnace var10 = (TileEntityAlchemyFurnace)par1World.getBlockTileEntity(par2, par3, par4);

            if (var10 != null)
            {
                par5EntityPlayer.displayGUIFurnace(var10);
            }

            return true;
        }
    }
	
	public void randomDisplayTick(World w, int x, int y, int z, Random r)
    {
		float _x = x + 0.3f + r.nextFloat()*0.4f;
		float _z = z + 0.3f + r.nextFloat()*0.4f;
		w.spawnParticle("smoke", _x, y + 1f, _z, 0,0.1f,0);
		if(w.getBlockId(x,y-1,z) == Block.fire.blockID)
		{
			int face = w.getBlockMetadata(x, y, z);
			ForgeDirection dir = ForgeDirection.getOrientation(face);
			
			_x = x + 0.4f + dir.offsetX*0.45f + r.nextFloat()*0.2f;
			_z = z + 0.5f + dir.offsetZ*0.45f;
			w.spawnParticle("flame", _x, y + 0.4f, _z, 0,0f,0);
		}
    }

	@Override
	public TileEntity createNewTileEntity(World var1) {
		// TODO Auto-generated method stub
		return new TileEntityAlchemyFurnace();
	}
	

	@Override
	public int getRenderType()
	{
		return Alchemania.alchemyFurnaceRI;
	}
	
	
	
	public String getTextureFile()
	{
		return Alchemania.MAIN_TEXTURE;
	}
}
