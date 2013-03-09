package hx.MinePainter;

import hx.utils.Debug;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class EventHandler {
	
	@ForgeSubscribe(priority = EventPriority.LOWEST)
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
			
			BlockSculpture.createEmpty = false;
			
			for(int i =0;i<16;i++)
				if(sculpture.materialBlock(i).blockID == materialID)
				{
					event.entity.worldObj.setBlockAndMetadata(event.x, event.y, event.z, sculpture.blockID, i);
					return;
				}
		}else if(event.entity.worldObj.getBlockId(event.x, event.y, event.z) != sculpture.blockID){
			int x = event.x + Facing.offsetsXForSide[event.face];
			int y = event.y + Facing.offsetsYForSide[event.face];
			int z = event.z + Facing.offsetsZForSide[event.face];
			
			System.err.println("blah "  + event.face);
			if(event.entity.worldObj.isAirBlock(x, y, z) || event.entity.worldObj.getBlockId(x,y,z) == sculpture.blockID)
				sculpture.onBlockActivated(event.entity.worldObj, event.x, event.y, event.z, event.entityPlayer, 0, 0, 0, 0);
				
		}
	}

	@ForgeSubscribe
	public void onDrawBlockhightlight(DrawBlockHighlightEvent event)
	{
		int blockID = event.player.worldObj.getBlockId(event.target.blockX,
				event.target.blockY, event.target.blockZ) ;
		
		BlockSculpture sculpture = (BlockSculpture) ModMinePainter.instance.block("Sculpture").block();
		
		int mode = TileEntitySculpture.getMode(event.player.getCurrentEquippedItem());
		if(mode == -1)return;
		
		boolean valid = false;
		if(blockID == ModMinePainter.instance.block("Sculpture").blockID)valid = true;
		else for(int i =0;i<16;i++)
			if(sculpture.materialBlock(i).blockID == blockID)
			{
				valid = true;
				break;
			}
		
		if(mode < 3 && !valid)return;
		
		TileEntitySculpture tes = (TileEntitySculpture) 
				event.player.worldObj.getBlockTileEntity(
				event.target.blockX,
				event.target.blockY, 
				event.target.blockZ);
		if(tes == null) tes = TileEntitySculpture.full;
		tes.xCoord = event.target.blockX;
		tes.yCoord = event.target.blockY;
		tes.zCoord = event.target.blockZ;
		
		Vec3 look = event.player.getLookVec();
		look = look.addVector(look.xCoord * 4, look.yCoord * 4, look.zCoord * 4);
		int[] pos = tes.rayTrace(event.player.getPosition(1.0f), look);
		
		pos = tes.selectionBox(pos, mode, tes.getAxis(look), tes.getMinMax());
		
		if(tes != TileEntitySculpture.full)
		{
			if(pos != null)
				sculpture.setBlockBounds(
						pos[0]/8f,
						pos[1]/8f,
						pos[2]/8f,
						pos[3]/8f,
						pos[4]/8f,
						pos[5]/8f);

			sculpture.onSelect = true;
			event.context.drawSelectionBox(event.player, event.target, 0, null, event.partialTicks);
			sculpture.onSelect = false;
			
		}else if(pos != null)
		{
			double[] bounds = new double[]{
					Block.blocksList[blockID].getBlockBoundsMinX(),
					Block.blocksList[blockID].getBlockBoundsMinY(),
					Block.blocksList[blockID].getBlockBoundsMinZ(),
					Block.blocksList[blockID].getBlockBoundsMaxX(),
					Block.blocksList[blockID].getBlockBoundsMaxY(),
					Block.blocksList[blockID].getBlockBoundsMaxZ()};
			
			Block.blocksList[blockID].setBlockBounds(
					pos[0]/8f,
					pos[1]/8f,
					pos[2]/8f,
					pos[3]/8f,
					pos[4]/8f,
					pos[5]/8f);
			
			event.context.drawSelectionBox(event.player, event.target, 0, null, event.partialTicks);
			
			Block.blocksList[blockID].setBlockBounds(
					(float)bounds[0],(float)bounds[1],(float)bounds[2],
					(float)bounds[3],(float)bounds[4],(float)bounds[5]);
		}else return;
		
		event.setCanceled(true);
	}
}
