package sptech.horticontrol.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sptech.horticontrol.dtos.response.PedidoResponseDTO;
import sptech.horticontrol.enums.EscopoPedidos;
import sptech.horticontrol.service.PedidoService;

import java.util.List;

/*
 * Porta de serviço consumida pelo microsserviço de exportação em PDF.
 *
 * Não é destinada ao navegador: o acesso é liberado apenas para quem
 * apresenta a chave compartilhada verificada pelo InternalApiKeyFilter.
 *
 * @Hidden mantém a rota fora da documentação pública do Swagger.
 */
@Hidden
@RestController
@RequestMapping("/internal/pedidos")
public class InternalPedidoController {

    private final PedidoService pedidoService;

    public InternalPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar(
            @RequestParam(defaultValue = "TODOS") EscopoPedidos escopo,
            @RequestParam(required = false) Long mercadoId) {

        return ResponseEntity.ok(
                pedidoService.listarPorEscopo(escopo, mercadoId));
    }
}
