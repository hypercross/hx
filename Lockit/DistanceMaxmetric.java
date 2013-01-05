package hx.Lockit;

import com.savarese.spatial.Distance;

public class DistanceMaxmetric implements Distance<Integer,Location>{

	@Override
	public double distance(Location arg0, Location arg1) {
		int dist = 0;
		if(Math.abs(arg0.x() - arg1.x()) > dist)dist = Math.abs(arg1.x() - arg0.x());
		if(Math.abs(arg0.y() - arg1.y()) > dist)dist = Math.abs(arg1.y() - arg0.y());
		if(Math.abs(arg0.z() - arg1.z()) > dist)dist = Math.abs(arg1.z() - arg0.z());
		
		return dist;
	}

	@Override
	public double distance2(Location arg0, Location arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

}
