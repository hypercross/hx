package hx.soundbox;

import net.minecraft.nbt.NBTTagCompound;
import hx.utils.SynchronizedTileEntity;

public class TileEntityTransmittedNote extends SynchronizedTileEntity {
	
	int tickCount = 0;
	int pitch = 0;
	int prevRedstoneState = 0;
	
	public void updateEntity()
	{
		if(tickCount<=0)return;
		
		tickCount--;
		
		if(tickCount <= 0 && this.worldObj.isRemote)
		{
			float f = (float)Math.pow(2.0D, (double)(pitch - 12) / 12.0D);
			this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, "note.harp", 3.0F, f);
		}
	}

	@Override
	protected void toNBT(NBTTagCompound nbt) {
		pitch = nbt.getInteger("pitch");
	}

	@Override
	protected void fromNBT(NBTTagCompound nbt) {
		nbt.setInteger("pitch", pitch);
	}

}
