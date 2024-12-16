package br.com.desafio.ambev.web.controller;

import br.com.desafio.ambev.application.service.IOrderService;
import br.com.desafio.ambev.domain.entity.Order;
import br.com.desafio.ambev.domain.exception.PedidoNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
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
    void criarPedidoComSucesso() {
        Order order = new Order(null, "Produto A", 2, 50.0, 100.0);
        when(orderService.criarPedido(any(Order.class))).thenReturn(order);

        ResponseEntity<Order> response = orderController.criarPedido(order);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("order-controller", "Pedido criado no controlador: " + order.getId());
    }

    @Test
    void criarPedidoComErro() {
        Order order = new Order(null, "", 2, 50.0, 100.0);
        doThrow(new IllegalArgumentException("Produto não pode ser nulo, vazio ou em branco")).when(orderService).criarPedido(any(Order.class));

        ResponseEntity<Order> response = orderController.criarPedido(order);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("error", "Erro ao criar pedido no controlador: Produto não pode ser nulo, vazio ou em branco");
    }

    @Test
    void listarPedidosComSucesso() {
        List<Order> pedidos = Arrays.asList(
                new Order(1L, "Produto A", 2, 50.0, 100.0),
                new Order(2L, "Produto B", 1, 30.0, 30.0)
        );
        when(orderService.listarPedidos()).thenReturn(pedidos);

        ResponseEntity<List<Order>> response = orderController.listarPedidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pedidos, response.getBody());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("order-controller", "Pedidos listados no controlador");
    }

    @Test
    void atualizarPedidoComSucesso() {
        Order orderUpdate = new Order(null, "Produto B", 2, 30.0, 60.0);
        Order updatedOrder = new Order(1L, "Produto B", 2, 30.0, 60.0);
        when(orderService.atualizarPedido(eq(1L), any(Order.class))).thenReturn(updatedOrder);

        ResponseEntity<Order> response = orderController.atualizarPedido(1L, orderUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedOrder, response.getBody());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("order-controller", "Pedido atualizado no controlador: " + updatedOrder.getId());
    }

    @Test
    void atualizarPedidoComErro() {
        Order orderUpdate = new Order(null, "", 2, 50.0, 100.0);
        doThrow(new IllegalArgumentException("Produto não pode ser nulo, vazio ou em branco")).when(orderService).atualizarPedido(eq(1L), any(Order.class));

        ResponseEntity<Order> response = orderController.atualizarPedido(1L, orderUpdate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("error", "Erro ao atualizar pedido no controlador: Produto não pode ser nulo, vazio ou em branco");
    }

    @Test
    void deletarPedidoComSucesso() {
        ResponseEntity<Void> response = orderController.deletarPedido(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("order-controller", "Pedido deletado no controlador: " + 1L);
    }

    @Test
    void deletarPedidoNaoEncontrado() {
        doThrow(new PedidoNotFoundException("Pedido não encontrado")).when(orderService).deletarPedido(999L);

        ResponseEntity<Void> response = orderController.deletarPedido(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(kafkaProducerServiceConfig, times(1)).enviarMensagem("error", "Erro ao deletar pedido no controlador: Pedido não encontrado");
    }
}
