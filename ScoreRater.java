package scoreRater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * @author Connie Shi
 * @author Abhishek Sharma 
 * @author Justin Barash
 * @author Steven Yoon
 * J.P. Morgan Code for Good 2014 (Team 10)
 *
 */
public class ScoreRater {
	private static ArrayList<String> nonKeyWords = new ArrayList<String>();
	private static int totalMessage = 0;
	private static double totalPrompt = 0.0;


	private static BufferedReader readerForPrompt, readerForMessage;
	private static String line, title, message, prompt;

	public static void main(String[] args) {

		try {			
			if (args.length == 2) {
				File promptFile = new File(args[0]);
				File messageFile = new File(args[1]);

				readerForPrompt = new BufferedReader(new FileReader(promptFile));
				readerForMessage = new BufferedReader(new FileReader(messageFile));
			}
			else {
				System.out.println("java -jar ScoreRater.jar [prompt filename] [message filename]");
				System.exit(1);
			}			

			populatenonKeyWords();
			ArrayList<String> titleList = readMessages();
			int grade = manager(prompt, message, titleList);
			DAO.seeSQL(grade);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static ArrayList<String> readMessages() throws IOException {
		int i = 0;

		while ((line = readerForPrompt.readLine()) != null) {
			if (i == 1) {
				title = line;
			}
			prompt = prompt + line;
			i++;
		}
		line = null;
		while ((line = readerForMessage.readLine()) != null) 
			message = message + line;

		title = parseString(title);
		String[] titleArray = title.split("\\s+");
		ArrayList<String> titleList = new ArrayList<String>();
		for(String a: titleArray){
			titleList.add(a);
		}
		return titleList;
	}


	/**
	 * @param promp
	 * @param messag
	 * @param titleL
	 * @return
	 */
	private static int manager(String p, String m, ArrayList<String> titleList) {
		int score = 0;
		String[] length = m.split("\\s+");

		//If message is less than a certain length, give it a score of 1
		if (length.length < 60) {
			return 1;
		} //else parse it 
		else {
			String prompt = parseString(p);
			String message = parseString(m);
			HashMap<String, Integer> hashP = countStrings(prompt);
			HashMap<String, Integer> hashM = countStrings(message);
			totalPrompt = (double) hashM.size();
			ArrayList<Word> promptR = findKeyWords(hashP);
			ArrayList<Word> messageR = findKeyWords(hashM);
			TreeMap<String, Integer> promptS = rankW(promptR, 0);
			TreeMap<String, Integer> messageS = rankW(messageR, 1);

			score = scorer(promptS, messageS, titleList, length.length);
			System.out.println("\nScore: " + score +"\n");
		}
		return score;
	}

	/**
	 * @param s
	 * @return
	 */
	public static int countWords(String s){
		int wordCount = 0;
		boolean word = false;
		int endOfLine = s.length() - 1;
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
				word = true;
			} else if (!Character.isLetter(s.charAt(i)) && word) {
				wordCount++;
				word = false;
			} else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
				wordCount++;
			}
		}
		return wordCount;
	}

	/**
	 * @param promptS
	 * @param messageS
	 * @param titleL
	 * @param l
	 * @return
	 */
	private static int scorer(TreeMap<String, Integer> promptS, TreeMap<String, Integer> messageS, ArrayList<String> titleL, int len) {
		int psize = promptS.keySet().size();
		Double mscore = 0.0;
		ArrayList<String> t1 = new ArrayList<String>();

		Iterator<Entry<String, Integer>> it1 = promptS.entrySet().iterator();
		while (it1.hasNext() && psize >=0) {
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it1.next();
			t1.add((String)pairs.getKey());
			psize--;
		}

		Iterator<Entry<String, Integer>> it = messageS.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
			if(t1.contains((String) pairs.getKey())){

				Double add = pairs.getValue()/((totalPrompt)/1.5);
				if(titleL.contains((String) pairs.getKey())){
					add = add * 20;
				}
				mscore = mscore + add;

			}
		}
		
		//Normalize the grade
		if(len > 200 && len <= 300){
			mscore = mscore +.05;
		} else if(len > 300 && len <= 400){
			mscore = mscore +.075;
		} else if(len > 400 && len <= 500){
			mscore = mscore + .15;
		} else if(len >500 && len <= 600){
			mscore = mscore +.2;
		} else if(len >= 600 ){
			mscore = mscore +.3;
		}

		if(mscore >= 0 && mscore <= .2){
			return 1;
		} else if(mscore>= .2 && mscore <= .4){
			return 2; 
		} else if(mscore>= .4 && mscore <= .6){
			return 3;
		} else if(mscore>= .6 && mscore <= .8){
			return 4;
		} else if(mscore>= .8 ){
			return 5;
		}

		return 1;
	}


	/**
	 * @param promptR
	 * @param i
	 * @return
	 */
	private static TreeMap<String, Integer> rankW(ArrayList<Word> promptR, int i) {
		// if word is already in set, see if new value is higher and if so change
		TreeMap<String, Integer> result = new TreeMap<String, Integer>();
		for(Word a: promptR){
			result.put(a.word, a.count);
			if(i == 0){
				totalMessage =totalMessage+ a.count;
			} else {
				totalPrompt = totalPrompt + a.count;
			}
			for(String b : a.syn.keySet()){
				result.put(b, a.syn.get(b));
				if(i == 0){
					totalMessage =totalMessage+ a.syn.get(b);
				} else {
					totalPrompt = totalPrompt + a.syn.get(b);
				}
			}
		}
		
		SortMap order = new SortMap(result);
		result = order.sort();

		return result;
	}

	/**
	 * @param hash
	 * @return
	 */
	private static ArrayList<Word> findKeyWords(HashMap<String, Integer> hash) {
		ArrayList<Word> result = new ArrayList<Word>();
		int count = 0;
		Map<String, Integer> synonyms = new HashMap<String, Integer>();
		ArrayList<String> synonym = new ArrayList<String>();
		for(String a: hash.keySet()){
			count = hash.get(a);
			synonym = getSynonym(a);
			if(synonym != null){
				for(String b: synonym) {
					if (!hash.containsKey(b)) {
						synonyms.put(b, count);
					}
				}
			}
			result.add(new Word(a, count, synonyms));
		}

		return result;
	}

	/**
	 * @param a
	 * @return
	 */
	private static ArrayList<String> getSynonym(String a) {
		ArrayList<String> result = new ArrayList<String>();
		result.add("test");
		return result;
	}


	/**
	 * @param original
	 * @return
	 */
	public static String parseString(String original) {
		original = original.toLowerCase();
		String ns = "";
		for(int i =0; i< original.length(); i++) {
			Character ch = original.charAt(i);
			if (Character.isLetter(ch))
				ns+=ch;
			else { 
				ns+=" ";
			}
		}
		return ns;
	}

	/**
	 * @param parsed
	 * @return
	 */
	public static HashMap<String, Integer> countStrings(String parsed){
		StringTokenizer st = new StringTokenizer(parsed);
		HashMap<String, Integer> hash = new HashMap<String, Integer>();
		while (st.hasMoreTokens()){
			String nextWord = st.nextToken();
			if (nonKeyWords.contains(nextWord))
				continue;
			else if (hash.containsKey(nextWord)) {
				hash.put(nextWord, hash.get(nextWord)+1 );
			}
			else {
				hash.put(nextWord, 1);
			}
		}
		return hash;
	}
	
	/**
	 * List of words that are not key word -
	 * Found the most used words in the English language
	 * Hardcoded for the sake of time
	 */
	public static void populatenonKeyWords() {
		nonKeyWords.add("and");
		nonKeyWords.add("or");
		nonKeyWords.add("this");
		nonKeyWords.add("is");
		nonKeyWords.add("a");
		nonKeyWords.add("the");
		nonKeyWords.add("you");
		nonKeyWords.add("your");
		nonKeyWords.add("i");
		nonKeyWords.add("they");
		nonKeyWords.add("it");
		nonKeyWords.add("im");
		nonKeyWords.add("that");
		nonKeyWords.add("in");
		nonKeyWords.add("to");
		nonKeyWords.add("was");
		nonKeyWords.add("of");
		nonKeyWords.add("me");
		nonKeyWords.add("but");
		nonKeyWords.add("are");
		nonKeyWords.add("an");
		nonKeyWords.add("so");
		nonKeyWords.add("when");
		nonKeyWords.add("about");
		nonKeyWords.add("do");
		nonKeyWords.add("my");
		nonKeyWords.add("t");
		nonKeyWords.add("on");
		nonKeyWords.add("if");
		nonKeyWords.add("at");
		nonKeyWords.add("had");
		nonKeyWords.add("not");
		nonKeyWords.add("have");
		nonKeyWords.add("would");
		nonKeyWords.add("with");
		nonKeyWords.add("then");
		nonKeyWords.add("them");
		nonKeyWords.add("s");
		nonKeyWords.add("has");
		nonKeyWords.add("for");
		nonKeyWords.add("be");
		nonKeyWords.add("as");
		nonKeyWords.add("get");
		nonKeyWords.add("out");
		nonKeyWords.add("some");
		nonKeyWords.add("someone");
		nonKeyWords.add("why");
		nonKeyWords.add("don");
	}
}