package sptech.horticontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.request.PedidoRequestDTO;
import sptech.horticontrol.dtos.response.PedidoResponseDTO;
import sptech.horticontrol.enums.StatusPedido;
import sptech.horticontrol.service.PedidoService;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@RequestBody PedidoRequestDTO pedidoDto) {
        PedidoResponseDTO novoPedido = pedidoService.criarPedido(pedidoDto);
        return ResponseEntity.status(201).body(novoPedido);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<PedidoResponseDTO>> listarAtivos(@RequestParam(required = false) Long mercadoId) {

        List<PedidoResponseDTO> listaAtivos = pedidoService.listarPedidosAtivos(mercadoId);

        if (listaAtivos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(listaAtivos);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<PedidoResponseDTO>> listarHistorico(@RequestParam(required = false) Long mercadoId) {

        List<PedidoResponseDTO> historico = pedidoService.listarHistoricoPedidos(mercadoId);

        if (historico.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(historico);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusPedido novoStatus) {

        pedidoService.atualizarStatusPedido(id, novoStatus);
        return ResponseEntity.status(204).build();
    }
}
