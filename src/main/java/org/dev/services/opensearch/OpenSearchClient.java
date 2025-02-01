package org.dev.services.opensearch;


import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.http.HttpHost;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;

@ApplicationScoped
public class OpenSearchClient {

    RestHighLevelClient client;

    @ConfigProperty(name = "openSearch.host")
    String host;
    @ConfigProperty(name = "openSearch.port")
    int port;

    public void init(@Observes StartupEvent ev) {
        System.out.println("Connecting to OpenSearchClient");
        System.out.println("Initialising RestHighLevelClient");
        this.client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port, "http")));
        System.out.println("Connected to OpenSearchClient");
    }

    public RestHighLevelClient getClient() {
        return client;
    }
}