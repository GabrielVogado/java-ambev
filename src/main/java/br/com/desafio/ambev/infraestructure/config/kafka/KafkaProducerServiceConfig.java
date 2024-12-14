package br.com.desafio.ambev.infraestructure.config.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceConfig {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void enviarMensagem(String topico, String mensagem) {
        kafkaTemplate.send(topico, mensagem);
    }
}

