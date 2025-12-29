package org.alliancegenome.curation_api.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.enums.GoogleAnalyticsDataType;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyGeneratedService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.Filter;
import com.google.analytics.data.v1beta.Filter.StringFilter.MatchType;
import com.google.analytics.data.v1beta.FilterExpression;
import com.google.analytics.data.v1beta.FilterExpressionList;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.OrderBy;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.nilosplace.process_display.ProcessDisplayHelper;
import net.nilosplace.process_display.util.ObjectFileStorage;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@ApplicationScoped
public class GoogleAnalyticsService {

	@Inject
	GeneToGeneOrthologyGeneratedService geneToGeneOrthologyGeneratedService;

	GoogleCredentials credentials;
	ObjectFileStorage<Map<String, Map<String, Double>>> objectFileStorage = new ObjectFileStorage<>();

	@PostConstruct
	protected void init() {

		try {
			
			DefaultCredentialsProvider profile = DefaultCredentialsProvider.builder().build();

			SecretsManagerClient secretsClient = SecretsManagerClient.builder()
					.credentialsProvider(profile)
					.region(Region.US_EAST_1).build();

			GetSecretValueRequest valueRequest = GetSecretValueRequest.builder().secretId("google_analytics_secret_key").build();

			GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);
			
			String googleAnalyticsSecretKey = valueResponse.secretString();

			secretsClient.close();

			credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(googleAnalyticsSecretKey.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, Map<String, Double>> getDataMap() {

		Map<String, Map<String, Double>> map = new HashMap<>();
		List<Pair<String, String>> allianceMap = generateAnalytics(GoogleAnalyticsDataType.ALLIANCE);
		GoogleAnalyticsDataType.ALLIANCE.updateMap(allianceMap, map);
		List<Pair<String, String>> mgiMap = generateAnalytics(GoogleAnalyticsDataType.MGI);
		GoogleAnalyticsDataType.MGI.updateMap(mgiMap, map);
		List<Pair<String, String>> fbMap = generateAnalytics(GoogleAnalyticsDataType.FB);
		GoogleAnalyticsDataType.FB.updateMap(fbMap, map);
		List<Pair<String, String>> rgdMap = generateAnalytics(GoogleAnalyticsDataType.RGD);
		GoogleAnalyticsDataType.RGD.updateMap(rgdMap, map);
		List<Pair<String, String>> sgdMap = generateAnalytics(GoogleAnalyticsDataType.SGD);
		GoogleAnalyticsDataType.SGD.updateMap(sgdMap, map);

		HashMap<String, Double> humanOrthoPopMap = generateHumanOrthologMap(map);
		for (String humanGeneId : humanOrthoPopMap.keySet()) {
			map.get("gene").put(humanGeneId, humanOrthoPopMap.get(humanGeneId));
		}
		return map;
	}

	public List<Pair<String, String>> generateAnalytics(GoogleAnalyticsDataType site) {
		
		List<Pair<String, String>> ret = new ArrayList<>();
		ObjectFileStorage<List<Pair<String, String>>> objectStorage = new ObjectFileStorage<>();

		if (StringUtils.isEmpty(site.getGa4PropertyId())) {
			return ret;
		}

		try {
			File cacheFile = new File(site.getName() + "analyticsCache.data");
			if (cacheFile.exists()) {
				Log.info("Reading cache from " + cacheFile);
				return objectStorage.readObjectFromFile(cacheFile);
			}

			BetaAnalyticsDataSettings settings =
			BetaAnalyticsDataSettings.newBuilder()
				.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
				.build();
			BetaAnalyticsDataClient analyticsData = BetaAnalyticsDataClient.create(settings);
			FilterExpressionList.Builder filterExpressionList = FilterExpressionList.newBuilder();

			for (String filterString : site.getFilters()) {
				FilterExpression filterExpression = FilterExpression.newBuilder()
					.setFilter(Filter.newBuilder()
						.setFieldName("pagePathPlusQueryString")
						.setStringFilter(
							Filter.StringFilter.newBuilder()
								.setMatchType(MatchType.FULL_REGEXP)
								.setValue(filterString)
						)
					)
					.build();
				filterExpressionList.addExpressions(filterExpression);
			}
				
			FilterExpression andFilter = FilterExpression.newBuilder().setOrGroup(filterExpressionList).build();
			Long offSet = 0L;
			Long limit = 10000L;
			boolean moreRows = true;
			LocalDate currentDate = LocalDate.now();

			Log.info("Pulling GA Data for " + site.getName());

			ProcessDisplayHelper ph = new ProcessDisplayHelper();
			ph.startProcess("Pulling GA Data for " + site.getName());
			while (moreRows) {
				RunReportRequest request = RunReportRequest.newBuilder()
				.setProperty("properties/" + site.getGa4PropertyId())
				.addMetrics(Metric.newBuilder().setName("screenPageViews"))
				.addDimensions(Dimension.newBuilder().setName("pagePathPlusQueryString"))
				.addDateRanges(DateRange.newBuilder().setStartDate(currentDate.minusYears(1).toString()).setEndDate(currentDate.toString()))
				.setDimensionFilter(andFilter)
				.addOrderBys(
					OrderBy.newBuilder()
						.setMetric(OrderBy.MetricOrderBy.newBuilder().setMetricName("screenPageViews"))
						.setDesc(true))
				.setOffset(offSet)
				.setLimit(limit)
				.build();

				RunReportResponse response = analyticsData.runReport(request);
				for (Row row : response.getRowsList()) {
					String dimension = row.getDimensionValues(0).getValue();
					String metric = row.getMetricValues(0).getValue();
					Pair<String, String> entry = Pair.of(dimension, metric);
					ret.add(entry);
					ph.progressProcess();
				}
				if (response.getRowsCount() < limit) {
					moreRows = false;
				} else {
					offSet += limit;
				}
			}
			ph.finishProcess();

			Log.info("Writing cache into " + cacheFile);
			objectStorage.writeObjectToFile(ret, cacheFile);
			Log.info("Finished Pulling GA Data for " + site.getName());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private HashMap<String, Double> generateHumanOrthologMap(Map<String, Map<String, Double>> map) {
		
		HashMap<String, Double> humanOrthoPopMap = new HashMap<>();
		ObjectFileStorage<HashMap<String, Double>> objectStorage = new ObjectFileStorage<>();

		try {
			File cacheFile = new File("humanOrthoPopMap.data");
			if (cacheFile.exists()) {
				Log.info("Reading cache from " + cacheFile);
				return objectStorage.readObjectFromFile(cacheFile);
			}
			ProcessDisplayHelper ph = new ProcessDisplayHelper();
			ph.startProcess("Pulling Orthologs for Human genes", map.get("gene").size());
			HashMap<String, List<String>> humanOrthoMap = new HashMap<>();
			for (String geneId : map.get("gene").keySet()) {
				HashMap<String, Object> params = new HashMap<>();
				params.put("subjectGene.primaryExternalId", geneId);
				params.put("subjectGene.taxon.curie", "NCBITaxon:9606");
				params.put("isBestScore.name", "Yes");
				SearchResponse<GeneToGeneOrthologyGenerated> resp = geneToGeneOrthologyGeneratedService.findByParams(null, params);
				if (resp != null && resp.getResults().size() > 0) {
					List<String> orthologs = humanOrthoMap.get(geneId);
					if (orthologs == null) {
						orthologs = new ArrayList<>();
						humanOrthoMap.put(geneId, orthologs);
					}
					for (GeneToGeneOrthologyGenerated ortho : resp.getResults()) {
						orthologs.add(ortho.getObjectGene().getPrimaryExternalId());
					}
				}
				ph.progressProcess();
			}
			ph.finishProcess();

			
			for (Map.Entry<String, List<String>> entry : humanOrthoMap.entrySet()) {
				String humanGeneId = entry.getKey();
				List<String> orthologs = entry.getValue();
				Double pop = map.get("gene").get(humanGeneId);
				if (orthologs != null && orthologs.size() > 0) {
					for (String ortholog : orthologs) {
						pop += map.get("gene").containsKey(ortholog) ? map.get("gene").get(ortholog) * 0.1 : 0.0;
					}
				}
				humanOrthoPopMap.put(humanGeneId, pop);
			}
			Log.info("Writing cache into " + cacheFile);
			objectStorage.writeObjectToFile(humanOrthoPopMap, cacheFile);
			Log.info("Finished Generating human ortho pop map");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return humanOrthoPopMap;
	}
	
}