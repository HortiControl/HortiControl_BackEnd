package sptech.horticontrol.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.request.ProdutoRequestDTO;
import sptech.horticontrol.dtos.response.ProdutoResponseDTO;
import sptech.horticontrol.service.ProdutoService;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Produtos", description = "Gerenciamento de produtos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Operation(summary = "Criar produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar (@RequestBody ProdutoRequestDTO dto) {

        return ResponseEntity.status(201).body(produtoService.criarProduto(dto));

    }

    @Operation(
            summary = "Listar produtos",
            description = "Retorna todos os produtos cadastrados"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada"),
            @ApiResponse(responseCode = "204", description = "Nenhum produto encontrado")
    })

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos () {

        List<ProdutoResponseDTO> listaProdutos = produtoService.listarProdutos();

        if (listaProdutos.isEmpty()) {
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.ok(listaProdutos);
        }

    }

    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza os dados de um produto existente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar (@PathVariable Long id, @RequestBody ProdutoRequestDTO dto) {

        return ResponseEntity.ok(produtoService.atualizarProduto(id, dto));

    }

    @Operation(
            summary = "Excluir produto",
            description = "Remove um produto pelo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir (@PathVariable Long id) {

        produtoService.excluirProduto(id);
        return ResponseEntity.status(204).build();

    }

    @Operation(
            summary = "Buscar produtos por nome",
            description = "Retorna produtos que contenham o nome informado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produtos encontrados"),
            @ApiResponse(responseCode = "204", description = "Nenhum produto encontrado")
    })
    @GetMapping("/busca")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome (@RequestParam String nome) {

        List<ProdutoResponseDTO> listaProdutos = produtoService.buscarProdutoPorNome(nome);

        if (listaProdutos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.ok(listaProdutos);

    }

    @Operation(
            summary = "Reajuste global de preços",
            description = "Atualiza o preço de todos os produtos para um novo valor"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Preços reajustados com sucesso")
    })
    @PatchMapping("/reajuste-global")
    public ResponseEntity<Void> reajustarPrecoGlobal(@RequestParam BigDecimal novoPreco) {

        produtoService.reajustarPrecoGlobal(novoPreco);

        return ResponseEntity.status(204).build();

    }

}
