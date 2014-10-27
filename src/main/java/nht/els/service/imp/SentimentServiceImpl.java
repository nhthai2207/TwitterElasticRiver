package nht.els.service.imp;

import java.util.HashMap;
import java.util.Map;

import nht.els.service.SentimentService;
import nht.els.utils.SentimentUtils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class SentimentServiceImpl implements SentimentService, InitializingBean{
	private Resource sentislangResource;
	private Resource sentislangWordsResource;
	private Map<String,Float> sentimentHash = new HashMap<String, Float>();
	public float getSentiment(String text) {
		return SentimentUtils.getSentiment(text, this.sentimentHash);
	}
	
	
	/**
	 * @return the sentislangResource
	 */
	public Resource getSentislangResource() {
		return sentislangResource;
	}

	/**
	 * @param sentislangResource the sentislangResource to set
	 */
	public void setSentislangResource(Resource sentislangResource) {
		this.sentislangResource = sentislangResource;
	}

	/**
	 * @return the sentislangWordsResource
	 */
	public Resource getSentislangWordsResource() {
		return sentislangWordsResource;
	}

	/**
	 * @param sentislangWordsResource the sentislangWordsResource to set
	 */
	public void setSentislangWordsResource(Resource sentislangWordsResource) {
		this.sentislangWordsResource = sentislangWordsResource;
	}

	public void afterPropertiesSet() throws Exception {
		sentimentHash.putAll(SentimentUtils.readSentimentFile(sentislangResource.getFile()));
		sentimentHash.putAll(SentimentUtils.readSentimentFile(sentislangWordsResource.getFile()));
		
	}
}
