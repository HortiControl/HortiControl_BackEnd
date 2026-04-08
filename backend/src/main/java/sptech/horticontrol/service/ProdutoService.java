package sptech.horticontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sptech.horticontrol.dtos.request.ProdutoRequestDTO;
import sptech.horticontrol.dtos.response.MercadoResponseDTO;
import sptech.horticontrol.dtos.response.ProdutoResponseDTO;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.entity.Produto;
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
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produtoExistente.setNome(dto.getNome());
        produtoExistente.setPreco(dto.getPreco());
        produtoExistente.setTipoEmbalagem(dto.getTipoEmbalagem());

        Produto produtoAtualizado = produtoRepository.save(produtoExistente);
        return converterParaResponse(produtoAtualizado);

    }

    public void excluirProduto (Long id) {

        if(!produtoRepository.existsById(id)) {
            throw new RuntimeException("ID não existe");
        }

        produtoRepository.deleteById(id);

    }

    public List<ProdutoResponseDTO> buscarProdutoPorNome (String nome) {

        return produtoRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(p -> new ProdutoResponseDTO(p.getId(), p.getNome(), p.getPreco(), p.getTipoEmbalagem()))
                .collect(Collectors.toList());

    }

    public void reajustarPrecoGlobal (BigDecimal novoPreco) {

        List<Produto> todosProdutos = produtoRepository.findAll();

        if (novoPreco.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("O preço não pode ser negativo");
        }

        for (Produto produto : todosProdutos) {
            produto.setPreco(novoPreco);
        }

        produtoRepository.saveAll(todosProdutos);

    }

    private ProdutoResponseDTO converterParaResponse(Produto p) {
        return new ProdutoResponseDTO(p.getId(), p.getNome(), p.getPreco(), p.getTipoEmbalagem());
    }

}
