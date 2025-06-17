package org.alliancegenome.curation_api.services;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.nilosplace.process_display.util.ObjectFileStorage;

import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.Filter;
import com.google.analytics.data.v1beta.Filter.StringFilter.MatchType;
import com.google.analytics.data.v1beta.FilterExpression;
import com.google.analytics.data.v1beta.FilterExpressionList;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.OrderBy;
import com.google.analytics.data.v1beta.Row;

import org.alliancegenome.curation_api.enums.GoogleAnalyticsDataType;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyGeneratedService;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class GoogleAnalyticsService {

	@Inject
	GeneToGeneOrthologyGeneratedService geneToGeneOrthologyGeneratedService;

	private static final String KEY_FILE_LOCATION = "/Users/vrgollapally/Desktop/analytics_secrets.json";
	GoogleCredentials credentials;
	ObjectFileStorage<Map<String, Map<String, Double>>> objectFileStorage = new ObjectFileStorage<>();

	@PostConstruct
	protected void init() {
		try {
			credentials = GoogleCredentials.fromStream(new FileInputStream(KEY_FILE_LOCATION));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, Map<String, Double>> getDataMap() {

		Map<String, Map<String, Double>> map = new HashMap<>();
		generateAnalytics(map, GoogleAnalyticsDataType.ALLIANCE);
		generateAnalytics(map, GoogleAnalyticsDataType.MGI);
		generateAnalytics(map, GoogleAnalyticsDataType.FB);
		generateAnalytics(map, GoogleAnalyticsDataType.RGD);
		generateAnalytics(map, GoogleAnalyticsDataType.SGD);

		generateOrthologPop(map);
		return map;
	}

	public void generateAnalytics(Map<String, Map<String, Double>> map, GoogleAnalyticsDataType site) {
		
		if (StringUtils.isEmpty(site.getGa4PropertyId())) {
			return;
		}

		try {
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
					site.updateMap(dimension, metric, map);
				}
				if (response.getRowsCount() < limit) {
					moreRows = false;
				} else {
					offSet += limit;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateOrthologPop(Map<String, Map<String, Double>> map) {

		try {

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
			}

			HashMap<String, Double> humanOrthoPopMap = new HashMap<>();
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

			for (String humanGeneId : humanOrthoPopMap.keySet()) {
				map.get("gene").put(humanGeneId, humanOrthoPopMap.get(humanGeneId));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}