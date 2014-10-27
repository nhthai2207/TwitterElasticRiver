package nht.els.service;

public interface SentimentService {
	/**
	 * get sentiment of text
	 * @param text: text to get
	 * @return total sentiment 
	 */
	public float getSentiment(String text);
}
