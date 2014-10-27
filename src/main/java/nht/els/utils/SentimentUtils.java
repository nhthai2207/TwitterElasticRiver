package nht.els.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SentimentUtils {
	/**
	 * get Sentiment of text
	 * @author thai.nh
	 * @param text : text to get sentiment
	 * @param sentimentsHash : sentiment {@link Map}
	 * @return sentiment total of text
	 */
	public static float getSentiment(String text, Map<String,Float> sentimentsHash){
		float sentiment_total = 0;
		if(text == null || "".equals(text)) return 0;
		String[] tokens = text.split(" ");
		for(int i = 0; i< tokens.length;i++){
			Float sentiment = sentimentsHash.get(tokens[i]);
			if(sentiment != null){
				sentiment_total += sentiment;
			}
		}
		return sentiment_total;
	}
	/**
	 * load the specified sentiment file into a hash
	 * @author thai.nh
	 * @param file: {@link File} to load
	 * @return {@link Map}  -- hash with data loaded 
	 */
	 
	public static Map<String,Float> readSentimentFile(File file){
		Map<String,Float> map = new HashMap<String, Float>();
		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while( (line = br.readLine()) != null){
				String[] strs = line.split("\t");
				if(strs.length == 2){
					String sentiScore = strs[0];
					String text = strs[1];
					map.put(text, Float.parseFloat(sentiScore));
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
		return map;
	}
}
