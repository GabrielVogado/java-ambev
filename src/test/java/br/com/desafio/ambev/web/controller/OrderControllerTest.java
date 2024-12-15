package br.com.desafio.ambev.web.controller;

import br.com.desafio.ambev.application.service.IOrderService;
import br.com.desafio.ambev.domain.entity.Order;
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
    }

    @Test
    void listarPedidos() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderService.listarPedidos()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.listarPedidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
        verify(orderService, times(1)).listarPedidos();
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
    }

    @Test
    void deletarPedido() {
        Long id = 1L;

        ResponseEntity<Void> response = orderController.deletarPedido(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deletarPedido(id);
    }
}

