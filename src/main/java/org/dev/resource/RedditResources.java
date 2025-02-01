package org.dev.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.dev.entity.redditChildren;
import org.dev.entity.redditChildrenData;
import org.dev.entity.redditPostResponse;
import org.dev.services.kafka.KafkaProducer;
import org.dev.services.opensearch.OpenSearchClient;
import org.dev.services.opensearch.OpenSearchServices;
import org.dev.tokengenerator.tokenGenerator;
import org.dev.proxies.fetchPostsByUserProxy;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.dev.Repository.RedditRepository;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;


import java.util.*;


@Path("/")
public class RedditResources {

    @Inject
    tokenGenerator tokengenerator;

    @Inject
    @RestClient
    fetchPostsByUserProxy fetchPostsByUserProxy;

    @Inject
    RedditRepository redditRepository;

    @Inject
    KafkaProducer kafkaProducer;

    @Inject
    OpenSearchServices openSearchServices;

    @Inject
    OpenSearchClient openSearchClient;

    String token = null;

    public String getToken(@Observes StartupEvent ev) {
        token = tokengenerator.fetchAccessToken();
        System.out.println(token);
        return token;
    }

    @GET
    @Path("/fetch-posts/{username}")
    public Response fetchPosts(@PathParam("username") String username) {
        String token = tokengenerator.getToken();
        try {
            redditPostResponse posts = fetchPostsByUserProxy.fetchPosts(username, token);
            List<redditChildren> childrenList = posts.getData().getChildren();
            childrenList.forEach(child -> {
                redditChildrenData data = child.getData();
                System.out.println("check1");
//                kafkaProducer.produce(data);
                try {
                    System.out.println("Checking if document exists");
                    System.out.println(data.getPermalink());
                    if (openSearchServices.documentExists("posts", data.getPermalink())) {
                        System.out.println("Document already exists");
                    }
                    else {
                        System.out.println("Document does not exist!!");
                        kafkaProducer.produce(data);
                    }
                } catch (Exception e) {
                    System.out.println("Document does not exist");
                }
//                kafkaProducer.produce(data);
            });
            return Response.ok(fetchPostsByUserProxy.fetchPosts(username, token)).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch posts", e);
        }
    }


    //search-posts-by-anything
    @GET
    @Path("/search-posts/{search}")
    public Response searchPosts(@PathParam("search") String search) {
        List<redditChildren> children=new ArrayList<>();
        try {
            List<String> result=openSearchServices.searchInAllFields("posts", search);
            ObjectMapper mapper = new ObjectMapper();
            result.forEach(child->{
                try {
                    redditChildren full = new redditChildren();
                    redditChildrenData data = mapper.readValue(child, redditChildrenData.class);
                    full.setData(data);
                    children.add(full);
                }
                catch (Exception e) {
                    throw new RuntimeException("Failed to search posts", e);
                }
            });
            return Response.ok(children).build();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to search posts", e);
        }
    }




//    public CompletableFuture<redditPostResponse> fetchPosts(@PathParam("username") String username) {
//        return CompletableFuture.supplyAsync(() -> fetchPostsByUserProxy.fetchPosts(username, token));
//    }
}
