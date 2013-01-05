package hx.Alchemania.Item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorTNTDispense extends BehaviorDefaultDispenseItem {

	@Override	
	protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack)
    {
        IPosition var4 = BlockDispenser.func_82525_a(par1IBlockSource);
        ItemStack var5 = par2ItemStack.splitStack(1);
        
        launchTNT(par1IBlockSource.getWorld(), var4);
        
        return par2ItemStack;
    }
	
	private void launchTNT(World w, IPosition pos)
	{
		EntityTNTPrimed tnt = new EntityTNTPrimed(w);
		tnt.setPosition(pos.getX(), pos.getY(), pos.getZ());
		tnt.setVelocity(1d, 1d, 1d);
		tnt.fuse = w.rand.nextInt(20) + 70;
		w.spawnEntityInWorld(tnt);
	}
}
