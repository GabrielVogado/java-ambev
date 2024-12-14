package br.com.desafio.ambev.application.service;

import br.com.desafio.ambev.domain.entity.Order;

import java.util.List;

public interface IOrderService {

    Order criarPedido(Order pedido);

    Order atualizarPedido(Long id, Order pedidoAtualizado);

    void deletarPedido(Long id);

    List<Order> listarPedidos();
}

