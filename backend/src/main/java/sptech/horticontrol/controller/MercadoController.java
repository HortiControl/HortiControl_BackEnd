package sptech.horticontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.request.MercadoRequestDTO;
import sptech.horticontrol.dtos.response.MercadoResponseDTO;
import sptech.horticontrol.service.MercadoService;

import java.util.List;

@RestController
@RequestMapping("/mercados")
public class MercadoController {

    @Autowired
    private MercadoService mercadoService;

    @PostMapping
    public ResponseEntity<MercadoResponseDTO> criar(@RequestBody MercadoRequestDTO dto) {

        return ResponseEntity.status(201).body(mercadoService.criarMercado(dto));

    }

    @GetMapping
    public ResponseEntity<List<MercadoResponseDTO>> listar() {

        List<MercadoResponseDTO> listarMercados = mercadoService.listarMercados();

        if(listarMercados.isEmpty()) {
           return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.ok(listarMercados);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<MercadoResponseDTO> atualizar(@PathVariable Long id, @RequestBody MercadoRequestDTO dto) {

        return ResponseEntity.ok(mercadoService.atualizarMercado(id, dto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        mercadoService.excluirMercado(id);
        return ResponseEntity.status(204).build();

    }
}