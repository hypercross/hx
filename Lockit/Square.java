package hx.Lockit;

import hx.utils.Debug;
import net.minecraft.world.ChunkPosition;

public class Square {

	public ChunkPosition st,ed;
	public TagList tags;
	
	public boolean contains(int x,int y,int z)
	{
		if(!inRange(x,st.x,ed.x))return false;
		if(!inRange(y,st.y,ed.y))return false;
		if(!inRange(z,st.z,ed.z))return false;
		return true;
	}
	
	private boolean inRange(int x, int b1, int b2)
	{
		if(x > b1)return x <= b2;
		if(x > b2)return x <= b1;
		return x == b1 || x == b2;
	}
	
	public Square(int... pos)
	{
		if(pos.length<6)
		{
			Debug.dafuq("Landmark edge needs 6 ints! ");
		}
		st = new ChunkPosition(pos[0],pos[1],pos[2]);
		ed = new ChunkPosition(pos[3],pos[4],pos[5]);
	}
}
