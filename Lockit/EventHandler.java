package hx.Lockit;

import hx.Lockit.LockProtectionHelper.LockLevel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class EventHandler
{
    public static EventHandler instance;

    private static int[] YOFF = { -1, 1, 0, 0, 0, 0};
    private static int[] ZOFF = {0, 0, -1, 1, 0, 0};
    private static int[] XOFF = {0, 0, 0, 0, -1, 1};

    public EventHandler()
    {
        new LockProtectionHelper();
        instance = this;
    }

    public boolean canPushBlock(int par0, World par1World, int par2, int par3, int par4, boolean par5)
    {
        if (LockProtectionHelper.getLockLevel(par2, par3, par4, par1World) > LockLevel.NearLock)
        {
            return false;
        }
        if(!LandmarkProtectionHelper.canBuild(par2, par3, par4, par1World))
        	return false;

        if (par0 == Block.obsidian.blockID)
        {
            return false;
        }
        else
        {
            if (par0 != Block.pistonBase.blockID && par0 != Block.pistonStickyBase.blockID)
            {
                if (Block.blocksList[par0].getBlockHardness(par1World, par2, par3, par4) == -1.0F)
                {
                    return false;
                }

                if (Block.blocksList[par0].getMobilityFlag() == 2)
                {
                    return false;
                }

                if (!par5 && Block.blocksList[par0].getMobilityFlag() == 1)
                {
                    return false;
                }
            }
            else if (BlockPistonBase.isExtended(par1World.getBlockMetadata(par2, par3, par4)))
            {
                return false;
            }

            return !par1World.blockHasTileEntity(par2, par3, par4);
        }
    }

    public void resolveExplosion(Explosion exp, World w)
    {
        LockProtectionHelper.resolveExplosion(exp, w);
        LandmarkProtectionHelper.resolveExplosion(exp, w);
    }

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onClientBreakLocked(BreakSpeed ev)
    {
        if (ev.entityPlayer.worldObj.isRemote)
        {
            EntityClientPlayerMP ecmp = (EntityClientPlayerMP)(ev.entityPlayer);
            MovingObjectPosition p = ecmp.rayTrace(5f, 1f);

            if (p != null)
            {
	            if (!LockProtectionHelper.canBuild(p.blockX, p.blockY, p.blockZ, ecmp.worldObj))
	            {
	                ev.setCanceled(true);
	            	return;
	            }
            
	            if(!LandmarkProtectionHelper.canBuild(p.blockX, p.blockY, p.blockZ, ecmp.worldObj))
	            {
	            	ev.setCanceled(true);
	            	return;
	            }
	            
	            if(isMarkEngraving(p.blockX, p.blockY, p.blockZ, ecmp.worldObj))
	            {
	            	ev.setCanceled(true);
	            	return;
	            }
            }
        }
    }

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onInteractLocked(PlayerInteractEvent event)
    {
        World w = event.entityPlayer.worldObj;
        int face = Math.max(0, event.face);
        int x = event.x + XOFF[face];
        int y = event.y + YOFF[face];
        int z = event.z + ZOFF[face];
        
        if(isMarkEngraving(event.x,event.y,event.z,event.entity.worldObj))
        {
        	event.setCanceled(true);
        	return;
        }

        if (LockProtectionHelper.isBuildingWith(event.entityPlayer, new int[] {LockProtectionHelper.hlockID}))
        {
            event.useBlock = Result.DENY;

            if (event.useItem != Result.DENY)
            {
                event.useItem = Result.ALLOW;
            }
        }

        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)
        {
            if (!LockProtectionHelper.canBuild(event.x, event.y, event.z, w))
            {
                event.setCanceled(true);
                event.entityPlayer.addChatMessage("That block can't be broken, it's locked.");
            }
            if(!LandmarkProtectionHelper.canBuild(event.x, event.y, event.z, w))
            {
            	event.setCanceled(true);
            	event.entityPlayer.addChatMessage("That block can't be broken, it's protected.");
            }
            
        }
        else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
        {
            if (!LockProtectionHelper.canUseBlock(event.x, event.y, event.z, w))
            {
                event.useBlock = Result.DENY;

                if (w.isRemote)
                {
                    event.entityPlayer.addChatMessage("That block can't be used, it's locked.");
                }
            }

            if (LockProtectionHelper.isBuilding(event.entityPlayer) && !LockProtectionHelper.canBuild(x, y, z, w))
            {
                event.useItem = Result.DENY;

                if (w.isRemote)
                {
                    event.entityPlayer.addChatMessage("That location can't be build upon, it's locked.");
                }
            }

            if (LockProtectionHelper.isBuildingWith(event.entityPlayer, ModLockit.bannedNearLock) &&
                    !LockProtectionHelper.canBuildLock(x, y, z, w))
            {
            	event.useItem = Result.DENY;

                if (w.isRemote)
                {
                    event.entityPlayer.addChatMessage("You can't build things that might be harmful there, it's near a lock.");
                }
            }
            
            if(LockProtectionHelper.isBuilding(event.entityPlayer) && 
            		!LandmarkProtectionHelper.canBuild(x, y, z, w))
            {
            	event.useItem = Result.DENY;
            	event.entityPlayer.addChatMessage("That location can't be build upon, it's protected.");
            }
            
            if(event.useBlock == Result.DENY || event.useItem == Result.DENY)
            	if(event.useBlock != Result.ALLOW && event.useItem != Result.ALLOW)
            		event.setCanceled(true);
        }
    }
    
    @ForgeSubscribe
    public void onChunkLoadReportMonument(ChunkEvent.Load load)
    {
    	for(Object te : load.getChunk().chunkTileEntityMap.values())
    		if(te instanceof TileEntityMonument)
    		{
    			TileEntityMonument tem = (TileEntityMonument) te;
    			ModLockit.instance.monuments
    			.reportAdd(tem.xCoord,tem.yCoord,tem.zCoord,tem.worldObj);
    		}
    }
    
    @ForgeSubscribe
    public void onMobSpawn(EntityJoinWorldEvent event)
    {
    	if(event.entity instanceof EntityMob)
    	{
    		if(!LandmarkProtectionHelper.canSpawnMonster((int)event.entity.posX, (int)event.entity.posY, (int)event.entity.posZ, event.world))
    			event.setCanceled(true);
    	}else return;
    }
    
    @ForgeSubscribe
    public void onPvpEvent(AttackEntityEvent aee)
    {
    	if(aee.target instanceof EntityPlayer)
    	{
    		if(!LandmarkProtectionHelper.canPvp((int)aee.target.posX, (int)aee.target.posY, (int)aee.target.posZ, aee.entity.worldObj))
    			aee.setCanceled(true);
    	}
    }
    
    private boolean isMarkEngraving(int x,int y,int z,World w)
    {
    	if(w.getBlockId(x, y,z)!= ModLockit.instance.block("Engraving").blockID)return false;
    	
    	int face = w.getBlockMetadata(x, y, z);
    	if(face >= 8)face = 1;
        else if(face<4)face = 0;
        else face -=2;
        ForgeDirection dir = ForgeDirection.getOrientation(face);
        
        int _x = x - dir.offsetX;
        int _y = y - dir.offsetY;
        int _z = z - dir.offsetZ;
        
        TileEntity te = w.getBlockTileEntity(_x, _y, _z);
        if(te == null)return false;
        if(te instanceof TileEntityMonument)
        {
        	if(((TileEntityMonument) te).activatedFace == face)
        		return true;
        }
        return false;
    }
}
