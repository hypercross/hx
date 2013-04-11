package hx.MinePainter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemHinge extends Item{

	public ItemHinge(int id)
	{
		super(id);
		setCreativeTab(CreativeTabs.tabTools);
		setUnlocalizedName("itemHinge");
	}
	
	public void updateIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon("stick");
    }
	
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return 0x888888;
    }
	
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int face, float xs, float ys, float zs)
	{
		if(w.isRemote)return false;
		if(w.getBlockId(x, y, z) != ModMinePainter.instance.block("Sculpture").blockID)return false;
		
		TileEntitySculpture tes = (TileEntitySculpture)w.getBlockTileEntity(x, y, z);
		
		if(xs > 0.5f)
		{
			tes.hinge = (byte) (zs > 0.5f ? 4 : 1); 
		}
		else
		{
			tes.hinge = (byte) (zs > 0.5f ? 2 : 3); 
		}
		
		tes.needUpdate = true;
		is.stackSize--;
		return true;
	}
}
