package hx.Lockit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLock extends TileEntity
{
    public int key, keyColor = -1;
    public String keyName;
    public boolean isSolid = true;
    public boolean hasKey = true;

    public TileEntityLock()
    {
    }

    public void setNewKey()
    {
        key = Keybase.getUnusedKey();
        Keybase.registerKey(key);
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("lock_key", this.key);
        par1NBTTagCompound.setBoolean("isSolid", isSolid);
        par1NBTTagCompound.setBoolean("hasKey", hasKey);

        if (keyColor != -1)
        {
            par1NBTTagCompound.setInteger("key_color", this.keyColor);
        }

        if (keyName != null)
        {
            par1NBTTagCompound.setString("key_name", this.keyName);
        }
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        Keybase.unregisterKey(key);
        super.readFromNBT(par1NBTTagCompound);
        this.key = par1NBTTagCompound.getInteger("lock_key");
        this.isSolid = par1NBTTagCompound.getBoolean("isSolid");
        this.hasKey = par1NBTTagCompound.getBoolean("hasKey");

        if (par1NBTTagCompound.hasKey("key_color"))
        {
            this.keyColor = par1NBTTagCompound.getInteger("key_color");
        }
        else
        {
            keyColor = -1;
        }

        if (par1NBTTagCompound.hasKey("key_name"))
        {
            this.keyName = par1NBTTagCompound.getString("key_name");
        }
        else
        {
            this.keyName = null;
        }

        Keybase.registerKey(key);
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
    }
}
