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
import sptech.horticontrol.dtos.request.MercadoRequestDTO;
import sptech.horticontrol.dtos.response.MercadoResponseDTO;
import sptech.horticontrol.service.MercadoService;


@RestController
@RequestMapping("/mercados")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Mercados", description = "Endpoints para gerenciamento de mercados")
public class MercadoController {

    private final MercadoService mercadoService;

    public MercadoController(MercadoService mercadoService) {
        this.mercadoService = mercadoService;
    }

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
            description = "Retorna todos os mercados cadastrados, com paginação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de mercados retornada"),
            @ApiResponse(responseCode = "204", description = "Nenhum mercado encontrado")
    })
    @GetMapping
    public ResponseEntity<Page<MercadoResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<MercadoResponseDTO> pagina = mercadoService.listarMercados(pageable);

        if(pagina.isEmpty()) {
           return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.ok(pagina);
        }
    }

    @Operation(
            summary = "Listar Mercados Consignados",
            description = "Retorna todos os mercados Consignado cadastrados, com paginação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de mercados retornada"),
            @ApiResponse(responseCode = "204", description = "Nenhum mercado encontrado")
    })

    @GetMapping("/consignados")
    public ResponseEntity<Page<MercadoResponseDTO>> listarConsignados(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<MercadoResponseDTO> pagina = mercadoService.mercadosConsignados(pageable);
        if (pagina.isEmpty()) {
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.ok(pagina);
        }
    }

    @Operation(
            summary = "Listar Mercados Normais",
            description = "Retorna todos os mercados Normais cadastrados, com paginação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de mercados retornada"),
            @ApiResponse(responseCode = "204", description = "Nenhum mercado encontrado")
    })

    @GetMapping("/normais")
    public ResponseEntity<Page<MercadoResponseDTO>> listarNormais(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<MercadoResponseDTO> pagina = mercadoService.mercadosNormais(pageable);

        if (pagina.isEmpty()) {
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.ok(pagina);
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