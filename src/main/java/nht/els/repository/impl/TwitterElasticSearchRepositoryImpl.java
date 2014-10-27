package nht.els.repository.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import nht.els.repository.TwitterElasticSearchRepository;
import nht.els.service.SentimentService;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class TwitterElasticSearchRepositoryImpl implements TwitterElasticSearchRepository, InitializingBean {

	@Value("${twitter.elasticsearch.cluster.host}")
	private String searchingHost;
	@Value("${twitter.elasticsearch.cluster.port}")
	private int searchingPort;
	@Value("${twitter.elasticsearch.cluster.name}")
	private String clusterName;
	@Value("${twitter.elasticsearch.cluster.index}")
	private String index;

	@Value("${twiter.elasticsearch.cluster.type}")
	private String type;
	Client client;

	private SentimentService sentimentSv;

	//
	public void afterPropertiesSet() throws Exception {
		Settings setting = ImmutableSettings.settingsBuilder().put(Collections.singletonMap("cluster.name", clusterName)).build();
		client = new TransportClient(setting).addTransportAddress(new InetSocketTransportAddress(searchingHost, searchingPort));
		//
	}

	public void updateTwitterSentiment(String id, float sentiment) {
		client.prepareUpdate(index, type, id).setScript("ctx._source.sentiment=" + sentiment + ",ctx._source.finish=" + true).execute().actionGet();

	}

	public void insertTwitterSentiment(String msg) throws ElasticsearchException, IOException {
		client.prepareIndex(index, type).setSource(XContentFactory.jsonBuilder().startObject().field("msg", msg).field("sentiment", 0).field("finish", false).endObject()).execute().actionGet();
	}

	
	public boolean bulkUpdateTwitterSentiment(Map<String,Float> objects) throws ElasticsearchException, IOException {
		if (objects != null) {
			
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for(String id: objects.keySet()){
				float sentiment =objects.get(id);
				bulkRequest.add(client.prepareUpdate(index, type, id).setScript("ctx._source.sentiment=" + sentiment + ",ctx._source.finish=" + true));

			}
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {
				return false;
			}
			return true;
		}
		return true;
	}
	public boolean bulkTwitterSentiment(List<String> msgs) throws ElasticsearchException, IOException {
		if (msgs != null) {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for (String msg : msgs) {

				bulkRequest.add(client.prepareIndex(index, type).setSource(XContentFactory.jsonBuilder().startObject().field("msg", msg).field("sentiment", 0).field("finish", false).endObject()));

			}
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {
				return false;
			}
			return true;
		}
		return true;
	}

	public SearchHit[] findUnFinishTwitterMsg(int size) throws InterruptedException, ExecutionException {
		SearchRequestBuilder searchBuilder = client.prepareSearch(index).setTypes(type);
		TermFilterBuilder filter = FilterBuilders.termFilter("finish", true);
		FilteredQueryBuilder queryBuilder = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.notFilter(filter));
		SearchHit[] hits = searchBuilder.setQuery(queryBuilder).setFrom(0).setSize(size).execute().get().getHits().getHits();
		return hits;
		// return
		// searchBuilder.setQuery(queryBuilder).setFrom(0).setSize(size).execute().get().getHits().iterator();
	}

	public void startPoint() {		
		/*
		 * TwitterElasticSearchRepositoryImpl repository = new
		 * TwitterElasticSearchRepositoryImpl(); repository.searchingHost =
		 * "ec2-54-191-180-182.us-west-2.compute.amazonaws.com";
		 * repository.searchingPort = 9300; repository.clusterName =
		 * "taih-cluster"; repository.index = "my_twitter_river";
		 * repository.type = "status";
		 */
		while (true) {
			try {
				this.afterPropertiesSet();
				SearchHit[] findUnFinishTwitterMsg = this.findUnFinishTwitterMsg(1000);
				System.out.println(findUnFinishTwitterMsg.length);
				Map<String, Float> sentimetMap = new HashMap<String, Float>();
				for (SearchHit type : findUnFinishTwitterMsg) {
					JSONObject tmp = new JSONObject(type.getSourceAsString());
					String text = tmp.getString("text");
					sentimetMap.put(type.getId(), getSentimentSv().getSentiment(text));
				}
				this.bulkUpdateTwitterSentiment(sentimetMap);
				Thread.sleep(300);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}

	public SentimentService getSentimentSv() {
		return sentimentSv;
	}

	public void setSentimentSv(SentimentService sentimentSv) {
		this.sentimentSv = sentimentSv;
	}

	

}
