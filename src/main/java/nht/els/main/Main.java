package nht.els.main;

import nht.els.service.TwitterSentimentService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"springConfig.xml");
		 TwitterSentimentService twitterSentimentService = context.getBean(TwitterSentimentService.class);
		 twitterSentimentService.updateUnFinishTwitter();
		 //System.out.println(sentimentService.getSentiment("My life so is very wonderful"));
		 
	}

}
