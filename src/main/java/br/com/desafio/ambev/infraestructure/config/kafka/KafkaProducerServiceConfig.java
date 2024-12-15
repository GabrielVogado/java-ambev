package br.com.desafio.ambev.infraestructure.config.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class KafkaProducerServiceConfig {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final String TOPIC_ERROR_REPROCESSING = "error-reprocessing";

    @Autowired
    public KafkaProducerServiceConfig(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarMensagem(String topico, String mensagem) {
        kafkaTemplate.send(topico, mensagem);
        int currentCount = requestCount.incrementAndGet();
        if (currentCount % 3 == 0) {
            reprocessarErro();
        }
    }

    private void reprocessarErro() {
        // Lógica para pegar uma mensagem da fila de erro e reprocessá-la
        // Simulação de reprocessamento de erro
        String mensagemErroReprocessada = "Erro reprocessado";
        try {
            kafkaTemplate.send(TOPIC_ERROR_REPROCESSING, mensagemErroReprocessada);
        } catch (Exception e) {
            kafkaTemplate.send(TOPIC_ERROR_REPROCESSING, "Erro no reprocessamento: " + e.getMessage());
        }
    }
}
