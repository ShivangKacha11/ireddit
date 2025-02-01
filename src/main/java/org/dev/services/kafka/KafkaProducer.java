package org.dev.services.kafka;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import org.dev.entity.redditChildrenData;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class KafkaProducer {

    @Channel("redditPost")
    Emitter<String> emitter;

    public void produce(redditChildrenData message){
        try {
//            System.out.println(message);
            ObjectMapper mapper = new ObjectMapper();
            String serialisedmessage = mapper.writeValueAsString(message);
            CompletionStage<Void> ackStage = emitter.send(serialisedmessage);
            ackStage.whenComplete((ack, error) -> {
                if (error != null) {
                    System.out.println("Error in sending message: " + error.getMessage());
                    throw new RuntimeException(error);
                } else {
                    System.out.println("Message sent to Kafka: ");
                }
            });
        } catch (Exception e) {
            System.out.println("Error in producing message: " + e.getMessage());
        }
    }
}