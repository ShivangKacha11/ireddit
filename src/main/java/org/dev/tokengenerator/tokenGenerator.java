package org.dev.tokengenerator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.dev.entity.tokenResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Base64;

@ApplicationScoped
public class tokenGenerator {

    @RestClient
    @Inject
    org.dev.proxies.tokenGeneratorProxy tokenGeneratorProxy;

    @ConfigProperty(name = "reddit.client.id")
    public String CLIENT_ID;

    @ConfigProperty(name="reddit.client.secret")
    public String CLIENT_SECRET;

    private static String token=null;

    public String fetchAccessToken(){
        if(token!=null){
            return token;
        }
        System.out.println(CLIENT_ID+" "+CLIENT_SECRET);
        String authString = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedString = Base64.getEncoder().encodeToString(authString.getBytes());

        try{
            tokenResponse response = tokenGeneratorProxy.getAccessToken("client_credentials","Basic "+encodedString);
            token="Bearer "+response.getAccessToken();
            System.out.println(token);
            return token;
        }
        catch(Exception e) {
            throw new RuntimeException("Failed to extract access token", e);
        }
    }


    public String getToken() {
        return fetchAccessToken();
    }
}
