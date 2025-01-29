package org.dev.proxies;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import org.dev.entity.tokenResponse;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;


@RegisterRestClient(baseUri = "https://www.reddit.com")
@ApplicationScoped
public interface tokenGeneratorProxy {

    @POST
    @Path("/api/v1/access_token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    tokenResponse getAccessToken(
            @QueryParam("grant_type") String grantType,
            @HeaderParam("Authorization") String auth
    );
}