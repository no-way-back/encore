package com.nowayback.reward.infrastructure.config;

import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingCompletedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingFailedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingRefundEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.ProjectFundingSuccessEvent;
import com.nowayback.reward.infrastructure.kafka.dto.project.event.ProjectCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> {
                    String dlqTopic = record.topic() + ".DLQ";
                    log.error("메시지를 DLQ로 전송 - topic: {}, DLQ: {}, offset: {}, error: {}",
                            record.topic(), dlqTopic, record.offset(), ex.getMessage());
                    return new TopicPartition(dlqTopic, record.partition());
                }
        );
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(
            DeadLetterPublishingRecoverer recoverer
    ) {
        FixedBackOff fixedBackOff = new FixedBackOff(1000L, 5L);
        return new DefaultErrorHandler(recoverer, fixedBackOff);
    }

    // ========== 공통 헬퍼 ==========

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> targetType) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        JsonDeserializer<T> valueDeserializer = new JsonDeserializer<>(targetType);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeMapperForKey(false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildListenerFactory(
            ConsumerFactory<String, T> consumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, T>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    // ========== ProjectCreatedEvent ==========

    @Bean
    public ConsumerFactory<String, ProjectCreatedEvent> projectCreatedConsumerFactory() {
        return buildConsumerFactory(ProjectCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectCreatedEvent>
    projectCreatedListenerFactory(DefaultErrorHandler errorHandler) {
        return buildListenerFactory(projectCreatedConsumerFactory(), errorHandler);
    }

    // ========== FundingFailedEvent ==========

    @Bean
    public ConsumerFactory<String, FundingFailedEvent> fundingFailedConsumerFactory() {
        return buildConsumerFactory(FundingFailedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FundingFailedEvent>
    fundingFailedListenerFactory(DefaultErrorHandler errorHandler) {
        return buildListenerFactory(fundingFailedConsumerFactory(), errorHandler);
    }

    // ========== FundingRefundEvent ==========

    @Bean
    public ConsumerFactory<String, FundingRefundEvent> fundingRefundConsumerFactory() {
        return buildConsumerFactory(FundingRefundEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FundingRefundEvent>
    fundingRefundListenerFactory(DefaultErrorHandler errorHandler) {
        return buildListenerFactory(fundingRefundConsumerFactory(), errorHandler);
    }

    // ========== FundingCompletedEvent ==========

    @Bean
    public ConsumerFactory<String, FundingCompletedEvent> fundingCompletedConsumerFactory() {
        return buildConsumerFactory(FundingCompletedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FundingCompletedEvent>
    fundingCompletedListenerFactory(DefaultErrorHandler errorHandler) {
        return buildListenerFactory(fundingCompletedConsumerFactory(), errorHandler);
    }

    // ========== ProjectFundingSuccessEvent ==========

    @Bean
    public ConsumerFactory<String, ProjectFundingSuccessEvent> projectFundingSuccessConsumerFactory() {
        return buildConsumerFactory(ProjectFundingSuccessEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectFundingSuccessEvent>
    projectFundingSuccessListenerFactory(DefaultErrorHandler errorHandler) {
        return buildListenerFactory(projectFundingSuccessConsumerFactory(), errorHandler);
    }
}