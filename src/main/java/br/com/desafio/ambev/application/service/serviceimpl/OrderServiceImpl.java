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

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, KafkaProducerServiceConfig kafkaProducerServiceConfig) {
        this.orderRepository = orderRepository;
        this.kafkaProducerServiceConfig = kafkaProducerServiceConfig;
    }

    public Order criarPedido(Order order) {
        order.setTotalPrice(order.getQuantity() * order.getPrice());
        Order saveOrder = orderRepository.save(order);
        kafkaProducerServiceConfig.enviarMensagem("order", "Pedido criado: " + saveOrder.getId());
        return saveOrder;
    }

    public List<Order> listarPedidos() {
        return orderRepository.findAll();
    }

    public Order atualizarPedido(Long id, Order orderUpdate) {
        Optional<Order> optionalPedido = orderRepository.findById(id);
        if (optionalPedido.isPresent()) {
            Order order = optionalPedido.get();
            order.setProduct(orderUpdate.getProduct());
            order.setQuantity(orderUpdate.getQuantity());
            order.setPrice(orderUpdate.getPrice());
            order.setTotalPrice(order.getQuantity() * order.getPrice());
            kafkaProducerServiceConfig.enviarMensagem("order", "Pedido atualizado: " + order.getId());
            return orderRepository.save(order);
        } else {
            throw new PedidoNotFoundException("Pedido não encontrado");
        }
    }

    public void deletarPedido(Long id) {
        orderRepository.deleteById(id);
        kafkaProducerServiceConfig.enviarMensagem("order", "Pedido deletado: " + id);
    }

}
