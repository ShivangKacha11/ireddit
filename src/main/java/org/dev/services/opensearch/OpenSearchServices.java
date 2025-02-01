package org.dev.services.opensearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import org.dev.entity.redditChildrenData;

import java.io.IOException;

import jakarta.inject.Inject;

import java.util.*;

import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.reindex.DeleteByQueryRequest;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;


@Singleton
public class OpenSearchServices {


    @Inject
    OpenSearchClient openSearchClient;

    // Check if a document exists
    public boolean documentExists(String index, String id) throws IOException {
        RestHighLevelClient client = openSearchClient.getClient();
        System.out.println(index + id);
        GetRequest request = new GetRequest(index, id);
        System.out.println(client.get(request, RequestOptions.DEFAULT));
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response);
        return response.isExists();
    }

    // Index a post using permalink as the document ID
    public String indexPost(redditChildrenData post) throws IOException {
        RestHighLevelClient client = openSearchClient.getClient();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(post);
        System.out.println(json);
        IndexRequest request = new IndexRequest("posts")
                .id(post.getPermalink()) // Use permalink as the document ID
                .source(json, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.getId();
    }

    public List<String> searchInAllFields(String index, String queryText) throws IOException {
        RestHighLevelClient client = openSearchClient.getClient();
        System.out.println("Searching for: " + queryText);
        // Create search request for the specified index
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryStringQuery("*" + queryText + "*")); // Match any subsequence of the queryText in all fields
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<String> results = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            results.add(hit.getSourceAsString()); // Returns the full document as JSON
        }
        System.out.println(results);
        return results;
    }

}