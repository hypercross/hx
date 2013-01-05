package hx.Lockit;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

@SideOnly(Side.CLIENT)
public class GuiEditEngraving extends GuiEditSign
{
    TileEntityEngraving entityEngraving;
    private int editLine = 0;

    public GuiEditEngraving(TileEntitySign par1TileEntitySign)
    {
        super(par1TileEntitySign);
        entityEngraving = (TileEntityEngraving) par1TileEntitySign;
        screenTitle = "Edit engraving";
    }

    @Override
    public void initGui()
    {
    	super.initGui();
    	this.entityEngraving.lineBeingEdited = editLine;
    	
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
        World w = entityEngraving.worldObj;
        int mID = w.getBlockId(x, y, z);
        if(mID!= ModLockit.instance.block("Monument").blockID)return;
        
        while(mID == w.getBlockId(x,y,z))y++;
        y--;
        
        TileEntityMonument tem = (TileEntityMonument) w.getBlockTileEntity(x, y, z);
        for(int i =0;i<4;i++)
        {
        	int[] info = PacketHandler.parsePacket(tem, entityEngraving.signText[i]);
        	if(info != null)
        		this.mc.getSendQueue().addToSendQueue(PacketHandler.getPacket(x, y, z, info[0], info[1]));
        }
    }

    public void updateScreen()
    {
    }

    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 200)
        {
            this.editLine = this.editLine - 1 & 3;
        }

        if (par2 == 208 || par2 == 28)
        {
            this.editLine = this.editLine + 1 & 3;
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
