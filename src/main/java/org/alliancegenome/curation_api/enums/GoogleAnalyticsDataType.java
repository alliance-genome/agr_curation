package org.alliancegenome.curation_api.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.quarkus.logging.Log;

public enum GoogleAnalyticsDataType {

	ALLIANCE(
		"alliance", 
		"352718941",
		List.of( "^\\/(gene)\\/(\\w+:(?:\\w)*\\d+)$", "^\\/(gene)\\/(ZFIN:ZDB-\\w+-\\d{6}-\\d+)$"),
		List.of( "^\\/(allele)\\/(\\w+:(?:\\w)*\\d+)$", "^\\/(allele)\\/(ZFIN:ZDB-\\w+-\\d{6}-\\d+)$")),
	MGI(
		"mgi", 
		"337076227", 
		List.of(".*(MGI:\\d+).*"),
		List.of(".*(MGI:\\d+).*")),
	RGD(
		"rgd",
		"352399653", 
		List.of("^\\/rgdweb\\/report\\/gene\\/main\\.html\\?id=(\\d+)$"),
		List.of("^\\/rgdweb\\/report\\/gene\\/main\\.html\\?id=(\\d+)$")),
	FB(
		"flybase", 
		"250855298", 
		List.of("^\\/reports/(FBgn[\\d]+)(?:\\.\\w+)?$"),
		List.of("^\\/reports/(FBal[\\d]+)(?:\\.\\w+)?$")),
	SGD(
		"sgd",
		"302498604", 
		List.of("^\\/locus\\/(S\\d{9})$"),
		List.of("^\\/allele\\/(S\\d{9})$")
	);

	private String name;
	private String ga4PropertyId;
	private List<String> geneFilters;
	private List<String> alleleFilters;
	private String doFilter = ".*(DOID:\\d+).*";
	private String goFilter = ".*(GO:\\d+).*";

	private GoogleAnalyticsDataType(String name, String ga4PropertyId, List<String> geneFilters, List<String> alleleFilters) {
		this.name = name;
		this.ga4PropertyId = ga4PropertyId;
		this.geneFilters = geneFilters;
		this.alleleFilters = alleleFilters;
	}

	public String getName() {
		return name;
	}
	public String getGa4PropertyId() {
		return ga4PropertyId;
	}
	public List<String> getFilters() {
		List<String> filters = new ArrayList<>();
		filters.addAll(geneFilters);
		filters.addAll(alleleFilters);
		filters.add(doFilter);
		filters.add(goFilter);
		return filters;
	}

	public void updateMap(String pagePathPlusQueryString, String metric, Map<String, Map<String, Double>> map) {

		for (String filter : this.getFilters()) {
			Pattern pattern = Pattern.compile(filter);
			Matcher matcher = pattern.matcher(pagePathPlusQueryString);
			String type = null;
			String ID = null;
		
			if (matcher.matches()) {
				if (pagePathPlusQueryString.contains("DOID:")) {
					ID = matcher.group(1);
					updateMasterMap(map, "disease_ontology", ID, metric);
					break;
				} else if (pagePathPlusQueryString.contains("GO:")) {
					ID = matcher.group(1);
					updateMasterMap(map, "gene_ontology", ID, metric);
					break;
				} else {
					switch (this.name) {
						case "alliance":
							type = matcher.group(1);
							ID = matcher.group(2);
							updateMasterMap(map, type, ID, metric);
							break;
		
						case "mgi":
							ID = matcher.group(1);
							updateMasterMap(map, List.of("gene", "allele"), ID, metric);
							break;
		
						case "rgd":
							ID = "RGD:" + matcher.group(1);
							updateMasterMap(map, List.of("gene", "allele"), ID, metric);
							break;
		
						case "flybase":
							ID = "FB:" + matcher.group(1);
							updateMasterMap(map, List.of("gene", "allele"), ID, metric);
							break;
		
						case "sgd":
							ID = "SGD:" + matcher.group(1);
							if (pagePathPlusQueryString.contains("/allele/")) {
								updateMasterMap(map, "allele", ID, metric);
							} else {
								updateMasterMap(map, "gene", ID, metric);
							}
							break;
		
						default:
							Log.info("No matching pattern found for: " + pagePathPlusQueryString);
					}
					break;
				}
			}
		}
	}

	private void updateMasterMap(Map<String, Map<String, Double>> map, String type, String id, String metric) {
		updateMasterMap(map, List.of(type), id, metric);
	}

	private void updateMasterMap(Map<String, Map<String, Double>> map, List<String> types, String id, String metric) {
		for (String type : types) {
			if (!map.containsKey(type)) {
				map.put(type, new HashMap<>());
			}
			Map<String, Double> typeMap = map.get(type);
			if (!typeMap.containsKey(id)) {
				typeMap.put(id, Double.parseDouble(metric));
			} else {
				typeMap.put(id, typeMap.get(id) + Double.parseDouble(metric));
			}
		}
	}
	
}