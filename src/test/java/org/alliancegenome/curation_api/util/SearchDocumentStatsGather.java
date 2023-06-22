package org.alliancegenome.curation_api.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class SearchDocumentStatsGather {

	HashMap<String, Integer> sizeMap = new HashMap<String, Integer>();
	
	public static void main(String[] args) throws Exception {
		new SearchDocumentStatsGather();
	}
	
	public SearchDocumentStatsGather() throws Exception {
		
		//RestHighLevelClient client = createClient("olin-dev.alliancegenome.org");
		RestHighLevelClient client = createClient("alpha.cluster01.alliancegenome.org");
		

		//final Scroll scroll = new Scroll();
		SearchRequest searchRequest = new SearchRequest("allele-000001");
		
		//searchRequest.scroll(scroll);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		searchSourceBuilder.query();
		searchSourceBuilder.size(1000);
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueSeconds(60));
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT); 
		String scrollId = searchResponse.getScrollId();

		SearchHit[] searchHits = searchResponse.getHits().getHits();
		
		int c = searchHits.length;
		
		while (searchHits != null && searchHits.length > 0) {
			
			for(SearchHit hit: searchHits) {
				processHit("", hit.getSourceAsMap());
			}
			
			System.out.println(c + " -> " + sizeMap);

			SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
			scrollRequest.scroll(TimeValue.timeValueSeconds(60));
			searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
			scrollId = searchResponse.getScrollId();
			c += searchHits.length;
			searchHits = searchResponse.getHits().getHits();
			//System.out.println(searchHits);
		}
		
		System.out.println(c + " -> " + sizeMap);

		ClearScrollRequest clearScrollRequest = new ClearScrollRequest(); 
		clearScrollRequest.addScrollId(scrollId);
		ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
		boolean succeeded = clearScrollResponse.isSucceeded();

		client.close();
	}


	private void processHit(String path, Map<String, Object> map) {

		for(String key: map.keySet()) {
			Object value = map.get(key);
			int size = String.valueOf(value).length();
			
			//if(value != null) System.out.println(value.getClass());
			
			String newKey = path + "." + key;
			
			if(value != null && value.getClass() == HashMap.class) {
				processHit(newKey, (Map<String, Object>)value);
			}
			
			//System.out.println(value.getClass());
			//System.out.println("Size: " + size + " Key: " + key + " -> " + value);
			
			if(sizeMap.containsKey(newKey)) {
				int currentSize = sizeMap.get(newKey);
				sizeMap.put(newKey, currentSize + size);
			} else {
				sizeMap.put(newKey, size);
			}

		}

	}

	// Used if APP needs to have multiple clients
	private RestHighLevelClient createClient(String hostName) {

		List<HttpHost> esHosts = new ArrayList<>();
		esHosts.add(new HttpHost(hostName, 9200));
		HttpHost[] hosts = new HttpHost[esHosts.size()];
		hosts = esHosts.toArray(hosts);

		int hours = 2 * (60 * 60 * 1000);
		RestHighLevelClient client = new RestHighLevelClient(
			RestClient.builder(hosts)
			.setRequestConfigCallback(
				// Timeout after 60 * 60 * 1000 milliseconds = 1 hour
				// Needed for long running snapshots
				requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(hours).setConnectionRequestTimeout(hours)
				)
			);
		return client;
	}

}
