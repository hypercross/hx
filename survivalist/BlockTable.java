package hx.survivalist;

import hx.survivalist.TileEntityTable;
import hx.utils.Debug;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockTable extends BlockContainer{

	public static BlockTable instance;
	
	public static Icon inv, side, top;
	
	public static boolean onRender = false;

	public BlockTable(int par1) {
		super(par1, Material.wood);
		this.setUnlocalizedName("blockTable");
		instance = this;
		this.setBlockBounds(0,0,0,1,.625f,1);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	public int getRenderType()
	{
		return ModSurvivalist.instance.block("Table").blockRI;
	}
	
	public void registerIcons(IconRegister register)
	{
		inv = register.registerIcon("survivalist:table");
		side = register.registerIcon("survivalist:table_side");
		top = register.registerIcon("survivalist:table_top");
	}
	
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	public static boolean isNormalCube(int par0)
	{
		return false;
	}
	
	public Icon getBlockTextureFromSideAndMetadata(int par1, int par2)
	{
		if(par1 == 0)return Block.planks.getBlockTextureFromSideAndMetadata(par1, par2);
		
		if(par1 == 1)return onRender ? top : inv;
		return side;
	}
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep, int par6, float par7, float par8, float par9)
	{
		
		TileEntityTable tepi = (TileEntityTable) w.getBlockTileEntity(x, y, z);
		
		int i = (par9 > 0.5f ? 2 : 0) + (par7 > 0.5f ? 1 : 0);
		tepi.yaw[i] = ep.rotationYaw;
		
		boolean result = tepi.interact(ep, i);
		if(result)w.markBlockForUpdate(x, y, z);
		return result;
	}
	
	public void breakBlock(World w, int x, int y, int z, int par5, int par6)
	{
		TileEntityTable tepi = (TileEntityTable) w.getBlockTileEntity(x, y, z);
		
		for(int i = 0;i<tepi.size();i++)
		{
			if(tepi.get(i) != null)
			{
				EntityItem ei = new EntityItem(w, x,y,z, tepi.get(i));
				ei.motionX *= w.rand.nextBoolean() ? 1.0f : -1.0f;
				ei.motionY *= w.rand.nextBoolean() ? 1.0f : -1.0f;
				ei.motionZ *= w.rand.nextBoolean() ? 1.0f : -1.0f;
				w.spawnEntityInWorld(ei);
			}
		
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityTable();
	}

}
