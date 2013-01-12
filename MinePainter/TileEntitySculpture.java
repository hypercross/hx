package hx.MinePainter;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class TileEntitySculpture extends TileEntity implements IBlockAccess{
	byte[] data = new byte[64];
	
	private int biasX,biasY,biasZ;
	
	public TileEntitySculpture()
	{
		Random rnd = new Random();
		rnd.nextBytes(data);
	}
	
	private boolean invalid(int x,int y,int z)
	{
		if(x>=8)return true;
		if(x<0)return true;
		if(y>=8)return true;
		if(y<0)return true;
		if(z>=8)return true;
		if(z<0)return true;
		return false;
	}
	
	public boolean get(int x,int y,int z)
	{
		if(invalid(x,y,z))return false;
		byte strip = data[x*8+y];
		
		return ((strip >> z) & 1) == 1;
	}
	
	public void set (int x,int y,int z)
	{
		x%=8;
		y%=8;
		byte strip = data[x*8+y];
		
		strip |= 1 << z;
	}
	
	public void del (int x,int y,int z)
	{
		x%=8;
		y%=8;
		byte strip = data[x*8+y];
		
		strip ^= 1 << z; 
	}
	
	//IBlockAccess

	public void bias(int x,int y,int z)
	{
		biasX = x;
		biasY = y;
		biasZ = z;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockOpaqueCube(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				   var2 - this.yCoord + biasY,
				   var3 - this.zCoord + biasZ);
	}
	
	@Override
	public int getBlockId(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				   var2 - this.yCoord + biasY,
				   var3 - this.zCoord + biasZ) ? Block.cobblestone.blockID : 0;
	}

	@Override
	public TileEntity getBlockTileEntity(int var1, int var2, int var3) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
		return worldObj.getLightBrightnessForSkyBlocks(var1, var2, var3, var4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getBrightness(int var1, int var2, int var3, int var4) {
		return worldObj.getBrightness(var1, var2, var3, var4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getLightBrightness(int var1, int var2, int var3) {
		return worldObj.getLightBrightness(var1, var2, var3);
	}

	@Override
	public int getBlockMetadata(int var1, int var2, int var3) {
		return 0;
	}

	@Override
	public Material getBlockMaterial(int var1, int var2, int var3) {
		return Material.rock;
	}

	@Override
	public boolean isBlockNormalCube(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				   var2 - this.yCoord + biasY,
				   var3 - this.zCoord + biasZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isAirBlock(int var1, int var2, int var3) {
		return !get(var1 - this.xCoord + biasX,
				   var2 - this.yCoord + biasY,
				   var3 - this.zCoord + biasZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BiomeGenBase getBiomeGenForCoords(int var1, int var2) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight() {
		return 8;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean extendedLevelsInChunkCache() {
		return worldObj.extendedLevelsInChunkCache();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesBlockHaveSolidTopSurface(int var1, int var2, int var3) {
		return get(var1 - this.xCoord + biasX,
				   var2 - this.yCoord + biasY,
				   var3 - this.zCoord + biasZ);
	}

	@Override
	public Vec3Pool getWorldVec3Pool() {
		return this.worldObj.getWorldVec3Pool();
	}

	@Override
	public boolean isBlockProvidingPowerTo(int var1, int var2, int var3,
			int var4) {
		return false;
	}
}
