package hx.survivalist;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWoodenBed extends BlockBed{

	private Icon[] bed_tops;
	private Icon[] bed_sides;
	private Icon[] bed_ends;
	
	public static BlockWoodenBed instance;
	
	public BlockWoodenBed(int par1) {
		super(par1);
		this.setBlockBounds(0, 0, 0, 1f, 0.125f,1f);
		instance = this;
	}
	
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		this.setBlockBounds(0, 0, 0, 1f, 0.125f,1f);
	}

	public int getRenderType()
	{
		return 14;
	}
	
	public boolean isBed(World w, int x,int y,int z,EntityLiving ep)
	{
		return true;
	}
	
	public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return ItemWoodenBed.instance.itemID;
    }
	
	public int idDropped(int par1, Random par2Random, int par3)
    {
        return isBlockHeadOfBed(par1) ? 0 : ItemWoodenBed.instance.itemID;
    }
	
	public void registerIcons(IconRegister register)
	{
		bed_tops = new Icon[]{register.registerIcon("survivalist:bed_feet_top"),register.registerIcon("survivalist:bed_head_top")};
		bed_ends = new Icon[]{register.registerIcon("survivalist:bed_feet_end"),register.registerIcon("survivalist:bed_head_end")};
		bed_sides = new Icon[]{register.registerIcon("survivalist:bed_feet_side"),register.registerIcon("survivalist:bed_head_side")};
	}
	
	public Icon getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        if (par1 == 0)
        {
            return Block.planks.getBlockTextureFromSide(par1);
        }
        else
        {
            int k = getDirection(par2);
            int l = Direction.bedDirection[k][par1];
            int i1 = isBlockHeadOfBed(par2) ? 1 : 0;
            return (i1 != 1 || l != 2) && (i1 != 0 || l != 3) ? (l != 5 && l != 4 ? this.bed_tops[i1] : this.bed_sides[i1]) : this.bed_ends[i1];
        }
    }
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		int light = par1World.skylightSubtracted;
		par1World.skylightSubtracted = 4;
		
		boolean result = super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
		par1World.skylightSubtracted = light;
		
		return result;
	}
}
