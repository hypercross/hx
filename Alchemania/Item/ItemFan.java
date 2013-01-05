package hx.Alchemania.Item;

import hx.Alchemania.Alchemania;
import hx.Alchemania.Block.TileEntityAlchemyFurnace;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemFan extends Item{
	public ItemFan(int par1) {
		super(par1);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
		setItemName("fan");
		setIconIndex(7);
	}

	public String getTextureFile()
	{
		return Alchemania.MAIN_TEXTURE;

	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int face, float xs, float ys, float zs)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(face);
		
		x += dir.offsetX;
		y += dir.offsetY;
		z += dir.offsetZ;
		
		if(w.getBlockId(x, y, z) != Block.fire.blockID)return false;
		if(w.getBlockId(x, y+1,z)!=Alchemania.blockAlchemyFurnace.blockID)return false;
		
		if(w.isRemote)
		{
			for(int i =0;i<10;i++)
			w.spawnParticle("smoke", x + 0.5f ,y + 2f,z + 0.5f,0,0.1f,0);
			return true;
		}else
		{
			TileEntityAlchemyFurnace teaf = (TileEntityAlchemyFurnace) w.getBlockTileEntity(x, y+1,z);
			teaf.furnaceCookTime++;
		}
		return false;
	}
}
