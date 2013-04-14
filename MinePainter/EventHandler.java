package hx.MinePainter;

import hx.utils.Debug;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class EventHandler {
	
	@ForgeSubscribe
	public void onPlayerRightClickSculptable(PlayerInteractEvent event)
	{
		if(event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)return;
		
		ItemStack is = event.entityPlayer.getCurrentEquippedItem();
		if(is == null)return;
		
		int mode = TileEntitySculpture.getMode(is); 
		if(mode == -1)return;
		

		BlockSculpture sculpture = (BlockSculpture) ModMinePainter.instance.block("Sculpture").block();		
		if(mode < 3)
		{
			int materialID  = event.entity.worldObj.getBlockId(event.x,event.y,event.z);
			int meta        = event.entity.worldObj.getBlockMetadata(event.x,event.y,event.z);
			
			if(materialID == 0 || BlockSculpture.instance.blockID == materialID)return;
			
			BlockSculpture.createEmpty = false;
			
				if(BlockSculpture.sculptable(materialID, meta))
				{
					event.entity.worldObj.setBlock(event.x, event.y, event.z, sculpture.blockID, meta, 3);
					TileEntitySculpture tes = (TileEntitySculpture) event.entity.worldObj.getBlockTileEntity(event.x,event.y,event.z);
					tes.blockId = materialID;
					tes.needUpdate = true;
					
					return;
				}
		}else if(event.entity.worldObj.getBlockId(event.x, event.y, event.z) != sculpture.blockID){
			int x = event.x + Facing.offsetsXForSide[event.face];
			int y = event.y + Facing.offsetsYForSide[event.face];
			int z = event.z + Facing.offsetsZForSide[event.face];
			
			if(event.entity.worldObj.isAirBlock(x, y, z) || event.entity.worldObj.getBlockId(x,y,z) == sculpture.blockID)
				sculpture.onBlockActivated(event.entity.worldObj, event.x, event.y, event.z, event.entityPlayer, 0, 0, 0, 0);
				
		}
	}
}
