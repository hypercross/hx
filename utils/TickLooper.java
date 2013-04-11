package hx.utils;

public class TickLooper {

	int tick = 0;
	int skip = 0;
	int hault = 0;
	final int max;
	
	public TickLooper(int max)
	{
		this.max = max;
	}
	
	public boolean tick()
	{
		tick++;
		tick %= max;
		return tick == 0;
	}
	
	public boolean tickWithSkip()
	{
		boolean tick = tick();
		if(!tick)return false;

		if(skip == 0)return true;
		skip--;
		return false;
	}
	
	public boolean tickWithHault()
	{
		if(hault>0)
		{
			hault -- ;
			return false;
		}
		return tick();
	}
	
	public void skip(int count)
	{
		skip += count;
	}
	
	public void hault(int count)
	{
		hault += count;
	}

	public void reset() {
		hault = skip = 0;
	}
}
