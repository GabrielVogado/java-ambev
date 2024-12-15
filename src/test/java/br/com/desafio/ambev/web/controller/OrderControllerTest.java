package br.com.desafio.ambev.web.controller;

import br.com.desafio.ambev.application.service.IOrderService;
import br.com.desafio.ambev.domain.entity.Order;
import br.com.desafio.ambev.infraestructure.config.kafka.KafkaProducerServiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private IOrderService orderService;

    @Mock
    private KafkaProducerServiceConfig kafkaProducerServiceConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarPedido() {
        Order order = new Order();
        when(orderService.criarPedido(any(Order.class))).thenReturn(order);

        ResponseEntity<Order> response = orderController.criarPedido(order);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(order, response.getBody());
        verify(orderService, times(1)).criarPedido(order);
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("order-controller"), anyString());
    }

    @Test
    void criarPedidoComErro() {
        when(orderService.criarPedido(any(Order.class))).thenThrow(new RuntimeException("Erro simulado"));

        ResponseEntity<Order> response = orderController.criarPedido(new Order());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("error"), anyString());
    }

    @Test
    void listarPedidos() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderService.listarPedidos()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.listarPedidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
        verify(orderService, times(1)).listarPedidos();
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("order-controller"), anyString());
    }

    @Test
    void listarPedidosComErro() {
        when(orderService.listarPedidos()).thenThrow(new RuntimeException("Erro simulado"));

        ResponseEntity<List<Order>> response = orderController.listarPedidos();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("error"), anyString());
    }

    @Test
    void atualizarPedido() {
        Long id = 1L;
        Order order = new Order();
        when(orderService.atualizarPedido(eq(id), any(Order.class))).thenReturn(order);

        ResponseEntity<Order> response = orderController.atualizarPedido(id, order);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
        verify(orderService, times(1)).atualizarPedido(eq(id), eq(order));
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("order-controller"), anyString());
    }

    @Test
    void atualizarPedidoComErro() {
        Long id = 1L;
        when(orderService.atualizarPedido(eq(id), any(Order.class))).thenThrow(new RuntimeException("Erro simulado"));

        ResponseEntity<Order> response = orderController.atualizarPedido(id, new Order());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("error"), anyString());
    }

    @Test
    void deletarPedido() {
        Long id = 1L;

        ResponseEntity<Void> response = orderController.deletarPedido(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deletarPedido(id);
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("order-controller"), anyString());
    }

    @Test
    void deletarPedidoComErro() {
        Long id = 1L;
        doThrow(new RuntimeException("Erro simulado")).when(orderService).deletarPedido(id);

        ResponseEntity<Void> response = orderController.deletarPedido(id);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem(eq("error"), anyString());
    }
}
