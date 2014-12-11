package scoreRater;

import java.util.Map;

/**
 * @author Connie Shi
 * @author Abhishek Sharma 
 * @author Justin Barash
 * @author Steven Yoon
 * J.P. Morgan Code for Good 2014 (Team 10)
 *
 */
public class Word {
	String word;
	int count;
	Map<String, Integer> syn;
	
	/**
	 * @param word
	 * @param count
	 * @param syn
	 */
	public Word(String word, int count, Map<String, Integer> syn) {
		this.word = word;
		this.count = count;
		this.syn = syn;
	}	
}
