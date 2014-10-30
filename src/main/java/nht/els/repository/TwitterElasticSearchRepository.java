package nht.els.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.search.SearchHit;

public interface TwitterElasticSearchRepository {

	public void updateTwitterSentiment(String id, float sentiment);

	public void insertTwitterSentiment(String msg) throws ElasticsearchException, IOException;

	public boolean bulkTwitterSentiment(List<String> msgs) throws ElasticsearchException, IOException;

	public SearchHit[] findUnFinishTwitterMsg(int size) throws InterruptedException, ExecutionException;
	
	//public void startPoint();

	boolean bulkUpdateTwitterSentiment(Map<String, Float> objects)
			throws ElasticsearchException, IOException;
}
