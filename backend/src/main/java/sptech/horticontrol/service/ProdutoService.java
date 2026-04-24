package sptech.horticontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public ProdutoResponseDTO criarProduto (ProdutoRequestDTO dto) {

        Produto novoProduto = new Produto();
        novoProduto.setNome(dto.getNome());
        novoProduto.setPreco(dto.getPreco());
        novoProduto.setTipoEmbalagem(dto.getTipoEmbalagem());
        novoProduto.setTipoProduto(dto.getTipoProduto());

        Produto salvo = produtoRepository.save(novoProduto);
        return converterParaResponse(salvo);

    }

    public List<ProdutoResponseDTO> listarProdutos () {

        return produtoRepository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());

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

    public List<ProdutoResponseDTO> buscarProdutoPorNome (String nome) {

        return produtoRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(p -> new ProdutoResponseDTO(p.getId(), p.getNome(), p.getPreco(), p.getTipoEmbalagem(), p.getTipoProduto()))
                .collect(Collectors.toList());

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
