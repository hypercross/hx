package hx.Lockit;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.world.World;

public class BlockTagAccess {

	HashMap<Long, ArrayList<Square>> square_list = new HashMap<Long, ArrayList<Square>>();
	World worldObj;
	
	public void load()
	{
		//TODO - parse square save file in save folder
	}
	
	public void save()
	{
		//TODO - save square list
	}
	
	
}
