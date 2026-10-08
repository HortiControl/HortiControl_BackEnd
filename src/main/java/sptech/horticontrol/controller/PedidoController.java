package sptech.horticontrol.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.request.PedidoRequestDTO;
import sptech.horticontrol.dtos.response.PedidoResponseDTO;
import sptech.horticontrol.enums.StatusPedido;
import sptech.horticontrol.service.PedidoService;

import java.math.BigDecimal;


@RestController
@RequestMapping("/pedidos")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Criar pedido", description = "Cria um novo pedido com itens vinculados a um mercado")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Pedido criado com sucesso", content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))), @ApiResponse(responseCode = "400", description = "Dados inválidos")})
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@RequestBody @Valid PedidoRequestDTO pedidoDto) {
        PedidoResponseDTO novoPedido = pedidoService.criarPedido(pedidoDto);
        return ResponseEntity.status(201).body(novoPedido);
    }

    @Operation(summary = "Listar pedidos ativos", description = "Lista pedidos em andamento, opcionalmente filtrando por mercado, com paginação")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Página de pedidos ativos"), @ApiResponse(responseCode = "204", description = "Nenhum pedido ativo encontrado")})
    @GetMapping("/ativos")
    public ResponseEntity<Page<PedidoResponseDTO>> listarAtivos(
            @RequestParam(required = false) Long mercadoId,
            @PageableDefault(size = 10, sort = "dataSolicitacao") Pageable pageable) {

        Page<PedidoResponseDTO> paginaAtivos = pedidoService.listarPedidosAtivos(mercadoId, pageable);

        if (paginaAtivos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(paginaAtivos);
    }

    @Operation(summary = "Listar histórico de pedidos", description = "Lista pedidos finalizados/cancelados, opcionalmente filtrando por mercado, com paginação")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Página de histórico de pedidos"), @ApiResponse(responseCode = "204", description = "Nenhum pedido encontrado")})
    @GetMapping("/historico")
    public ResponseEntity<Page<PedidoResponseDTO>> listarHistorico(
            @RequestParam(required = false) Long mercadoId,
            @PageableDefault(size = 10, sort = "dataSolicitacao") Pageable pageable) {

        Page<PedidoResponseDTO> paginaHistorico = pedidoService.listarHistoricoPedidos(mercadoId, pageable);

        if (paginaHistorico.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(paginaHistorico);
    }


    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido específico")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Status atualizado com sucesso"), @ApiResponse(responseCode = "404", description = "Pedido não encontrado")})
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestParam StatusPedido novoStatus) {

        pedidoService.atualizarStatusPedido(id, novoStatus);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Atualizar pagamento do cliente", description = "Atualiza o valor referente a quanto o cliente já pagou do pedido")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Valor atualizado com sucesso"), @ApiResponse(responseCode = "404", description = "Pedido não encontrado")})
    @PatchMapping("/{id}/pagamento")
    public ResponseEntity<Void> atualizarPagamento(@PathVariable Long id, @RequestParam BigDecimal valor) {

        pedidoService.registrarPagamento(id, valor);

        return ResponseEntity.status(204).build();

    }

    @Operation(summary = "Exclusão de um pedido por completo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        pedidoService.deletarPedido(id);
        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Remoção de um item específico de um pedido")
    @DeleteMapping("/{pedidoId}/itens/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long pedidoId, @PathVariable Long itemId) {

        pedidoService.removerItemDoPedido(pedidoId, itemId);
        return ResponseEntity.noContent().build();

    }
}
