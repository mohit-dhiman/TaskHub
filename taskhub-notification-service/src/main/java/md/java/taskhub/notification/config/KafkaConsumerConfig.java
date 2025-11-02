package md.java.taskhub.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import md.java.taskhub.notification.kafka.ConsumerTypeResolver;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.function.BiFunction;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${app.kafka.consumers.concurrency}")
    private int concurrentConsumers;
    @Value("${app.kafka.backoff.interval}")
    private long backoffInterval;
    @Value("${app.kafka.backoff.attempts}")
    private long backoffMaxAttempts;


    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties kafkaProperties,
                                                           ObjectMapper objectMapper) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(),
                new StringDeserializer(),
                new JsonDeserializer<>(objectMapper)
                        .ignoreTypeHeaders()
                        .trustedPackages("*")
                        .typeResolver(ConsumerTypeResolver::topicBaseJavaType));
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {
        // default resolver is topic-dlt and same partition
        BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> destinationResolver =
                (record, ex) ->
                        new TopicPartition(record.topic() + ".DLT", record.partition());
        return new DeadLetterPublishingRecoverer(kafkaTemplate, destinationResolver) {

        };
    }

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
        // Backoff: initial delay, multiplier (not used for FixedBackOff), max attempts
        // Example: backoffInterval= 2000, backoffMaxAttempts = 3
        // retry 3 times with 2s interval (maxAttempts = 3)
        FixedBackOff fixedBackOff = new FixedBackOff(backoffInterval, backoffMaxAttempts);

        // First retry as per the backoff (fixedBackOff) then go to the recoverer
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);
        // configure which exceptions are non-retryable (fail fast)
        errorHandler.addNotRetryableExceptions(DeserializationException.class);
        // Retry listener will be fired for each retry attempt
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            System.out.println("Retry attempt " + deliveryAttempt + " for record " + record + " due to " + ex.getMessage());
        });
        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> taskEventKafkaListenerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setConcurrency(concurrentConsumers); // parallel consumers
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
