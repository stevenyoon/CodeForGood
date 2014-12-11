package scoreRater;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Connie Shi
 * @author Abhishek Sharma 
 * @author Justin Barash
 * @author Steven Yoon
 * J.P. Morgan Code for Good 2014 (Team 10)
 *
 */
public class SortMap {
	private TreeMap<String, Integer> map;

	/**
	 * @param result
	 * @return 
	 */
	public SortMap(TreeMap<String, Integer> result) {
		this.map = result;
		sort();
	}

	/**
	 * @return
	 */
	public TreeMap<String, Integer> sort() {
		ValueComparator bvc = new ValueComparator(this.map);
		TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(bvc);
		sortedMap.putAll(map);
		return (TreeMap<String,Integer>) sortedMap;
	}
}

/**
 * @author Connie Shi
 * @author Abhishek Sharma 
 * @author Justin Barash
 * @author Steven Yoon
 * J.P. Morgan Code for Good 2014 (Team 10)
 *
 */
class ValueComparator implements Comparator<String>{
	private Map<String, Integer> base;
	public ValueComparator(Map<String, Integer> base) {
		this.base = base;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(String a, String b) {
		if(base.get(a) >= base.get(b))
			return -1;
		else
			return 1;
	}
}
