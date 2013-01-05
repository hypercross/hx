package hx.Lockit;

import java.util.TreeSet;

public class Keybase
{
    private static TreeSet keys = new TreeSet();

    public static int getUnusedKey()
    {
        int i = new Object().hashCode();

        while (keys.contains(i))
        {
            i++;
        }

        return i;
    }

    public static void clear()
    {
        keys.clear();
    }

    public static void registerKey(int i)
    {
        keys.add(i);
    }

    public static void unregisterKey(int i)
    {
        keys.remove(i);
    }
}
