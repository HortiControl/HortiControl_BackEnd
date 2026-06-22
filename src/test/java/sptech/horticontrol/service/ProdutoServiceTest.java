package sptech.horticontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sptech.horticontrol.dtos.request.ProdutoRequestDTO;
import sptech.horticontrol.dtos.response.ProdutoResponseDTO;
import sptech.horticontrol.entity.Produto;
import sptech.horticontrol.enums.TipoEmbalagem;
import sptech.horticontrol.enums.TipoProduto;
import sptech.horticontrol.exceptions.RecursoNaoEncontradoException;
import sptech.horticontrol.exceptions.RegraNegocioException;
import sptech.horticontrol.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Integra o Mockito com o JUnit.
// Permite usar @Mock e @InjectMocks.
@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    // public ProdutoResponseDTO criarProduto (ProdutoRequestDTO dto)
    @Test
    void deveCriarProduto() {

        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        dto.setNome("Alface");
        dto.setPreco(new BigDecimal("10.00"));
        dto.setTipoProduto(TipoProduto.PRE_LAVADO);
        dto.setTipoEmbalagem(TipoEmbalagem.BANDEJA);

        Produto produtoSalvo = new Produto();
        produtoSalvo.setId(1L);
        produtoSalvo.setNome("Alface");
        produtoSalvo.setPreco(new BigDecimal("10.00"));
        produtoSalvo.setTipoProduto(TipoProduto.PRE_LAVADO);
        produtoSalvo.setTipoEmbalagem(TipoEmbalagem.BANDEJA);

        when(produtoRepository.save(any(Produto.class)))
                .thenReturn(produtoSalvo);

        ProdutoResponseDTO resultado = produtoService.criarProduto(dto);

        assertNotNull(resultado);
        assertEquals("Alface", resultado.getNome());
        assertEquals(new BigDecimal("10.00"), resultado.getPreco());

        verify(produtoRepository, times(1))
                .save(any(Produto.class));
    }

    // public List<ProdutoResponseDTO> listarProdutos()
    @Test
    void deveListarProdutos() {

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Tomate");
        produto.setPreco(new BigDecimal("8.00"));

        when(produtoRepository.findAll()).thenReturn(List.of(produto));

        List<ProdutoResponseDTO> resultado = produtoService.listarProdutos();

        assertEquals(1, resultado.size());
        assertEquals("Tomate", resultado.get(0).getNome());

        verify(produtoRepository, times(1))
                .findAll();
    }

    // public List<ProdutoResponseDTO> ProdutosPreLavados()
    @Test
    void deveListarProdutosPreLavados() {

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Alface");
        produto.setTipoProduto(TipoProduto.PRE_LAVADO);

        when(produtoRepository.findByTipoProduto(TipoProduto.PRE_LAVADO))
                .thenReturn(List.of(produto));

        List<ProdutoResponseDTO> resultado = produtoService.ProdutosPreLavados();

        assertEquals(1, resultado.size());
        assertEquals("Alface", resultado.get(0).getNome());

        verify(produtoRepository).findByTipoProduto(TipoProduto.PRE_LAVADO);
    }

    // public List<ProdutoResponseDTO> ProdutosNaoLavados()
    @Test
    void deveListarProdutosNaoLavados() {

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Cenoura");
        produto.setTipoProduto(TipoProduto.NAO_LAVADO);

        when(produtoRepository.findByTipoProduto(TipoProduto.NAO_LAVADO))
                .thenReturn(List.of(produto));

        List<ProdutoResponseDTO> resultado =
                produtoService.ProdutosNaoLavados();

        assertEquals(1, resultado.size());
        assertEquals("Cenoura", resultado.get(0).getNome());

        verify(produtoRepository).findByTipoProduto(TipoProduto.NAO_LAVADO);
    }

    // public ProdutoResponseDTO atualizarProduto (Long id, ProdutoRequestDTO dto)
    @Test
    void deveAtualizarProduto() {

        Long id = 1L;

        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome("Produto antigo");

        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        dto.setNome("Produto novo");
        dto.setPreco(new BigDecimal("20.00"));
        dto.setTipoProduto(TipoProduto.PRE_LAVADO);
        dto.setTipoEmbalagem(TipoEmbalagem.BANDEJA);

        when(produtoRepository.findById(id))
                .thenReturn(Optional.of(produto));

        when(produtoRepository.save(any(Produto.class)))
                .thenReturn(produto);

        ProdutoResponseDTO resultado =
                produtoService.atualizarProduto(id, dto);

        assertEquals("Produto novo", resultado.getNome());

        verify(produtoRepository).findById(id);
        verify(produtoRepository).save(any(Produto.class));
    }

    // public void excluirProduto (Long id)
    @Test
    void deveExcluirProduto() {

        Long id = 1L;

        when(produtoRepository.existsById(id))
                .thenReturn(true);

        produtoService.excluirProduto(id);

        verify(produtoRepository).existsById(id);
        verify(produtoRepository).deleteById(id);
    }

    // public List<ProdutoResponseDTO> buscarProdutoPorNome (String nome)
    @Test
    void deveBuscarProdutoPorNome() {

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Tomate");

        when(produtoRepository.findByNomeContainingIgnoreCase("Tom"))
                .thenReturn(List.of(produto));

        List<ProdutoResponseDTO> resultado =
                produtoService.buscarProdutoPorNome("Tom");

        assertEquals(1, resultado.size());
        assertEquals("Tomate", resultado.get(0).getNome());

        verify(produtoRepository).findByNomeContainingIgnoreCase("Tom");
    }

    // public void reajustarPrecoGlobal (BigDecimal novoPreco)
    @Test
    void deveReajustarPrecoGlobal() {

        Produto produto1 = new Produto();
        produto1.setId(1L);
        produto1.setNome("Alface");
        produto1.setPreco(new BigDecimal("10.00"));
        produto1.setTipoProduto(TipoProduto.PRE_LAVADO);

        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Tomate");
        produto2.setPreco(new BigDecimal("12.00"));
        produto2.setTipoProduto(TipoProduto.PRE_LAVADO);

        List<Produto> produtos = List.of(produto1, produto2);

        when(produtoRepository.findByTipoProduto(TipoProduto.PRE_LAVADO))
                .thenReturn(produtos);

        produtoService.reajustarPrecoGlobal(new BigDecimal("20.00"));

        assertEquals(new BigDecimal("20.00"), produto1.getPreco());
        assertEquals(new BigDecimal("20.00"), produto2.getPreco());

        verify(produtoRepository).findByTipoProduto(TipoProduto.PRE_LAVADO);

        verify(produtoRepository).saveAll(produtos);
    }


    // PARTE DAS EXCEÇÕES

    // cria exceção quando tenta atualizar produto inexistente
    @Test
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {

        Long id = 99L;

        ProdutoRequestDTO dto = new ProdutoRequestDTO();

        when(produtoRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> produtoService.atualizarProduto(id, dto)
        );

        verify(produtoRepository).findById(id);
        verify(produtoRepository, never()).save(any());
    }

    // cria exceção quando preço do produto for negativo
    @Test
    void deveLancarExcecaoQuandoPrecoForNegativo() {

        assertThrows(
                RegraNegocioException.class,
                () -> produtoService.reajustarPrecoGlobal(
                        new BigDecimal("-5.00")
                )
        );

        verify(produtoRepository, never()).saveAll(any());
    }

    // cria exceççao quando tenta excluir produto inexistente
    @Test
    void deveLancarExcecaoAoExcluirProdutoInexistente() {

        Long id = 99L;

        when(produtoRepository.existsById(id))
                .thenReturn(false);

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> produtoService.excluirProduto(id)
        );

        verify(produtoRepository).existsById(id);

        verify(produtoRepository, never()).deleteById(any());
    }
}