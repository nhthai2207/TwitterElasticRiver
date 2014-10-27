package nht.els.main;

import nht.els.repository.TwitterElasticSearchRepository;
import nht.els.service.SentimentService;

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
		 SentimentService sentimentService = context.getBean(SentimentService.class);
		 TwitterElasticSearchRepository twitterService = context.getBean(TwitterElasticSearchRepository.class);
		 twitterService.startPoint();
		 //System.out.println(sentimentService.getSentiment("My life so is very wonderful"));
		 
	}

}
