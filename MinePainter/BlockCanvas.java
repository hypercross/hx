package hx.MinePainter;

import hx.utils.Debug;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockCanvas extends BlockContainer{
	
	private float dp = 0.01f;
	
	public static BlockCanvas instance ;

	public BlockCanvas(int id){
		super(id, Material.cloth);
		setUnlocalizedName("blockCanvas");
		instance = this; 
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityCanvas();
	}
	
	public Icon getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return Block.cloth.getBlockTextureFromSideAndMetadata(par1, 0);
    }

	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int meta = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        
        int face = meta;
        if(face >= 8)face = 1;
        else if(face < 4)face = 0;
        else face-=2;
        
        TileEntitySculpture tes = getSculptureOnBack(par1IBlockAccess,par2,par3,par4, face);
        if(tes != null)
        {
        	this.setBlockBounds(0 - Facing.offsetsXForSide[face],
        						0 - Facing.offsetsYForSide[face],
        						0 - Facing.offsetsZForSide[face],
        						1 - Facing.offsetsXForSide[face],
        						1 - Facing.offsetsYForSide[face],
        						1 - Facing.offsetsZForSide[face]);
        	
        	return;
        }

        if (meta >= 8)
        {
            this.setBlockBounds(0, 0f, 0, 1f, dp, 1f);
        }
        else if (meta < 4)
        {
            this.setBlockBounds(0, 1 - dp, 0, 1f, 1f, 1f);
        }
        else if (meta == 4)
        {
            this.setBlockBounds(0, 0f, 1 - dp, 1f, 1f, 1f);
        }
        else if (meta == 5)
        {
            this.setBlockBounds(0, 0f, 0, 1f, 1f, dp);
        }
        else if (meta == 6)
        {
            this.setBlockBounds(1 - dp, 0f, 0, 1f, 1f, 1f);
        }
        else if (meta == 7)
        {
            this.setBlockBounds(0, 0f, 0, dp, 1f, 1f);
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
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
    public int getRenderType()
    {
        return -1;
    }

    public int idDropped(int par1, Random par2Random, int par3)
    {
    	return ModMinePainter.instance.item("Canvas").item().itemID;
    }
    
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return ModMinePainter.instance.item("Canvas").item().itemID;
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

        int bid = w.getBlockId(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ);
        if (Block.isNormalCube(bid) || Block.blocksList[bid] == BlockSculpture.instance)
            return;

        w.setBlock(x, y, z, 0, 0, 3);
    }
    
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ep,
    		int face, float _x, float _y, float _z)
    {
    	if(w.isRemote)return false;
    	
    	int color = -1;
    	int op = -1;	//-1 == no-op, 0 == draw, 1 == flood, 2 == fill , 3 == big draw
    	
    	if(ep.inventory.getCurrentItem() == null)
    	{
    		color = 0;
    		op = 0;
    	}
    	else if(ep.getCurrentEquippedItem().itemID == Item.dyePowder.itemID)
    	{
    		color = ep.getCurrentEquippedItem().getItemDamage();
    		color = ItemDye.dyeColors[color & 15] | 0xff000000;
    		op = 0;
    	}else if(ep.getCurrentEquippedItem().itemID == Item.slimeBall.itemID)
    	{
    		color = 0xffffff;
    		op = 2;
    	}else if(ep.getCurrentEquippedItem().itemID == ItemBrush.instance.itemID || 
    			ep.getCurrentEquippedItem().itemID == ItemBrushSmall.instance.itemID)
    	{
    		for(int i = 0; i < ep.inventory.getSizeInventory(); i ++)
    		{
    			ItemStack is = ep.inventory.getStackInSlot(i);
    			if(is == null)continue;
    			if(is.itemID != ItemPalette.instance.itemID)continue;
    			
    			color = ItemPalette.getColors(is)[0];
    			op = ep.getCurrentEquippedItem().itemID == ItemBrush.instance.itemID ? 3 : 0;
    			break;
    		}
    		
    		ep.getCurrentEquippedItem().damageItem(1, ep);
    	}
    	
    	//if(ep.isWet())op = 1;
    	
    	if(op == -1)return false;
    	
    	float px,py;
    	
    	int meta = w.getBlockMetadata(x, y, z);
    	int index = pixelIndex(_x,_y,_z,meta);
    	
    	TileEntityCanvas tec = (TileEntityCanvas) w.getBlockTileEntity(x, y, z);
    	
    	if(op == 0)
    	{
    		tec.image.blend(15-index/16, 15-index%16, color);
    	}else if(op == 1)
    	{
    		tec.image.flood(15-index/16, 15-index%16, -1, color);
    	}else if(op == 2)
    	{
    		tec.image.fill(color);
    	}else if(op == 3)
    	{
    		int picx = 15-index/16;
    		int picy = 15-index%16;
    		
    		int alpha = (color >> 24) & 0xff;
    		int half  = ( (alpha *3 / 4) << 24) + (color & 0xffffff);
    		int quarter = ( (alpha >> 1) << 24) + (color & 0xffffff);
    		
    		for(int i = -1; i<2;i++)
    			for(int j = -1; j<2;j++)
    			{
    				if( Math.abs(i) + Math.abs(j) == 0)
    					tec.image.blend(picx + i,	picy + j, color);
    				else if( Math.abs(i) + Math.abs(j) == 1)
    					tec.image.blend(picx + i,	picy + j, half);
    				else if( Math.abs(i) + Math.abs(j) == 2)
    					tec.image.blend(picx + i,	picy + j, quarter);
    			}
    		
    	}
    	
    	w.markBlockForUpdate(x, y, z);
    	
    	return true;
    }
    
    public static TileEntitySculpture getSculptureOnBack(IBlockAccess w, int x,int y,int z, int face)
    {
    	ForgeDirection dir = ForgeDirection.getOrientation(face);
    	x -= dir.offsetX;
    	y -= dir.offsetY;
    	z -= dir.offsetZ;
    	
    	try{
    		return (TileEntitySculpture) w.getBlockTileEntity(x,y,z);
    	}catch(Exception e)
    	{
    		return null;
    	}
    }
    
    public MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 st, Vec3 ed)
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
        
    	TileEntitySculpture tes = getSculptureOnBack(w,x,y,z,face);
    	if(tes != null)
    	{
    		MovingObjectPosition mop =  BlockSculpture.instance.collisionRayTrace(w, tes.xCoord, tes.yCoord,tes.zCoord, st, ed);
    		if(mop != null)
    		{
    			mop.blockX = x;
    			mop.blockY = y;
    			mop.blockZ = z;
    			return mop;
    		}
    	}else return super.collisionRayTrace(w, x, y, z, st, ed);
    	return null;
    }
     
    private static ForgeDirection[] xproj=
    	{
    	ForgeDirection.WEST,
    	ForgeDirection.EAST,
    	ForgeDirection.SOUTH,
    	ForgeDirection.NORTH,

    	ForgeDirection.WEST,
    	ForgeDirection.EAST,
    	ForgeDirection.SOUTH,
    	ForgeDirection.NORTH,

    	ForgeDirection.WEST,
    	ForgeDirection.EAST,
    	ForgeDirection.SOUTH,
    	ForgeDirection.NORTH
    	};
    private static ForgeDirection[] yproj=
    	{
    	ForgeDirection.NORTH,
    	ForgeDirection.SOUTH,
    	ForgeDirection.WEST,
    	ForgeDirection.EAST,

    	ForgeDirection.UP,
    	ForgeDirection.UP,
    	ForgeDirection.UP,
    	ForgeDirection.UP,

    	ForgeDirection.SOUTH,
    	ForgeDirection.NORTH,
    	ForgeDirection.EAST,
    	ForgeDirection.WEST,
    	
    	};
   
    public int pixelIndex(float x,float y,float z,int face)
    {
    	ForgeDirection xdir = xproj[face];
    	ForgeDirection ydir = yproj[face];
    	
    	float xoff = xdir.offsetX * x + xdir.offsetY * y + xdir.offsetZ * z;
    	float yoff = ydir.offsetX * x + ydir.offsetY * y + ydir.offsetZ * z;
    	
    	if(xoff < 0)xoff+=1;
    	if(yoff < 0)yoff+=1;
    	
    	return (int)(xoff*16)*16 + (int)(yoff*16);
    }
}
