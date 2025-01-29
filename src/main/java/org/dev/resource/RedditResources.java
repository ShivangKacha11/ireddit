package org.dev.resource;


import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.dev.entity.redditChildren;
import org.dev.entity.redditChildrenData;
import org.dev.entity.redditPostResponse;
import org.dev.service.KafkaProducer;
import org.dev.tokengenerator.tokenGenerator;
import org.dev.entity.redditPostResponse;
import org.dev.proxies.fetchPostsByUserProxy;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.dev.Repository.RedditRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;


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

    String token=null;
    public String getToken(@Observes StartupEvent ev) {
        token=tokengenerator.fetchAccessToken();
        System.out.println(token);
        return token;
    }

    @GET
    @Path("/fetch-posts/{username}")
    public Response fetchPosts(@PathParam("username") String username) {
        String token = tokengenerator.getToken();
//        System.out.println("Token: "+token);
//        System.out.println(fetchPostsByUserProxy.fetchPosts(username, token));
//        return CompletableFuture.supplyAsync(() -> fetchPostsByUserProxy.fetchPosts(username, token));
        try {
//            System.out.println(fetchPostsByUserProxy.fetchPosts(username, token));
            redditPostResponse posts=fetchPostsByUserProxy.fetchPosts(username, token);
            List<redditChildren> childrenList= posts.getData().getChildren();
            childrenList.forEach(child->{
                redditChildrenData data=child.getData();
                kafkaProducer.produce(data);
//                redditRepository.addRedditPost(data);
            });
            return Response.ok(fetchPostsByUserProxy.fetchPosts(username, token)).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch posts", e);
        }
    }

//    public CompletableFuture<redditPostResponse> fetchPosts(@PathParam("username") String username) {
//        return CompletableFuture.supplyAsync(() -> fetchPostsByUserProxy.fetchPosts(username, token));
//    }
}
