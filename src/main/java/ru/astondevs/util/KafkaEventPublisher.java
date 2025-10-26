package ru.astondevs.util;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private static final Logger log = Logger.getLogger(KafkaEventPublisher.class.getName());
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent event for user " + key + " to topic " +  topic);
                    } else {
                        log.warning("Failed to send event for user " + key + " " + ex);
                    }
                });
    }
}
