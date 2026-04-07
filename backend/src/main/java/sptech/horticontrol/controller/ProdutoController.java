package sptech.horticontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.request.ProdutoRequestDTO;
import sptech.horticontrol.dtos.response.ProdutoResponseDTO;
import sptech.horticontrol.service.ProdutoService;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar (@RequestBody ProdutoRequestDTO dto) {

        return ResponseEntity.status(201).body(produtoService.criarProduto(dto));

    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos () {

        List<ProdutoResponseDTO> listaProdutos = produtoService.listarProdutos();

        if (listaProdutos.isEmpty()) {
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.ok(listaProdutos);
        }

    }



}
