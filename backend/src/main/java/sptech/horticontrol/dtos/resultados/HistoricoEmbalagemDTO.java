package sptech.horticontrol.dtos.resultados;

public record HistoricoEmbalagemDTO(
        String mes,
        Integer bandejas,
        Integer potes,
        Integer sacos
) {}
