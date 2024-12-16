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
            validarPedido(order);
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
            validarPedido(orderUpdate);
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
            if (!orderRepository.existsById(id)) {
                throw new PedidoNotFoundException("Pedido não encontrado");
            }
            orderRepository.deleteById(id);
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ORDER_SERVICE, "Pedido deletado no serviço: " + id);
        } catch (Exception exception) {
            kafkaProducerServiceConfig.enviarMensagem(TOPIC_ERROR, "Erro ao deletar pedido no serviço: " + exception.getMessage());
            throw exception;
        }
    }

    private void validarPedido(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }
        if (order.getProduct() == null || order.getProduct().trim().isEmpty()) {
            throw new IllegalArgumentException("Produto não pode ser nulo, vazio ou em branco");
        }
        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (order.getPrice() <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        // Preço total deve ser recalculado com base na quantidade e no preço unitário
        double totalPrice = order.getQuantity() * order.getPrice();
        if (totalPrice <= 0) {
            throw new IllegalArgumentException("Preço total deve ser maior que zero");
        }
        order.setTotalPrice(totalPrice);
    }

}
