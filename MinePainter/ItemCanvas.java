package hx.MinePainter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemCanvas extends Item{

	public ItemCanvas(int id) {
		super(id);
		setCreativeTab(CreativeTabs.tabDecorations);
		setUnlocalizedName("itemCanvas");
	}
	
	@Override
	public void updateIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon("painting");
    }

	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return 0xFFCCCC;
    }
	
	private int getMeta(EntityPlayer ep, int face)
    {
        if (face > 1)
        {
            return face + 2;
        }

        Vec3 vec = ep.getLookVec();
        double dp = 1f;
        int f = 0;

        for (int i = 2; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            Vec3 newVec = Vec3.createVectorHelper(dir.offsetX, dir.offsetY, dir.offsetZ);

            if (newVec.dotProduct(vec) < dp)
            {
                f = i;
                dp = newVec.dotProduct(vec);
            }
        }

        return f + 8 * face - 2;
    }
	
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int face, float xs, float ys, float zs)
	{
		int id = w.getBlockId(x, y, z);
		if (Block.blocksList[id] != BlockSculpture.instance && !Block.isNormalCube(id))
        {
            return false;
        }
		
		ForgeDirection dir = ForgeDirection.getOrientation(face);
        int _x = x + dir.offsetX;
        int _y = y + dir.offsetY;
        int _z = z + dir.offsetZ;
        
        if(w.getBlockId(_x, _y, _z)>0)return false;
        
        int canvasID = ModMinePainter.instance.block("Canvas").id();
        w.setBlock(_x, _y, _z, canvasID, getMeta(ep, face), 3);
        
        if(ep.capabilities.isCreativeMode)return true;
        is.stackSize -- ;
        return true;
	}
}
