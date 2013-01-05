package hx.Lockit;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.common.ForgeDirection;

public class GuiEditLandmark extends GuiEditSign
{
	TileEntityEngraving entityEngraving;
    private int editLine = 0;

    public GuiEditLandmark(TileEntitySign par1TileEntitySign)
    {
        super(par1TileEntitySign);
        entityEngraving = (TileEntityEngraving) par1TileEntitySign;
        screenTitle = "Edit Landmark";
    }

    @Override
    public void initGui()
    {
    	super.initGui();
    	editLine = 1; 
    	this.entityEngraving.lineBeingEdited = editLine;
    	this.entityEngraving.signText[0] = "This place is :";
    	this.entityEngraving.signText[2] = "---------------";
    	this.entityEngraving.signText[3] = "---------------";
    }
    
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        
        int face = entityEngraving.getBlockMetadata();
        if(face >= 8)face = 1;
        else if(face<4)face = 0;
        else face -=2;
        ForgeDirection dir = ForgeDirection.getOrientation(face);
        
        int x = entityEngraving.xCoord - dir.offsetX;
        int y = entityEngraving.yCoord - dir.offsetY;
        int z = entityEngraving.zCoord - dir.offsetZ;
        
        this.mc.getSendQueue().addToSendQueue(PacketHandler.getPacket(x, y, z, 0, 1));
        
        TileEntityMonument tem = (TileEntityMonument) entityEngraving.worldObj.getBlockTileEntity(x, y, z);
        if(tem == null)return;//HMMM
        tem.state = TileEntityMonument.MonumentState.Landmark;
        ModLockit.instance.monuments.reportAdd(x, y, z, entityEngraving.worldObj);
    }
    
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 40, 16777215);

        for (int var4 = 0; var4 < this.controlList.size(); ++var4)
        {
            GuiButton var5 = (GuiButton)this.controlList.get(var4);
            var5.drawButton(this.mc, par1, par2);
        }
    }

    public void updateScreen()
    {
    }

    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 200)
        {
            this.editLine = this.editLine - 4 & 3;
        }

        if (par2 == 208 || par2 == 28)
        {
            this.editLine = this.editLine + 4 & 3;
        }

        if (par2 == 14 && this.entityEngraving.signText[this.editLine].length() > 0)
        {
            this.entityEngraving.signText[this.editLine] = this.entityEngraving.signText[this.editLine].substring(0, this.entityEngraving.signText[this.editLine].length() - 1);
        }

        if (ChatAllowedCharacters.allowedCharacters.indexOf(par1) >= 0 && this.entityEngraving.signText[this.editLine].length() < 12)
        {
            this.entityEngraving.signText[this.editLine] = this.entityEngraving.signText[this.editLine] + par1;
        }

        this.entityEngraving.lineBeingEdited = editLine;
    }
}
