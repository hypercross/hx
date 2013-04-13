package hx.MinePainter;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class EventHandlerClient {

	@ForgeSubscribe
	public void onDrawBlockhightlight(DrawBlockHighlightEvent event)
	{
		int blockID = event.player.worldObj.getBlockId(event.target.blockX,
				event.target.blockY, event.target.blockZ) ;
		int blockMeta = event.player.worldObj.getBlockMetadata(event.target.blockX,
				event.target.blockY, event.target.blockZ) ;
		
		BlockSculpture sculpture = (BlockSculpture) ModMinePainter.instance.block("Sculpture").block();
		
		int mode = TileEntitySculpture.getMode(event.player.getCurrentEquippedItem());
		if(mode == -1)return;
		
		boolean valid = false;
		if(blockID == sculpture.blockID)valid = true;
		else 
			if(BlockSculpture.sculptable(blockID, blockMeta))
			{
				valid = true;
			}
		
		if(mode < 3 && !valid)return;
		if(blockID != sculpture.blockID && event.player.worldObj.blockHasTileEntity(
				event.target.blockX,
				event.target.blockY, 
				event.target.blockZ))return;
		
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
