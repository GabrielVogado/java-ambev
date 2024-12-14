package br.com.desafio.ambev.web.documentation;

import br.com.desafio.ambev.domain.entity.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Orders", description = "APIs relacionadas a pedidos")
public interface OrderDocumentation {

    @Operation(summary = "Cria um novo pedido", responses = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    ResponseEntity<Order> criarPedido(@RequestBody Order pedido);

    @Operation(summary = "Lista todos os pedidos", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    })
    ResponseEntity<List<Order>> listarPedidos();

    @Operation(summary = "Atualiza um pedido existente", responses = {
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    ResponseEntity<Order> atualizarPedido(@PathVariable Long id, @RequestBody Order pedidoAtualizado);

    @Operation(summary = "Deleta um pedido existente", responses = {
            @ApiResponse(responseCode = "204", description = "Pedido deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    ResponseEntity<Void> deletarPedido(@PathVariable Long id);

}
