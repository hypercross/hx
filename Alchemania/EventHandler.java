package hx.Alchemania;

import java.util.HashMap;

import hx.Alchemania.Effect.AlchemaniaEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

public class EventHandler {
	
	public static EntityPlayer dispenser;
	@ForgeSubscribe
	public void redirectEntity(EntityJoinWorldEvent event)
	{
		if(dispenser == null)return;
		Entity e = event.entity;
		
		Vec3 view = dispenser.getLookVec();        		
		double speed = Math.sqrt(e.motionX*e.motionX + 
								 e.motionY*e.motionY +
								 e.motionZ*e.motionZ);
		
		e.motionX = view.xCoord * speed;
		e.motionY = view.yCoord * speed;
		e.motionZ = view.zCoord * speed;     
		
		if(e instanceof IProjectile)
			((IProjectile) e).setThrowableHeading(e.motionX, 
					e.motionY, e.motionZ, 1.1F, 6.0F);
		
		dispenser = null;
	}

	//=======================================================
	
	@ForgeSubscribe 
	public void applyPotionOnHit(LivingAttackEvent event)
	{
		Entity entity = event.source.getEntity();
		if(entity == null)return;
		if(event.source.getSourceOfDamage() 
				!= event.source.getEntity())return;
		
		if(entity instanceof EntityLiving)
		{
			EntityLiving ev = (EntityLiving) entity;
			
			ItemStack is = ev.getHeldItem();
			if(is == null)return;
			if(!Alchemania.dippable(is.itemID))return;
			if(is.stackTagCompound == null)return;
			if(!is.stackTagCompound.hasKey("AME"))return;
			
			AlchemaniaEffect[] effs = AlchemaniaEffect.parseEffects(is);
			for(AlchemaniaEffect eff : effs)
			{
				eff.applyEffect(event.entityLiving);
			}
			is.stackTagCompound.removeTag("AME");
		}
	}
	
	private HashMap<IProjectile,AlchemaniaEffect[]> arrowEffs = new HashMap<IProjectile,AlchemaniaEffect[]>();
	private AlchemaniaEffect[] pendingArrowEff;	
	@ForgeSubscribe
	public void recordArrowEff(ArrowLooseEvent event)
	{
		if(event.entityPlayer.worldObj.isRemote)return;
		float var7 = (float)event.charge / 20.0F;
        var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;
		if(var7 < 0.1d)return;
		
		int size = event.entityPlayer.inventory.getSizeInventory();
		InventoryPlayer inv = event.entityPlayer.inventory;
		for(int i = 0; i < size; i ++)
		{
			ItemStack is = inv.getStackInSlot(i);
			if(is == null)continue;
			if(is.itemID != Item.arrow.shiftedIndex)continue;
			
			if(is.stackTagCompound == null)return;
			if(!is.stackTagCompound.hasKey("AME"))return;
			
			pendingArrowEff = AlchemaniaEffect.parseEffects(is);
			return;
		}
	}
	
	@ForgeSubscribe
	public void mapArrowEff(EntityJoinWorldEvent event)
	{
		if(pendingArrowEff == null)return;
		if(event.entity instanceof EntityArrow)
		{
			arrowEffs.put((IProjectile) event.entity, pendingArrowEff);
			pendingArrowEff = null;
		}
	}
	
	@ForgeSubscribe
	public void applyPotionOnArrow(LivingAttackEvent event)
	{
		if(event.entity.worldObj.isRemote)return;
		Entity entity = event.source.getSourceOfDamage();
		if(entity == null)return;
		
		if(arrowEffs.containsKey(entity))
		{
			AlchemaniaEffect[] effs = arrowEffs.get(entity);
			for(AlchemaniaEffect eff : effs)
			{
				eff.applyEffect(event.entityLiving);
			}
			arrowEffs.remove(entity);
		}
	}
	
	//=======================================================
	
}
