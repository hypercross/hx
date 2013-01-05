package hx.Lockit;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemChisel extends Item
{
    public ItemChisel(int par1)
    {
        super(par1);
        maxStackSize = 1;
        this.setMaxDamage(24);
        setCreativeTab(CreativeTabs.tabTools);
        setIconIndex(20);
        setItemName("itemChisel");
    }

    public String getTextureFile()
    {
        return ModLockit.instance.MAIN_TEXTURE;
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

    @Override
    public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int face, float xs, float ys, float zs)
    {
        if (!Block.isNormalCube(w.getBlockId(x, y, z)))
        {
            return false;
        }
        
        int guiID = -1;        
        if(this.isValidLandmark(w, x, y, z))
        	guiID = -2;
        else if(isPotentialPlotmark(w,x,y,z))
        	guiID = -3;

        ForgeDirection dir = ForgeDirection.getOrientation(face);
        int _x = x + dir.offsetX;
        int _y = y + dir.offsetY;
        int _z = z + dir.offsetZ;
        
        if(w.getBlockId(_x, _y, _z)>0)return false;
        
        int engravingID = ModLockit.instance.block("Engraving").id();
        w.setBlockAndMetadataWithNotify(_x, _y, _z, engravingID, getMeta(ep, face));
        TileEntityEngraving tee = (TileEntityEngraving) w.getBlockTileEntity(_x, _y, _z);

        if (!w.isRemote)
        {
            ep.openGui(ModLockit.instance, guiID, w, _x, _y, _z);
        }

        if(guiID != -1)
        {
        	TileEntityMonument tem = (TileEntityMonument) w.getBlockTileEntity(x, y, z);
        	tem.activatedFace = (byte) face;
        }
        
        is.damageItem(1, ep);
        return true;
    }
    
    private boolean isValidLandmark(World w,int x,int y,int z)
    {
    	if(w.getBlockId(x, y, z) != ModLockit.instance.block("Monument").blockID)return false;
    	TileEntityMonument tem = (TileEntityMonument) w.getBlockTileEntity(x, y, z);
    	if(tem == null)return false;
    	if(tem.state != TileEntityMonument.MonumentState.Invalid)
    		return false;
    	boolean res = (w.getBlockId(x, y+1, z) == Block.beacon.blockID);
    	return res;
    }
    
    private boolean isPotentialPlotmark(World w,int x,int y,int z)
    {
    	if(w.getBlockId(x, y, z) != ModLockit.instance.block("Monument").blockID)return false;
    	TileEntityMonument tem = (TileEntityMonument) w.getBlockTileEntity(x, y, z);
    	if(tem == null)return false;
    	if(tem.state != TileEntityMonument.MonumentState.Invalid)
    		return false;
    	if(w.getBlockId(x, y+1, z) != 0)return false;

    	Location nearAddr = ModLockit.instance.monuments.nearestLandmark(x, y, z, w);
    	if(nearAddr == null)return false;
    	return true;
    }
}
