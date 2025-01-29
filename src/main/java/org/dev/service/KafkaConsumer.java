package org.dev.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.kafka.common.TopicPartition;
import org.dev.Repository.RedditRepository;
import org.dev.entity.redditChildrenData;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class KafkaConsumer {

    @Inject
    RedditRepository redditRepository;

//    int retryCount=3;

    @Incoming("posts_in")
    @Transactional
    public CompletionStage<Void> consume(IncomingKafkaRecord<String, String> record) {
        try {
//            if(retryCount!=1){
//                System.out.println("The retry count is "+retryCount);
//                retryCount--;
//                throw new RuntimeException("Simulating error to check if kafka retries");
//            }
            String key = record.getKey();
            String value = record.getPayload();
            ObjectMapper mapper = new ObjectMapper();
            redditChildrenData deserialisedmessage = mapper.readValue(value, redditChildrenData.class);
            redditRepository.addRedditPost(deserialisedmessage);
            return record.ack().thenRun(() -> {
                System.out.println("Message acknowledged: ");
            });
        } catch (Exception e) {
            System.out.println("Error in consuming message: " + e.getMessage());
            return record.nack(e);
        }
    }
}
