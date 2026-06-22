package sptech.horticontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sptech.horticontrol.dtos.request.MercadoRequestDTO;
import sptech.horticontrol.dtos.response.MercadoResponseDTO;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.enums.TipoMercado;
import sptech.horticontrol.exceptions.RecursoNaoEncontradoException;
import sptech.horticontrol.repository.MercadoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MercadoServiceTest {

    @Mock
    private MercadoRepository mercadoRepository;

    @InjectMocks
    private MercadoService mercadoService;

    // public MercadoResponseDTO criarMercado(MercadoRequestDTO dto)
    @Test
    void deveCriarMercado() {

        MercadoRequestDTO dto = new MercadoRequestDTO();
        dto.setNome("Mercado MJ4");
        dto.setTipoMercado(TipoMercado.NORMAL);
        dto.setCep("01234-567");
        dto.setNumero("100");

        Mercado mercadoSalvo = new Mercado();
        mercadoSalvo.setId(1L);
        mercadoSalvo.setNome("Mercado MJ4");
        mercadoSalvo.setTipoMercado(TipoMercado.NORMAL);
        mercadoSalvo.setCep("01234-567");
        mercadoSalvo.setNumero("100");

        when(mercadoRepository.save(any(Mercado.class)))
                .thenReturn(mercadoSalvo);

        MercadoResponseDTO resultado = mercadoService.criarMercado(dto);

        assertNotNull(resultado);
        assertEquals("Mercado MJ4", resultado.getNome());
        assertEquals(TipoMercado.NORMAL, resultado.getTipoMercado());

        verify(mercadoRepository, times(1)).save(any(Mercado.class));
    }

    // public List<MercadoResponseDTO> listarMercados()
    @Test
    void deveListarMercados() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);
        mercado.setNome("Mercado MJ4");

        when(mercadoRepository.findAll()).thenReturn(List.of(mercado));

        List<MercadoResponseDTO> resultado = mercadoService.listarMercados();

        assertEquals(1, resultado.size());
        assertEquals("Mercado MJ4", resultado.getFirst().getNome());

        verify(mercadoRepository, times(1)).findAll();
    }

    // public List<MercadoResponseDTO> mercadosConsignados()
    @Test
    void deveListarMercadosConsignados() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);
        mercado.setNome("Mercado Consignado");
        mercado.setTipoMercado(TipoMercado.CONSIGNADO);

        when(mercadoRepository.findByTipoMercado(TipoMercado.CONSIGNADO))
                .thenReturn(List.of(mercado));

        List<MercadoResponseDTO> resultado = mercadoService.mercadosConsignados();

        assertEquals(1, resultado.size());
        assertEquals(TipoMercado.CONSIGNADO, resultado.getFirst().getTipoMercado());

        verify(mercadoRepository).findByTipoMercado(TipoMercado.CONSIGNADO);
    }

    // public List<MercadoResponseDTO> mercadosNormais()
    @Test
    void deveListarMercadosNormais() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);
        mercado.setNome("Mercado Normal");
        mercado.setTipoMercado(TipoMercado.NORMAL);

        when(mercadoRepository.findByTipoMercado(TipoMercado.NORMAL))
                .thenReturn(List.of(mercado));

        List<MercadoResponseDTO> resultado = mercadoService.mercadosNormais();

        assertEquals(1, resultado.size());
        assertEquals(TipoMercado.NORMAL, resultado.getFirst().getTipoMercado());

        verify(mercadoRepository).findByTipoMercado(TipoMercado.NORMAL);
    }

    // public MercadoResponseDTO atualizarMercado(Long id, MercadoRequestDTO dto)
    @Test
    void deveAtualizarMercado() {

        Long id = 1L;

        Mercado mercado = new Mercado();
        mercado.setId(id);
        mercado.setNome("Nome Antigo");

        MercadoRequestDTO dto = new MercadoRequestDTO();
        dto.setNome("Nome Atualizado");
        dto.setTipoMercado(TipoMercado.CONSIGNADO);

        when(mercadoRepository.findById(id))
                .thenReturn(Optional.of(mercado));

        when(mercadoRepository.save(any(Mercado.class)))
                .thenReturn(mercado);

        MercadoResponseDTO resultado = mercadoService.atualizarMercado(id, dto);

        assertEquals("Nome Atualizado", resultado.getNome());

        verify(mercadoRepository).findById(id);
        verify(mercadoRepository).save(any(Mercado.class));
    }

    // public void excluirMercado(Long id)
    @Test
    void deveExcluirMercado() {

        Long id = 1L;

        when(mercadoRepository.existsById(id))
                .thenReturn(true);

        mercadoService.excluirMercado(id);

        verify(mercadoRepository).existsById(id);
        verify(mercadoRepository).deleteById(id);
    }


    // PARTE DAS EXCEÇÕES

    // cria exceção quando tenta atualizar mercado inexistente
    @Test
    void deveLancarExcecaoAoAtualizarMercadoInexistente() {

        Long id = 99L;
        MercadoRequestDTO dto = new MercadoRequestDTO();

        when(mercadoRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> mercadoService.atualizarMercado(id, dto)
        );

        verify(mercadoRepository).findById(id);
        verify(mercadoRepository, never()).save(any());
    }

    // cria exceção quando tenta excluir mercado inexistente
    @Test
    void deveLancarExcecaoAoExcluirMercadoInexistente() {

        Long id = 99L;

        when(mercadoRepository.existsById(id))
                .thenReturn(false);

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> mercadoService.excluirMercado(id)
        );

        verify(mercadoRepository).existsById(id);
        verify(mercadoRepository, never()).deleteById(any());
    }
}