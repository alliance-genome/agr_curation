package org.alliancegenome.curation_api.base.dao;

import java.util.*;

import javax.inject.Inject;

import org.alliancegenome.curation_api.base.document.BaseDocument;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.*;

import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.json.JsonObject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class BaseESDAO<E extends BaseDocument> extends BaseDocumentDAO<E> {

    @Inject protected RestHighLevelClient restHighLevelClient;
    @Inject protected MeterRegistry registry;

    protected BaseESDAO(Class<E> myClass, String index) {
        super(myClass, index);
        log.debug("RestHighLevelClient: " + restHighLevelClient);
        log.debug("MeterRegistry: " + registry);
    }

    public SearchResponse<E> searchByParams(Pagination pagination, Map<String, Object> params) {
        //log.info("BaseESDAO: searching with: " + params);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
        BoolQueryBuilder bool = QueryBuilders.boolQuery();

        if(params.containsKey("searchFilters")) {
            HashMap<String, HashMap<String, HashMap<String, Object>>> searchFilters = (HashMap<String, HashMap<String, HashMap<String, Object>>>)params.get("searchFilters");
            for(String filterName: searchFilters.keySet()) {
                BoolQueryBuilder innerBool = QueryBuilders.boolQuery();
                int boost = 0;
                for(String field: searchFilters.get(filterName).keySet()) {
                    float value = (float)(100/Math.pow(10, boost));
                    Operator op = Operator.AND;
                    if(((String)searchFilters.get(filterName).get(field).get("tokenOperator")).toUpperCase().equals("OR")) {
                        op = Operator.OR;
                    }

                    SimpleQueryStringBuilder simple = QueryBuilders.simpleQueryStringQuery(searchFilters.get(filterName).get(field).get("queryString").toString())
                            .field(field, value >=1 ? value : 1)
                            .defaultOperator(op)
                            //.autoGenerateSynonymsPhraseQuery(false)
                            //.fuzzyTranspositions(false)
                            .boost(value >=1 ? value : 1);
                    
                    innerBool.should().add(simple);
                    boost++;
                }
                
                bool.must().add(innerBool);
            }
        }
        
        SearchSourceBuilder query = searchSourceBuilder.query(bool);

        if(params.containsKey("sortOrders")) {
            ArrayList<HashMap<String, Object>> sortOrders = (ArrayList<HashMap<String, Object>>)params.get("sortOrders");
            if(sortOrders != null) {
                
                for(HashMap<String, Object> map: sortOrders) {
                    String key = (String)map.get("field");
                    
                    int value = (int)map.get("order");
                    if(value == 1) {
                        query.sort(new FieldSortBuilder(key + "_keyword").order(SortOrder.ASC));
                    }
                    if(value == -1) {
                        query.sort(new FieldSortBuilder(key + "_keyword").order(SortOrder.DESC));
                    }
                }
            }
        }

        searchSourceBuilder = searchSourceBuilder.from(pagination.getPage() * pagination.getLimit());
        searchSourceBuilder = searchSourceBuilder.size(pagination.getLimit());

        log.debug(searchSourceBuilder);

        SearchRequest searchRequest = new SearchRequest(esIndex); 
        searchRequest.source(searchSourceBuilder);
        
        // TODO implement aggregations

        try {
            org.elasticsearch.action.search.SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            List<E> results = new ArrayList<>(hits.getHits().length);
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                JsonObject json = new JsonObject(sourceAsString);
                results.add(json.mapTo(myClass));
            }
            SearchResponse<E> resp = new SearchResponse<E>(results);
            resp.setTotalResults(hits.getTotalHits().value);
            return resp;
        } catch (Exception e) {
            //e.printStackTrace();
            return new SearchResponse<E>();
        }

    }

    public E find(String id) {

        return null;
    }

}
