package org.alliancegenome.curation_api.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import io.quarkus.logging.Log;

public class EsClientFactory {

	public static RestHighLevelClient createClient(String esHosts, String esProtocol) {

		List<HttpHost> hostsList = new ArrayList<>();
		
		String array[] = esHosts.split(":");
		if(esProtocol == null || esProtocol.length() == 0) {
			esProtocol = "http";
		}
		
		if(array.length == 2) {
			hostsList.add(new HttpHost(array[0], Integer.parseInt(array[1]), esProtocol));
		} else {
			hostsList.add(new HttpHost(array[0], 9200, esProtocol));
		}
		
		Log.info("Adding Search Host: " + esHosts);

		HttpHost[] hosts = new HttpHost[hostsList.size()];
		hosts = hostsList.toArray(hosts);

		int hours = 2 * (60 * 60 * 1000);
		return new RestHighLevelClient(
				RestClient.builder(hosts)
						//.setRequestConfigCallback(
						//		// Timeout after 60 * 60 * 1000 milliseconds = 1 hour
						//		// Needed for long running snapshots
						//		requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(10000).setConnectionRequestTimeout(10000)
						//)
		);

	}


}