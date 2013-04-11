package hx.Lockit;

import java.util.ArrayList;
import java.util.List;

public class TagList{
	ArrayList<Tag> tags = new ArrayList<Tag>();

	public List<Tag> getTags() {
		return tags;
	}

	public boolean add(Tag tag) {
		return tags.add(tag);
	}

	public boolean remove(Tag tag) {
		return tags.remove(tag);
	}
	
	

}
