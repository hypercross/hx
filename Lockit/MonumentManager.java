package hx.Lockit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import com.savarese.spatial.Distance;
import com.savarese.spatial.KDTree;

public class MonumentManager {
	
	private static int mID; 
	
	private  KDTree<Integer,Location,String> landmarks = new KDTree<Integer,Location,String>(3);
	
	private  KDTree<Integer,Location,Integer> guardBlocks = new KDTree<Integer,Location,Integer>(3);
	
	private HashMap<Location,Location> cachedToLandmark = new HashMap<Location,Location>();
	private HashMap<Location,Location> cachedToPlotmark = new HashMap<Location,Location>();
	
	private Distance distFunc = new DistanceMaxmetric();
	
	public MonumentManager()
	{
		mID = ModLockit.instance.block("Monument").blockID;
	}
	
	private boolean isPlot(Location loc,World w)
	{
		TileEntityMonument tem = (TileEntityMonument)w.getBlockTileEntity(loc.x(), loc.y(), loc.z());
		if(tem == null)return false;
		if(tem.state != TileEntityMonument.MonumentState.Plotmark)return false;
		return true;
	}
	
	private boolean isLandmark(Location loc,World w)
	{
		TileEntityMonument tem = (TileEntityMonument)w.getBlockTileEntity(loc.x(), loc.y(), loc.z());
		if(tem == null)return false;
		if(tem.state != TileEntityMonument.MonumentState.Landmark)return false;
		return true;
	}
	
	public TileEntityMonument permissionOwner(int x,int y,int z,World w)
	{
		int dist = ModLockit.maxPlotRange;
		Location s = new Location(x-dist,y-dist,z-dist);
		Location e = new Location(x+dist,y+dist,z+dist);
		Location c = new Location(x		,y		,z);
		
		if(cachedToPlotmark.containsKey(c))
		{
			Location t = cachedToPlotmark.get(c); 
			if(isPlot(t,w))
				return (TileEntityMonument)w.getBlockTileEntity(t.x(), t.y(), t.z());
			else cachedToPlotmark.remove(c);
		}
		
		Iterator<Map.Entry<Location,Integer>> it = guardBlocks.iterator(s, e);
		
		Location closest = null;
		
		while(it.hasNext())
		{
			Location loc = it.next().getKey();
			if(!isPlot(loc,w))continue;
			TileEntityMonument tem = (TileEntityMonument)w.getBlockTileEntity(loc.x(), loc.y(), loc.z());
			if(tem == null)continue;
			if(tem.landmark() == null)continue;
			if(distFunc.distance(loc, c) > tem.landmark().plotRange)continue;
			
			if(closest == null)closest = loc;
			else if(distFunc.distance(loc, c) < distFunc.distance(closest, c))
				closest = loc;
		}
		
		if(closest == null)return null;
		
		cachedToPlotmark.put(c, closest);
		
		return (TileEntityMonument) w.getBlockTileEntity(closest.x(), closest.y(), closest.z());
	}
	
	public Location nearestLandmark(int x,int y,int z,World w)
	{
		int dist = ModLockit.maxLandmarkRange;
		Location s = new Location(x-dist,y-dist,z-dist);
		Location e = new Location(x+dist,y+dist,z+dist);
		Location c = new Location(x		,y		,z);
		
		if(cachedToLandmark.containsKey(c))
		{
			Location t = cachedToLandmark.get(c); 
			if(isLandmark(t,w))
				return t;
			else cachedToPlotmark.remove(c);
		}
		
		Iterator<Map.Entry<Location,String>> it = landmarks.iterator(s, e);
		
		Location closest = null;
		
		while(it.hasNext())
		{
			Location loc = it.next().getKey();
			TileEntityMonument tem = (TileEntityMonument)w.getBlockTileEntity(loc.x(), loc.y(), loc.z());
			if(tem == null)continue;
			if(distFunc.distance(loc, c) > tem.landmarkRange)continue;
			
			if(closest == null)closest = loc;
			else if(distFunc.distance(loc, c) < distFunc.distance(closest, c))
				closest = loc;
		}
		
		if(closest != null)
			cachedToLandmark.put(c, closest);
		
		return closest;
	}
	
	public void reportRemove(int x,int y,int z)
	{
		Location loc = new Location(x,y,z);
		landmarks.remove(loc);
		guardBlocks.remove(loc);
	}
	
	public void reportAdd(int x,int y,int z,World w)
	{	
		Location loc = new Location(x,y,z);
		if(landmarks.containsKey(loc))return;
		if(guardBlocks.containsKey(loc))return;
		
		if(w.getBlockId(x,y,z) != mID)return;
		TileEntityMonument tem = (TileEntityMonument) w.getBlockTileEntity(x, y, z);
		if(tem.state == TileEntityMonument.MonumentState.Invalid)return;
		ForgeDirection dir = ForgeDirection.getOrientation(tem.activatedFace);
		TileEntityEngraving engraving =
				(TileEntityEngraving) w.getBlockTileEntity(x + dir.offsetX, y + dir.offsetY , z + dir.offsetZ);
		if(engraving == null)return;		
		
		if(tem.state == TileEntityMonument.MonumentState.Landmark)
			landmarks.put(loc, engraving.signText[1]);
		else if(tem.state == TileEntityMonument.MonumentState.Milestone)
			guardBlocks.put(loc, (int)tem.milestoneRange);
		else guardBlocks.put(loc, (int) tem.plotRange);
		tem.name = engraving.signText[1];
	}
	
	public String nameOfLoc(Location loc,World w)
	{
		TileEntityMonument tem = (TileEntityMonument) w.getBlockTileEntity(loc.x(), loc.y(), loc.z());		
		return tem.name;
	}
	
	public String toString()
	{
		String str = "";

		for(Location loc : landmarks.keySet())
		{
			str += loc.toString() + " ";
		}
		
		for(Location loc : guardBlocks.keySet())
		{
			str += loc.toString() + " ";
		}
		
		return str;
	}
}
