package hx.Lockit;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class LandmarkProtectionHelper {

	public static boolean canSpawnMonster(int x,int y,int z,World w)
	{
		TileEntityMonument[] marks = getMarks(x,y,z,w);
		if(marks == null)return true;
		
		return marks[1].getPermission(TileEntityMonument.FLAG_MONSTERS);
	}
	
	public static boolean canPvp(int x,int y,int z,World w)
	{
		TileEntityMonument[] marks = getMarks(x,y,z,w);
		if(marks == null)return true;
		
		return marks[1].getPermission(TileEntityMonument.FLAG_PVP);
	}
	
	public static boolean canBuild(int x,int y,int z,World w)
	{
		if(ModLockit.protectSky)while(w.isAirBlock(x, y, z))y--;
		return canBuild(getMarks(x,y,z,w));
	}
	
	private static TileEntityMonument[] getMarks(int x,int y,int z,World w)
	{
		TileEntityMonument tem = ModLockit.instance.monuments.permissionOwner(x,y,z,w);
        if(tem == null)return null;

        Location loc = ModLockit.instance.monuments
        		.nearestLandmark(tem.xCoord,tem.yCoord,tem.zCoord,w);
        if(loc == null)return null;
        TileEntityMonument temLandmark = (TileEntityMonument) w.getBlockTileEntity(loc.x(),loc.y(),loc.z()); 
        if(temLandmark == null)return null;
        
        return new TileEntityMonument[]{tem, temLandmark};
	}
	
	private static boolean canBuild(TileEntityMonument[] marks)
	{
		if(marks == null)return true;
		
		boolean build = marks[1].getPermission(TileEntityMonument.FLAG_BUILD);
		boolean lock  = marks[1].getPermission(TileEntityMonument.FLAG_ALWAYS_LOCK);
		if(build)return true;
		if(lock)return false;
		
		return LockProtectionHelper.canBuild(marks[0].xCoord, marks[0].yCoord, marks[0].zCoord, marks[0].worldObj);
	}
	
	public static void resolveExplosion(Explosion exp, World w)
    {
        if (exp instanceof Explosion)
        {
            Explosion explosion = (Explosion)exp;

            for (int i = explosion.affectedBlockPositions.size() - 1; i >= 0; i--)
            {
                ChunkPosition position = (ChunkPosition)(explosion.affectedBlockPositions.get(i));

                if (!canBuild(position.x, position.y, position.z, (World)w))
                {
                    explosion.affectedBlockPositions.remove(i);
                }
            }
        }
    }
}
