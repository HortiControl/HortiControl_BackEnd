package sptech.horticontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.request.ProdutoRequestDTO;
import sptech.horticontrol.dtos.response.ProdutoResponseDTO;
import sptech.horticontrol.service.ProdutoService;

import java.math.BigDecimal;
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

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar (@PathVariable Long id, @RequestBody ProdutoRequestDTO dto) {

        return ResponseEntity.ok(produtoService.atualizarProduto(id, dto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir (@PathVariable Long id) {

        produtoService.excluirProduto(id);
        return ResponseEntity.status(204).build();

    }

    @GetMapping("/produtos/{nome}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome (@RequestParam String nome) {

        List<ProdutoResponseDTO> listaProdutos = produtoService.buscarProdutoPorNome(nome);

        if (listaProdutos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.ok(listaProdutos);

    }

    @PatchMapping("/reajuste-global")
    public ResponseEntity<Void> reajustarPrecoGlobal(@RequestParam BigDecimal novoPreco) {

        produtoService.reajustarPrecoGlobal(novoPreco);

        return ResponseEntity.status(204).build();

    }

}
