package com.nowayback.reward.infrastructure.config;

import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingCompletedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingFailedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.FundingRefundEvent;
import com.nowayback.reward.infrastructure.kafka.dto.funding.event.ProjectFundingSuccessEvent;
import com.nowayback.reward.infrastructure.kafka.dto.project.event.ProjectCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Kafka Producer 설정
     * - JSON 직렬화, 전송 보장, 재시도 횟수, 멱등성 설정
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        applyIamAuthIfProd(configProps);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // ========== ProjectCreatedEvent 전용 설정 ==========

    @Bean
    public ConsumerFactory<String, ProjectCreatedEvent> projectCreatedConsumerFactory() {
        JsonDeserializer<ProjectCreatedEvent> valueDeserializer =
                new JsonDeserializer<>(ProjectCreatedEvent.class);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        applyIamAuthIfProd(props);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectCreatedEvent>
    projectCreatedListenerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ProjectCreatedEvent>();
        factory.setConsumerFactory(projectCreatedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ========== FundingCompletedEvent 전용 설정 ==========

    @Bean
    public ConsumerFactory<String, FundingCompletedEvent> fundingCompletedConsumerFactory() {
        JsonDeserializer<FundingCompletedEvent> valueDeserializer =
                new JsonDeserializer<>(FundingCompletedEvent.class);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        applyIamAuthIfProd(props);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FundingCompletedEvent>
    fundingCompletedListenerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, FundingCompletedEvent>();
        factory.setConsumerFactory(fundingCompletedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ========== FundingFailedEvent 전용 설정 ==========

    @Bean
    public ConsumerFactory<String, FundingFailedEvent> fundingFailedConsumerFactory() {
        JsonDeserializer<FundingFailedEvent> valueDeserializer =
                new JsonDeserializer<>(FundingFailedEvent.class);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        applyIamAuthIfProd(props);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FundingFailedEvent>
    fundingFailedListenerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, FundingFailedEvent>();
        factory.setConsumerFactory(fundingFailedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ========== FundingRefundEvent 전용 설정 ==========

    @Bean
    public ConsumerFactory<String, FundingRefundEvent> fundingRefundConsumerFactory() {
        JsonDeserializer<FundingRefundEvent> valueDeserializer =
                new JsonDeserializer<>(FundingRefundEvent.class);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        applyIamAuthIfProd(props);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FundingRefundEvent>
    fundingRefundListenerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, FundingRefundEvent>();
        factory.setConsumerFactory(fundingRefundConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ========== ProjectFundingSuccessEvent 전용 설정 ==========

    @Bean
    public ConsumerFactory<String, ProjectFundingSuccessEvent> projectFundingSuccessConsumerFactory() {
        JsonDeserializer<ProjectFundingSuccessEvent> valueDeserializer =
                new JsonDeserializer<>(ProjectFundingSuccessEvent.class);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        applyIamAuthIfProd(props);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectFundingSuccessEvent>
    projectFundingSuccessListenerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ProjectFundingSuccessEvent>();
        factory.setConsumerFactory(projectFundingSuccessConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    private void applyIamAuthIfProd(Map<String, Object> configProps) {
        if (!"prod".equals(activeProfile)) {
            return;
        }

        log.info("Applying MSK IAM authentication (profile=prod)");

        configProps.put("security.protocol", "SASL_SSL");
        configProps.put("sasl.mechanism", "AWS_MSK_IAM");
        configProps.put(
                "sasl.jaas.config",
                "software.amazon.msk.auth.iam.IAMLoginModule required;"
        );
        configProps.put(
                "sasl.client.callback.handler.class",
                "software.amazon.msk.auth.iam.IAMClientCallbackHandler"
        );
    }
}
