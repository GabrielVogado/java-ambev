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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaProducerServiceConfig kafkaProducerServiceConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarPedido() {
        Order order = new Order();
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.criarPedido(order);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(order);
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("order-service"), anyString());
    }

    @Test
    void criarPedidoComErro() {
        Order order = new Order();
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Erro simulado"));

        assertThrows(RuntimeException.class, () -> orderService.criarPedido(order));

        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("error"), anyString());
    }

    @Test
    void listarPedidos() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.listarPedidos();

        assertEquals(orders, result);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void listarPedidosComErro() {
        when(orderRepository.findAll()).thenThrow(new RuntimeException("Erro simulado"));

        assertThrows(RuntimeException.class, () -> orderService.listarPedidos());

        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("error"), anyString());
    }

    @Test
    void atualizarPedido() {
        Long id = 1L;
        Order order = new Order();
        when(orderRepository.findById(eq(id))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order orderUpdate = new Order();
        Order result = orderService.atualizarPedido(id, orderUpdate);

        assertNotNull(result);
        verify(orderRepository, times(1)).findById(id);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("order-service"), anyString());
    }

    @Test
    void atualizarPedidoComErro() {
        Long id = 1L;
        when(orderRepository.findById(eq(id))).thenThrow(new RuntimeException("Erro simulado"));

        assertThrows(RuntimeException.class, () -> orderService.atualizarPedido(id, new Order()));

        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("error"), anyString());
    }

    @Test
    void atualizarPedidoNotFound() {
        Long id = 1L;
        when(orderRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThrows(PedidoNotFoundException.class, () -> orderService.atualizarPedido(id, new Order()));

        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("error"), anyString());
    }

    @Test
    void deletarPedido() {
        Long id = 1L;

        orderService.deletarPedido(id);

        verify(orderRepository, times(1)).deleteById(id);
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("order-service"), anyString());
    }

    @Test
    void deletarPedidoComErro() {
        Long id = 1L;
        doThrow(new RuntimeException("Erro simulado")).when(orderRepository).deleteById(id);

        assertThrows(RuntimeException.class, () -> orderService.deletarPedido(id));

        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("error"), anyString());
    }
}
