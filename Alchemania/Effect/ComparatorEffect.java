package hx.Alchemania.Effect;

import java.util.Comparator;

public class ComparatorEffect implements Comparator<AlchemaniaEffect> {

	@Override
	public int compare(AlchemaniaEffect a, AlchemaniaEffect b) {
		if(b == null)return -1;
		if(a == null)return 1;
		if(a.purity != b.purity) return a.purity > b.purity ? -1 : 1;
		return 0;
	}

}
