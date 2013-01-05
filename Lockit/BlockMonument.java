package hx.Lockit;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMonument extends BlockContainer
{
    public BlockMonument(int par1)
    {
        super(par1, Material.rock);
        this.setBlockName("monument");
        this.setCreativeTab(CreativeTabs.tabDecorations).setHardness(0.5F);
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int metadata)
    {
        return 4;
    }

    @Override
    public String getTextureFile()
    {
        return ModLockit.instance.MAIN_TEXTURE;
    }
    
    @Override
    public void onSetBlockIDWithMetaData(World par1World, int x, int y, int z, int meta)
    {
    	ModLockit.instance.monuments.reportRemove(x, y, z);
    }

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityMonument();
	}
	
	@Override
	public void randomDisplayTick(World w, int x, int y, int z, Random r)
	{
		TileEntityMonument tem = (TileEntityMonument) w.getBlockTileEntity(x, y, z);
		if(tem.state != TileEntityMonument.MonumentState.Invalid)
		{
			for(int i =0; i < ModLockit.particleDensity;i+=20)
			{
			double xs = r.nextDouble() * 2 - 1;
			double ys = r.nextDouble();
			double zs = r.nextDouble() * 2 - 1;
			w.spawnParticle("portal",x + .5f, y+1f, z+.5f, xs,ys,zs);
			}
		}

		int dist = 0;
		if(tem.landmark() == null)return;
		if(tem.state == TileEntityMonument.MonumentState.Plotmark)
			dist = tem.landmark().plotRange;
		else return;
		if(dist > 10)return;
		
		int bound = dist*2 +3;
		int distEx = dist+1;
		int height[][] = new int[bound][bound];
		
		for(int i =0;i<bound;i++)
			for(int j =0;j<bound;j++)
			{
				height[i][j] = y + dist;
				while(height[i][j] > y - dist
						&& w.isAirBlock(x + i - distEx
								, height[i][j], z + j - distEx))height[i][j]--;
			}
		
		for (int i = 0 ;i < ModLockit.particleDensity; i++)
		{
		
			boolean f = r.nextBoolean();
			
			float _x = r.nextInt(bound-1) - distEx + r.nextFloat();
			float _y = r.nextInt(bound-1) - distEx + r.nextFloat();
			
			
			
			if(f) _x = _x > 0 ? distEx : -distEx;
			else  _y = _y > 0 ? distEx : -distEx;
			_x += 0.5f;
			_y += 0.5f;
			
			float xs =  x + _x;
			float ys = height[(int)(_x-.5F) + distEx][(int)(_y-.5F) + distEx];
			float zs =  z + _y;
			
			w.spawnParticle("portal",xs, ys, zs, 0, 1, 0);
		}
	}
}
