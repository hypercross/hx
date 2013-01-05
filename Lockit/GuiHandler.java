package hx.Lockit;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world,
            int x, int y, int z)
    {
        if (ID == 0)
            return new ContainerKeychain(player.inventory,
                    new InventoryKeychain(player.inventory.getCurrentItem()));
        else if (ID < 0)
        {
            return new ContainerDummy();
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
            int x, int y, int z)
    {
        if (ID == 0)
            return new GuiKeychain(player.inventory,
                    new InventoryKeychain(player.inventory.getCurrentItem()));
        else if (ID == -1)
        {
            return new GuiEditEngraving((TileEntitySign) world.getBlockTileEntity(x, y, z));
        }
        else if (ID == -2)
        {
            return new GuiEditLandmark((TileEntitySign) world.getBlockTileEntity(x, y, z));
        }
        else if (ID == -3)
        {
            return new GuiEditPlotmark((TileEntitySign) world.getBlockTileEntity(x, y, z));
        }

        return null;
    }
}
