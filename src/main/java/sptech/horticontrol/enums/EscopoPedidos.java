package sptech.horticontrol.enums;

/*
 * Recorte de pedidos usado pelo microsserviço de exportação em PDF,
 * que trabalha com estes três conjuntos em vez das listagens
 * separadas de ativos e histórico da API.
 */
public enum EscopoPedidos {

    ATIVOS,
    HISTORICO,
    TODOS

}
