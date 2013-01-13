package hx.utils;

import java.lang.reflect.Array;

public class Debug {
	public static void dafuq()
	{
		System.err.println("dafuq");
	}
	
	public static void dafuq(Object obj)
	{
		if(obj instanceof int[])
		{
			int[] array = (int[])obj;
			for(int one : array)
				System.err.print(one + ", ");
			System.err.println();
		}
		System.err.println(obj);
	}
}
