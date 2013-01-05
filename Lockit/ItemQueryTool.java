package hx.Lockit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemQueryTool extends ItemChisel {

	public ItemQueryTool(int par1) {
		super(par1);
		setItemName("itemQueryTool");
	}

	public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int face, float xs, float ys, float zs)
    {
		System.err.println((byte)((byte) (1 << 7) & (byte)-1));
		
		if(!(w.getBlockTileEntity(x, y, z) instanceof TileEntityMonument))return false;
		TileEntityMonument tem = (TileEntityMonument) w.getBlockTileEntity(x, y, z);
		
		
		ep.addChatMessage("Client side: " + w.isRemote + "==================");
		ep.addChatMessage("Activated Face:" + ForgeDirection.getOrientation(tem.activatedFace).toString());
		ep.addChatMessage("landmark range: "+tem.landmarkRange);
		ep.addChatMessage("plot range: "+tem.plotRange);
		ep.addChatMessage("mile range: "+tem.milestoneRange);
		ep.addChatMessage("state: " + tem.state);
		
		String p="";
		for(int i =1;i<256;i=i<<1)
			p += tem.getPermission((byte) i) ? "T " : "F ";
		ep.addChatMessage(p);
		
		System.err.println(ModLockit.instance.monuments);
		return false;
    }
}
