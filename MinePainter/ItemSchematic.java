package hx.MinePainter;

import hx.utils.Debug;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemSchematic extends Item{

	public ItemSchematic(int par1) {
		super(par1);
		setCreativeTab(CreativeTabs.tabDecorations);
		setUnlocalizedName("itemSchematic");
		this.setMaxStackSize(1);

	}
	
	@SideOnly(Side.CLIENT)
    public void func_94581_a(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.func_94245_a("paper");
    }

	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return 0x99ffFF;
    }
	
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int face, float xs, float ys, float zs)
	{
		checkNBT(is);
		
		int blockId = w.getBlockId(x, y, z);
		int meta    = w.getBlockMetadata(x, y, z);
		BlockSculpture sculpture = (BlockSculpture) ModMinePainter.instance.block("Sculpture").block();
		
		if(sculpture.blockID == blockId)
		{
			// copy from sculpture
			TileEntitySculpture tes = (TileEntitySculpture)w.getBlockTileEntity(x, y, z);
			
			if(!is.hasTagCompound())is.setTagCompound(new NBTTagCompound());
			
			NBTTagCompound nbt = is.getTagCompound().getCompoundTag("sculptureInfo");
			
			int f = playerFacing(ep);
			for(int i =0; i<4; i++)
			{
				if((3 - i)%4 == f%4)
					nbt.setByteArray("data", tes.data);
				tes.rotate();
			}
			is.getTagCompound().setCompoundTag("sculptureInfo", nbt);
			
			return false;
		}
		
		boolean found = false;
		int i = 0;
		for(i =0;i<16;i++)if(BlockSculpture.materialBlock[i].blockID == blockId)
		{
			found = true;
			break;
		}
		if(!found)return false;
		
		//put sculpture content onto the block
		BlockSculpture.createEmpty = true;
		w.setBlockAndMetadataWithNotify(x, y, z, sculpture.blockID, i, 3);
		TileEntitySculpture tes = (TileEntitySculpture)w.getBlockTileEntity(x, y, z);
		tes.data = (is.getTagCompound().getCompoundTag("sculptureInfo").getByteArray("data"));
		tes.blockMeta = meta;
		tes.needUpdate = true;
		
		byte current = playerFacing(ep);
		current ++;
		while(current-- %4 != 0)tes.rotate();
		
		int count = 0;
		for(int _x = 0;_x<8;_x++)
			for(int _y = 0;_y<8;_y++)
				for(int _z = 0;_z<8;_z++)
					if(!tes.get(_x, _y, _z))count++;
		
		if(!w.isRemote)
		sculpture.dropAllScrap(w, x, y, z, count);
		
		return true;
	}
	
	private byte playerFacing(EntityPlayer ep)
	{
		float x = ep.getRotationYawHead() + 45f;
		while(x<0)x+=360f;
		return (byte)(x/90f);
	}

	public static void checkNBT(ItemStack item) {
		if(item.stackTagCompound == null)
			item.setTagCompound(new NBTTagCompound());
		
		NBTTagCompound nbt = item.stackTagCompound;
		nbt.setCompoundTag("sculptureInfo",
				nbt.getCompoundTag("sculptureInfo"));

		nbt = nbt.getCompoundTag("sculptureInfo");
		byte[] bytes = nbt.getByteArray("data");
		if(bytes.length == 0)nbt.setByteArray("data", new byte[64]);
		
	}
}
