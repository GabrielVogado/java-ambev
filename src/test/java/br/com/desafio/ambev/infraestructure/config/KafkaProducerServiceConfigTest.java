package br.com.desafio.ambev.infraestructure.config;

import br.com.desafio.ambev.infraestructure.config.kafka.KafkaProducerServiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class KafkaProducerServiceConfigTest {

    @InjectMocks
    private KafkaProducerServiceConfig kafkaProducerServiceConfig;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void enviarMensagem() {
        String topico = "test-topic";
        String mensagem = "test-message";

        kafkaProducerServiceConfig.enviarMensagem(topico, mensagem);

        verify(kafkaTemplate, times(1)).send(eq(topico), eq(mensagem));
    }
}
