package sptech.horticontrol.service.resultado;

import org.springframework.stereotype.Component;
import sptech.horticontrol.dtos.resultados.EmbalagemDataDTO;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.enums.TipoEmbalagem;

import java.util.List;

/*
 * Responsável apenas por contar itens de pedidos agrupados por tipo de
 * embalagem (bandeja, pote ou saco).
 *
 * Extraído de ResultadoService para que a contagem por embalagem possa ser
 * reaproveitada (consumo do período selecionado e histórico anual) e testada
 * isoladamente, sem depender do restante do relatório.
 */
@Component
public class EmbalagemCalculator {

    public int somarPorEmbalagem(List<Pedido> pedidos, TipoEmbalagem tipo) {
        return pedidos.stream()
                .flatMap(p -> p.getItens().stream())
                .filter(i -> i.getProduto().getTipoEmbalagem() == tipo)
                .mapToInt(i -> i.getQuantidade())
                .sum();
    }

    public EmbalagemDataDTO calcular(List<Pedido> pedidos) {
        return new EmbalagemDataDTO(
                somarPorEmbalagem(pedidos, TipoEmbalagem.BANDEJA),
                somarPorEmbalagem(pedidos, TipoEmbalagem.POTE),
                somarPorEmbalagem(pedidos, TipoEmbalagem.SACO)
        );
    }
}
