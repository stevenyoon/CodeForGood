package scoreRater;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * @author Connie Shi
 * @author Abhishek Sharma 
 * @author Justin Barash
 * @author Steven Yoon
 * J.P. Morgan Code for Good 2014 (Team 10)
 *
 */
public class Thesaurus {

	/**
	 * @param word
	 * @return
	 */
	public static ArrayList<String> getSynonyms(String word) {
		return new SendRequest().SendRequestYo(word, "en_US",
				"7xUHrp9SM8XNLhuOamGw", "json");
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
class SendRequest {
	final String endpoint = "http://thesaurus.altervista.org/thesaurus/v1";

	public SendRequest() {}

	/**
	 * @param word
	 * @param language
	 * @param key
	 * @param output
	 * @return
	 */
	public ArrayList<String> SendRequestYo(String word, String language, String key, String output) {
		try {
			ArrayList<String> returnSyns = new ArrayList<String>();
			URL serverAddress = new URL(endpoint + "?word="
					+ URLEncoder.encode(word, "UTF-8") + "&language="
					+ language + "&key=" + key + "&output=" + output);

			HttpURLConnection connection = (HttpURLConnection) serverAddress.openConnection();
			connection.connect();
			int rc = connection.getResponseCode();

			if (rc == 200) {
				String line = null;
				BufferedReader br = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();

				while ((line = br.readLine()) != null)
					sb.append(line + '\n');

				JSONObject obj = (JSONObject) JSONValue.parse(sb.toString());
				JSONArray array = (JSONArray) obj.get("response");

				for (int i = 0; i < array.size(); i++) {
					JSONObject list = (JSONObject) ((JSONObject) array.get(i)).get("list");
					String syn = (String) list.get("synonyms");
					String[] syns = syn.split("\\|");
					for (int k = 0; k < syns.length; k++) {
						if (!syns[k].contains("antonym")) {
							returnSyns.add(syns[k]);
						}
					}
				}
				return returnSyns;
			} 
			else
				System.out.println("HTTP error:" + rc);
			connection.disconnect();
		}
		catch (java.net.MalformedURLException e) {
			e.printStackTrace();
		} 
		catch (java.net.ProtocolException e) {
			e.printStackTrace();
		} 
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
