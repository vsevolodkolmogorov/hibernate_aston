package ru.astondevs.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.test.context.EmbeddedKafka;
import ru.astondevs.dto.UserDto;
import ru.astondevs.entity.User;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.service.impl.UserServiceImpl;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "user.created", "user.deleted" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceKafkaIntegrationTest {

    @Autowired(required = false)
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private Consumer<String, UserDto> consumer;

    @BeforeAll
    void setup() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        JsonDeserializer<UserDto> deserializer = new JsonDeserializer<>(UserDto.class);
        deserializer.addTrustedPackages("*");
        consumer = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        ).createConsumer();
        consumer.subscribe(List.of("user.created", "user.deleted"));
    }

    @AfterAll
    void tearDown() {
        consumer.close();
    }

    @Test
    void shouldPublishUserCreatedEventToKafka() {
        UserDto dto = new UserDto();
        dto.setName("John");
        dto.setEmail("john.doe@mail.com");
        dto.setAge(30);
        dto.setRole_id(1L);

        userService.save(dto);

        ConsumerRecord<String, UserDto> record =
                KafkaTestUtils.getSingleRecord(consumer, "user.created", Duration.ofSeconds(5));

        assertNotNull(record);
        assertEquals("john.doe@mail.com", record.key());
        assertEquals(dto.getEmail(), record.value().getEmail());
        assertEquals(dto.getName(), record.value().getName());
    }

    @Test
    void shouldPublishUserDeletedEventToKafka() {
        UserDto dto = new UserDto();
        dto.setName("John");
        dto.setEmail("test+@mail.com");
        dto.setAge(30);
        dto.setRole_id(1L);

        userService.save(dto);
        User user = userRepository.findUserByEmail("test+@mail.com").orElseThrow(NoSuchElementException::new);
        userService.delete(user.getId());

        ConsumerRecord<String, UserDto> record =
                KafkaTestUtils.getSingleRecord(consumer, "user.deleted", Duration.ofSeconds(5));

        assertNotNull(record);
        assertEquals("test+@mail.com", record.key());
        assertEquals(dto.getEmail(), record.value().getEmail());
        assertEquals(dto.getName(), record.value().getName());
    }
}
