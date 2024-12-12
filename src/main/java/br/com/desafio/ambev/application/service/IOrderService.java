package br.com.desafio.ambev.application.service;

import br.com.desafio.ambev.domain.entity.Order;

import java.util.List;

public interface IOrderService {

    List<Order> getAllOrders();

    Order saveOrder(Order order);
}

