package nht.els.service.imp;

import java.util.HashMap;
import java.util.Map;

import nht.els.repository.TwitterElasticSearchRepository;
import nht.els.service.SentimentService;
import nht.els.service.TwitterSentimentService;

import org.elasticsearch.search.SearchHit;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwitterSentimentServiceImpl implements TwitterSentimentService{
	@Autowired
	TwitterElasticSearchRepository twitterElasticSearchRepository;
	@Autowired
	SentimentService sentimentService;
	
	public void updateUnFinishTwitter() {
		long begin = System.currentTimeMillis();
		
		try {
			
			SearchHit[] findUnFinishTwitterMsg = twitterElasticSearchRepository.findUnFinishTwitterMsg(10);
			System.out.println(findUnFinishTwitterMsg.length);
			Map<String, Float> sentimetMap = new HashMap<String, Float>();
			for (SearchHit type : findUnFinishTwitterMsg) {
				JSONObject tmp = new JSONObject(type.getSourceAsString());
				String text = tmp.getString("text");				
				sentimetMap.put(type.getId(), sentimentService.getSentiment(text));
			}
			twitterElasticSearchRepository.bulkUpdateTwitterSentiment(sentimetMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(System.currentTimeMillis() - begin);
		
	}

}
