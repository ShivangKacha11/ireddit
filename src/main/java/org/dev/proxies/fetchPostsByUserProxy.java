package org.dev.proxies;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import org.dev.entity.tokenResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import org.dev.entity.redditPostResponse;

@RegisterRestClient(baseUri = "https://oauth.reddit.com")
@ApplicationScoped
public interface fetchPostsByUserProxy {

    @GET
    @Path("/user/{username}/submitted")
    @Produces(MediaType.APPLICATION_JSON)
    redditPostResponse fetchPosts(
            @PathParam("username") String username,
            @HeaderParam("Authorization") String auth
    );

}