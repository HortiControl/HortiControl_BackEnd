package sptech.horticontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // public Page<MercadoResponseDTO> listarMercados(Pageable pageable)
    @Test
    void deveListarMercados() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);
        mercado.setNome("Mercado MJ4");

        Pageable pageable = PageRequest.of(0, 10);
        when(mercadoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(mercado)));

        Page<MercadoResponseDTO> resultado = mercadoService.listarMercados(pageable);

        assertEquals(1, resultado.getContent().size());
        assertEquals("Mercado MJ4", resultado.getContent().getFirst().getNome());

        verify(mercadoRepository, times(1)).findAll(pageable);
    }

    // public Page<MercadoResponseDTO> mercadosConsignados(Pageable pageable)
    @Test
    void deveListarMercadosConsignados() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);
        mercado.setNome("Mercado Consignado");
        mercado.setTipoMercado(TipoMercado.CONSIGNADO);

        Pageable pageable = PageRequest.of(0, 10);
        when(mercadoRepository.findByTipoMercado(TipoMercado.CONSIGNADO, pageable))
                .thenReturn(new PageImpl<>(List.of(mercado)));

        Page<MercadoResponseDTO> resultado = mercadoService.mercadosConsignados(pageable);

        assertEquals(1, resultado.getContent().size());
        assertEquals(TipoMercado.CONSIGNADO, resultado.getContent().getFirst().getTipoMercado());

        verify(mercadoRepository).findByTipoMercado(TipoMercado.CONSIGNADO, pageable);
    }

    // public Page<MercadoResponseDTO> mercadosNormais(Pageable pageable)
    @Test
    void deveListarMercadosNormais() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);
        mercado.setNome("Mercado Normal");
        mercado.setTipoMercado(TipoMercado.NORMAL);

        Pageable pageable = PageRequest.of(0, 10);
        when(mercadoRepository.findByTipoMercado(TipoMercado.NORMAL, pageable))
                .thenReturn(new PageImpl<>(List.of(mercado)));

        Page<MercadoResponseDTO> resultado = mercadoService.mercadosNormais(pageable);

        assertEquals(1, resultado.getContent().size());
        assertEquals(TipoMercado.NORMAL, resultado.getContent().getFirst().getTipoMercado());

        verify(mercadoRepository).findByTipoMercado(TipoMercado.NORMAL, pageable);
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