package br.com.desafio.ambev.web.controller;

import br.com.desafio.ambev.application.service.IOrderService;
import br.com.desafio.ambev.domain.entity.Order;
import br.com.desafio.ambev.domain.exception.PedidoNotFoundException;
import br.com.desafio.ambev.infraestructure.config.kafka.KafkaProducerServiceConfig;
import br.com.desafio.ambev.web.documentation.OrderDocumentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController implements OrderDocumentation {

    private final IOrderService orderService;
    private final KafkaProducerServiceConfig kafkaProducerServiceConfig;
    private final String TOPIC_ORDER_CONTROLLER = "order-controller";
    private final String TOPIC_ERROR = "error";

    @Autowired
    public OrderController(IOrderService orderService, KafkaProducerServiceConfig kafkaProducerServiceConfig) {
        this.orderService = orderService;
        this.kafkaProducerServiceConfig = kafkaProducerServiceConfig;
    }

    @PostMapping
    public ResponseEntity<Order> criarPedido(@RequestBody Order pedido) {
        try {
            Order novoPedido = orderService.criarPedido(pedido);
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ORDER_CONTROLLER, "Pedido criado no controlador: " + novoPedido.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
        } catch (IllegalArgumentException e) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro de Validação ao criar pedido no controlador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao criar pedido no controlador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> listarPedidos() {
        try {
            List<Order> pedidos = orderService.listarPedidos();
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ORDER_CONTROLLER, "Pedidos listados no controlador");
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao listar pedidos no controlador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> atualizarPedido(@PathVariable Long id, @RequestBody Order pedidoAtualizado) {
        try {
            Order pedido = orderService.atualizarPedido(id, pedidoAtualizado);
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ORDER_CONTROLLER, "Pedido atualizado no controlador: " + pedido.getId());
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro de Validação ao atualizar pedido no controlador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (PedidoNotFoundException e) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao atualizar pedido no controlador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao atualizar pedido no controlador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long id) {
        try {
            orderService.deletarPedido(id);
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ORDER_CONTROLLER, "Pedido deletado no controlador: " + id);
            return ResponseEntity.noContent().build();
        } catch (PedidoNotFoundException e) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Pedifo Não Encontrado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao deletar pedido no controlador: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
