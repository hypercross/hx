package hx.Lockit;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityMonument extends TileEntity {

	public static final byte FLAG_AUTO_ASSIGN = 1;
	public static final byte FLAG_ALWAYS_LOCK = 2;
	public static final byte FLAG_NEW  		  = 4;
	public static final byte FLAG_ANIMALS 	  = 8;
	public static final byte FLAG_MONSTERS	 = 16;
	public static final byte FLAG_MOVE		 = 32;
	public static final byte FLAG_BUILD		 = 64;
	public static final byte FLAG_PVP		 = -128;
	
	public String name="";
	public MonumentState state;
	private byte permissions;
	
	public byte plotRange,milestoneRange;
	public int landmarkRange;
	
	//for display
	public byte activatedFace = 0;
	public enum MonumentState
	{
		Invalid,
		Landmark,
		Milestone,
		Plotmark
	}
	
	public TileEntityMonument()
	{
		super();
		state = MonumentState.Invalid;
		initPerm();
		plotRange = (byte) ModLockit.defaultPlotRange;
		landmarkRange = ModLockit.defaultLandmarkRange;
		milestoneRange = (byte) ModLockit.defaultMilestoneRange; 
	}
	
	public boolean getPermission(byte flag)
	{
		return (flag & permissions) != 0;
	}
	
	public void setPermission(byte flag, boolean state)
	{
		permissions |= flag;
		if(!state)permissions ^= flag;
	}
	
	public TileEntityMonument landmark()
	{
		Location loc = ModLockit.instance.monuments.nearestLandmark(xCoord, yCoord, zCoord, worldObj);
		if(loc ==null)return null;
		return (TileEntityMonument) worldObj.getBlockTileEntity(loc.x(),loc.y(), loc.z());
	}

	private byte toByte(MonumentState state)
	{
		switch(state)
		{
		case Invalid :
			return 0;
		case Landmark :
			return 1;
		case Milestone :
			return 2;
		case Plotmark :
			return 3;
		}
		return 0;
	}
	
	public MonumentState toState(byte abyte)
	{
		switch(abyte)
		{
		case 0:
			return MonumentState.Invalid;
		case 1:
			return MonumentState.Landmark;
		case 2:
			return MonumentState.Milestone;
		case 3:
			return MonumentState.Plotmark;
		}
		return MonumentState.Invalid;
	}
	
	public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("name", name);
        nbt.setByte("state", toByte(state));
        nbt.setByte("permissions", permissions);
        nbt.setByte("plotRange", plotRange);
        nbt.setByte("milestoneRange", milestoneRange);
        nbt.setInteger("landmarkRange", landmarkRange);
        nbt.setByte("activatedFace", activatedFace);
    }
	
	public void readFromNBT(NBTTagCompound nbt)
    {
		super.readFromNBT(nbt);
		name			= nbt.getString("name");
		state 			= toState(nbt.getByte("state"));
		permissions		= nbt.getByte("permissions");
		plotRange		= nbt.getByte("plotRange");
		milestoneRange	= nbt.getByte("milestoneRange");
		landmarkRange	= nbt.getInteger("landmarkRange");
		activatedFace	= nbt.getByte("activatedFace");
    }

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		readFromNBT(pkt.customParam1);
		if(this.worldObj.isRemote)ModLockit.instance.monuments.reportAdd(xCoord, yCoord, zCoord, worldObj);
	}

	public void initPerm() {
		permissions = FLAG_AUTO_ASSIGN | FLAG_NEW | FLAG_ANIMALS | FLAG_MONSTERS | FLAG_MOVE | FLAG_PVP;
		
	}
	
	@Override
	public void onChunkUnload()
	{
		ModLockit.instance.monuments.reportRemove(xCoord, yCoord, zCoord);
	}
}
