package hx.Lockit;

import com.savarese.spatial.GenericPoint;

class Location extends GenericPoint<Integer> implements Comparable<Location>
{
	public Location(int x,int y,int z)
	{
		super(x,y,z);
	}
	
	public int x()
	{
		return super.getCoord(0);
	}
	
	public int y()
	{
		return super.getCoord(1);
	}
	
	public int z()
	{
		return super.getCoord(2);
	}

	@Override
	public int compareTo(Location o) {
		if(this.x()!= o.x())return x() - o.x();
		if(this.y()!= o.y())return y() - o.y();
		return z() - o.z();
	}
}