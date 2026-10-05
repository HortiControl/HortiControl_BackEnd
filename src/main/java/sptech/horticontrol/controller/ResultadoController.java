package sptech.horticontrol.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sptech.horticontrol.dtos.resultados.ResultadosResponseDTO;
import sptech.horticontrol.service.ResultadoService;

@RestController
@RequestMapping("/resultados")
@Tag(name = "Resultados", description = "Dados analíticos do sistema")
public class ResultadoController {

    private final ResultadoService resultadoService;

    public ResultadoController(ResultadoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    @GetMapping
    public ResponseEntity<ResultadosResponseDTO> retornoDados(
            @RequestParam(defaultValue = "HOJE") String periodo) {
        return ResponseEntity.ok(resultadoService.gerarResultados(periodo));
    }

}
