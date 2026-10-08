package sptech.horticontrol.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sptech.horticontrol.dtos.request.ProdutoRequestDTO;
import sptech.horticontrol.dtos.response.ProdutoResponseDTO;
import sptech.horticontrol.entity.Produto;
import sptech.horticontrol.enums.TipoProduto;
import sptech.horticontrol.exceptions.RecursoNaoEncontradoException;
import sptech.horticontrol.exceptions.RegraNegocioException;
import sptech.horticontrol.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public ProdutoResponseDTO criarProduto (ProdutoRequestDTO dto) {

        Produto novoProduto = new Produto();
        novoProduto.setNome(dto.getNome());
        novoProduto.setPreco(dto.getPreco());
        novoProduto.setTipoEmbalagem(dto.getTipoEmbalagem());
        novoProduto.setTipoProduto(dto.getTipoProduto());

        Produto salvo = produtoRepository.save(novoProduto);
        return converterParaResponse(salvo);

    }

    public Page<ProdutoResponseDTO> listarProdutos(Pageable pageable) {
        return produtoRepository.findAll(pageable)
                .map(this::converterParaResponse);
    }

    public Page<ProdutoResponseDTO> produtosPreLavados(Pageable pageable) {
        return produtoRepository.findByTipoProduto(TipoProduto.PRE_LAVADO, pageable)
                .map(this::converterParaResponse);
    }

    public Page<ProdutoResponseDTO> produtosNaoLavados(Pageable pageable) {
        return produtoRepository.findByTipoProduto(TipoProduto.NAO_LAVADO, pageable)
                .map(this::converterParaResponse);
    }

    public ProdutoResponseDTO atualizarProduto (Long id, ProdutoRequestDTO dto) {

        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

        produtoExistente.setNome(dto.getNome());
        produtoExistente.setPreco(dto.getPreco());
        produtoExistente.setTipoEmbalagem(dto.getTipoEmbalagem());
        produtoExistente.setTipoProduto(dto.getTipoProduto());

        Produto produtoAtualizado = produtoRepository.save(produtoExistente);
        return converterParaResponse(produtoAtualizado);

    }

    public void excluirProduto (Long id) {

        if (!produtoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado");
        }

        produtoRepository.deleteById(id);

    }

    public Page<ProdutoResponseDTO> buscarProdutoPorNome(String nome, Pageable pageable) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(this::converterParaResponse);
    }

    public void reajustarPrecoGlobal (BigDecimal novoPreco) {

        if (novoPreco.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Preço não pode ser negativo");
        }

        List<Produto> produtosPreLavados = produtoRepository.findByTipoProduto(TipoProduto.PRE_LAVADO);

        for (Produto produto : produtosPreLavados) {
            produto.setPreco(novoPreco);
        }

        produtoRepository.saveAll(produtosPreLavados);

    }

    private ProdutoResponseDTO converterParaResponse(Produto p) {
        return new ProdutoResponseDTO(p.getId(), p.getNome(), p.getPreco(), p.getTipoEmbalagem(), p.getTipoProduto());
    }

}
