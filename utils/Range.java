package hx.utils;

public class Range {

	final int s;
	final int e;
	
	boolean lastIn = false;
	public Range(int s, int e)
	{
		this.s = s;
		this.e = e;
	}
	
	public boolean isIn(int num)
	{
		return lastIn = s <= num && num < e;
	}
	
	public boolean wentIn(int num)
	{
		boolean last = lastIn;
		isIn(num);
		return lastIn && !last;
	}
	
	public boolean wentOut(int num)
	{
		boolean last = lastIn;
		isIn(num);
		return last && !lastIn;
	}
}
