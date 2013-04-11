package hx.survivalist;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;

import hx.utils.Debug;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumStatus;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

public class EventHandler {
	
	@ForgeSubscribe
	public void onJumpingCostStamina(LivingJumpEvent event) {
		if(!(event.entityLiving instanceof EntityPlayer))return;

		PlayerFactor pf = PlayerStat.getStat((EntityPlayer) event.entityLiving).getFactor("stamina");

		pf.reserve -= 10;
		pf.saturation = Math.max(20, pf.saturation);
	}

	@ForgeSubscribe
	public void onPlayerFinishUse(BeforeEntityFinishUseItemEvent event)
	{
		ItemStack is = event.ep.getItemInUse();

		//drink water bottles
		if(is.itemID == Item.potion.itemID)
		{
			List effects = Item.potion.getEffects(is);
			if(effects == null || effects.isEmpty())
			{
				PlayerFactor thirst = PlayerStat.getStat(event.ep).getFactor("thirst");
				thirst.value += 2000;
				thirst.saturation += 1200;

				thirst.value = Math.min(thirst.value, thirst.max);
				thirst.saturation = Math.min(thirst.saturation, thirst.value);
			}
		}
	}

	@ForgeSubscribe
	public void onPlayerUseBowl(PlayerInteractEvent event)
	{
		EntityPlayer ep = event.entityPlayer;
		World world = event.entityPlayer.worldObj;

		if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
		{
			
			ItemStack is = ep.getCurrentEquippedItem();
			if(is != null && is.itemID == Item.bowlEmpty.itemID)
			{
				if(!ItemDrink.instance.isLookingAtWater(ep))return;

				event.entityPlayer.playSound("random.drink", 0.5F, event.entityPlayer.worldObj.rand.nextFloat() * 0.1F + 0.9F);

				PlayerFactor thirst = PlayerStat.getStat(event.entityPlayer).getFactor("thirst");
				thirst.value += 250;
				thirst.saturation += 750;

				thirst.value = Math.min(thirst.value, thirst.max);
				thirst.saturation = Math.min(thirst.saturation, thirst.value);
				
				PlayerFactor health = PlayerStat.getStat(event.entityPlayer).getFactor("health");
				health.value -- ;
			}
		}
		else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			ItemStack is = ep.getCurrentEquippedItem();
			if(is != null && is.itemID == Item.bowlEmpty.itemID)
			if(world.getBlockId(event.x,event.y,event.z) == Block.cauldron.blockID)
			{
				int meta = world.getBlockMetadata(event.x,event.y,event.z);
				if(meta <= 0)return;
				
				event.entityPlayer.playSound("random.drink", 0.5F, event.entityPlayer.worldObj.rand.nextFloat() * 0.1F + 0.9F);

				PlayerFactor thirst = PlayerStat.getStat(event.entityPlayer).getFactor("thirst");
				thirst.value += 100;
				thirst.saturation += 300;

				thirst.value = Math.min(thirst.value, thirst.max);
				thirst.saturation = Math.min(thirst.saturation, thirst.value);
				
				if(!world.isRemote)
					if(world.rand.nextFloat() <= 0.05)world.setBlockMetadataWithNotify(event.x,event.y,event.z, meta - 1, 2);
			}
		}
	}
}
