package br.com.desafio.ambev.application.service.serviceimpl;

import br.com.desafio.ambev.application.service.IOrderService;
import br.com.desafio.ambev.domain.entity.Order;
import br.com.desafio.ambev.domain.exception.PedidoNotFoundException;
import br.com.desafio.ambev.domain.repository.OrderRepository;
import br.com.desafio.ambev.infraestructure.config.kafka.KafkaProducerServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final KafkaProducerServiceConfig kafkaProducerServiceConfig;
    private final String TOPIC_ORDER_SERVICE = "order-service";
    private final String TOPIC_ERROR = "error";

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, KafkaProducerServiceConfig kafkaProducerServiceConfig) {
        this.orderRepository = orderRepository;
        this.kafkaProducerServiceConfig = kafkaProducerServiceConfig;
    }

    public Order criarPedido(Order order) {
        try {
            order.setTotalPrice(order.getQuantity() * order.getPrice());
            Order saveOrder = orderRepository.save(order);
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ORDER_SERVICE, "Pedido criado no serviço: " + saveOrder.getId());
            return saveOrder;
        } catch (Exception exception) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao criar pedido no serviço: " + exception.getMessage());
            throw exception;
        }
    }

    public List<Order> listarPedidos() {
        try {
            return orderRepository.findAll();
        } catch (Exception exception) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao listar pedidos no serviço: " + exception.getMessage());
            throw exception;
        }
    }

    public Order atualizarPedido(Long id, Order orderUpdate) {
        try {
            Optional<Order> optionalPedido = orderRepository.findById(id);
            if (optionalPedido.isPresent()) {
                Order order = optionalPedido.get();
                order.setProduct(orderUpdate.getProduct());
                order.setQuantity(orderUpdate.getQuantity());
                order.setPrice(orderUpdate.getPrice());
                order.setTotalPrice(order.getQuantity() * order.getPrice());
                kafkaProducerServiceConfig.enviarMensagem(TOPIC_ORDER_SERVICE, "Pedido atualizado no serviço: " + order.getId());
                return orderRepository.save(order);
            } else {
                throw new PedidoNotFoundException("Pedido não encontrado");
            }
        } catch (Exception exception) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao atualizar pedido no serviço: " + exception.getMessage());
            throw exception;
        }
    }

    public void deletarPedido(Long id) {
        try {
            orderRepository.deleteById(id);
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ORDER_SERVICE, "Pedido deletado no serviço: " + id);
        } catch (Exception exception) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao deletar pedido no serviço: " + exception.getMessage());
            throw exception;
        }
    }
}
