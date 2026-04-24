package sptech.horticontrol.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.request.MercadoRequestDTO;
import sptech.horticontrol.dtos.response.MercadoResponseDTO;
import sptech.horticontrol.service.MercadoService;

import java.util.List;

@RestController
@RequestMapping("/mercados")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Mercados", description = "Endpoints para gerenciamento de mercados")
public class MercadoController {

    @Autowired
    private MercadoService mercadoService;

    @Operation(
            summary = "Cadastrar mercado",
            description = "Cria um novo mercado no sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mercado criado com sucesso",
                    content = @Content(schema = @Schema(implementation = MercadoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<MercadoResponseDTO> criar(@RequestBody @Valid MercadoRequestDTO dto) {
        return ResponseEntity.status(201).body(mercadoService.criarMercado(dto));
    }

    @Operation(
            summary = "Listar mercados",
            description = "Retorna todos os mercados cadastrados"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de mercados retornada"),
            @ApiResponse(responseCode = "204", description = "Nenhum mercado encontrado")
    })
    @GetMapping
    public ResponseEntity<List<MercadoResponseDTO>> listar() {

        List<MercadoResponseDTO> listarMercados = mercadoService.listarMercados();

        if(listarMercados.isEmpty()) {
           return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.ok(listarMercados);
        }

    }

    @Operation(
            summary = "Atualizar mercado",
            description = "Atualiza os dados de um mercado pelo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mercado atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MercadoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MercadoRequestDTO dto) {

        return ResponseEntity.ok(mercadoService.atualizarMercado(id, dto));
    }

    @Operation(
            summary = "Excluir mercado",
            description = "Remove um mercado pelo ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mercado removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        mercadoService.excluirMercado(id);
        return ResponseEntity.status(204).build();

    }
}