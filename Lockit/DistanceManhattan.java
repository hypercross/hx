package hx.Lockit;

import com.savarese.spatial.Distance;

public class DistanceManhattan implements Distance<Integer,Location>{

	@Override
	public double distance(Location arg0, Location arg1) {
		// TODO Auto-generated method stub
		return Math.abs(arg1.x() + arg1.y() + arg1.z() - arg0.x() - arg0.y() - arg0.z());
	}

	@Override
	public double distance2(Location arg0, Location arg1) {
		// TODO Auto-generated method stub
		return distance(arg0,arg1);
	}

}
