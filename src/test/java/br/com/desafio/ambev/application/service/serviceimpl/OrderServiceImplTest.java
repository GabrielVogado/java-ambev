package br.com.desafio.ambev.application.service.serviceimpl;

import br.com.desafio.ambev.domain.entity.Order;
import br.com.desafio.ambev.domain.exception.PedidoNotFoundException;
import br.com.desafio.ambev.domain.repository.OrderRepository;
import br.com.desafio.ambev.infraestructure.config.kafka.KafkaProducerServiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaProducerServiceConfig kafkaProducerServiceConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarPedidoComSucesso() {
        Order order = new Order(null, "Produto A", 2, 50.0, 100.0);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderServiceImpl.criarPedido(order);

        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("order-service", "Pedido criado no serviço: " + order.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void criarPedidoComErroProdutoVazio() {
        Order order = new Order(null, "", 2, 50.0, 100.0);

        assertThrows(IllegalArgumentException.class, () -> orderServiceImpl.criarPedido(order));
    }

    @Test
    void atualizarPedidoComSucesso() {
        Order orderUpdate = new Order(null, "Produto B", 2, 30.0, 60.0);
        Order existingOrder = new Order(1L, "Produto A", 2, 50.0, 100.0);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));

        Order result = orderServiceImpl.atualizarPedido(1L, orderUpdate);

        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("order-service", "Pedido atualizado no serviço: " + existingOrder.getId());
    }

    @Test
    void atualizarPedidoNaoEncontrado() {
        Order orderUpdate = new Order(null, "Produto B", 2, 30.0, 60.0);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PedidoNotFoundException.class, () -> orderServiceImpl.atualizarPedido(1L, orderUpdate));
    }

    @Test
    void deletarPedidoComSucesso() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        orderServiceImpl.deletarPedido(1L);

        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("order-service", "Pedido deletado no serviço: " + 1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletarPedidoNaoEncontrado() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        assertThrows(PedidoNotFoundException.class, () -> orderServiceImpl.deletarPedido(1L));
    }
}
