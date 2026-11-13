package sptech.horticontrol.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sptech.horticontrol.dto.request.MercadoRequestDTO;
import sptech.horticontrol.dto.response.MercadoResponseDTO;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.exception.RecursoNaoEncontradoException;
import sptech.horticontrol.repository.MercadoRepository;


@Service
public class MercadoService {

    private final MercadoRepository mercadoRepository;
    private final MercadoLeituraCache mercadoLeituraCache;

    public MercadoService(MercadoRepository mercadoRepository, MercadoLeituraCache mercadoLeituraCache) {
        this.mercadoRepository = mercadoRepository;
        this.mercadoLeituraCache = mercadoLeituraCache;
    }

    @CacheEvict(cacheNames = MercadoLeituraCache.CACHE, allEntries = true)
    public MercadoResponseDTO criarMercado(MercadoRequestDTO dto) {

        Mercado novoMercado = new Mercado();
        novoMercado.setNome(dto.getNome());
        novoMercado.setTipoMercado(dto.getTipoMercado());
        novoMercado.setCep(dto.getCep());
        novoMercado.setNumero(dto.getNumero());

        Mercado salvo = mercadoRepository.save(novoMercado);
        return converterParaResponse(salvo);
    }

    public Page<MercadoResponseDTO> listarMercados(Pageable pageable) {
        return mercadoLeituraCache.listarTodos(pageable).paraPage(pageable);
    }

    public Page<MercadoResponseDTO> mercadosConsignados(Pageable pageable) {
        return mercadoLeituraCache.listarConsignados(pageable).paraPage(pageable);
    }

    public Page<MercadoResponseDTO> mercadosNormais(Pageable pageable) {
        return mercadoLeituraCache.listarNormais(pageable).paraPage(pageable);
    }

    @CacheEvict(cacheNames = MercadoLeituraCache.CACHE, allEntries = true)
    public MercadoResponseDTO atualizarMercado(Long id, MercadoRequestDTO dto) {

        Mercado mercadoExistente = mercadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mercado não encontrado"));

        mercadoExistente.setNome(dto.getNome());
        mercadoExistente.setTipoMercado(dto.getTipoMercado());
        mercadoExistente.setCep(dto.getCep());
        mercadoExistente.setNumero(dto.getNumero());

        Mercado atualizado = mercadoRepository.save(mercadoExistente);
        return converterParaResponse(atualizado);
    }

    @CacheEvict(cacheNames = MercadoLeituraCache.CACHE, allEntries = true)
    public void excluirMercado(Long id) {
        if (!mercadoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Mercado não encontrado");
        }
        mercadoRepository.deleteById(id);
    }

    static MercadoResponseDTO converterParaResponse(Mercado m) {
        return new MercadoResponseDTO(
                m.getId(),
                m.getNome(),
                m.getTipoMercado(),
                m.getCep(),
                m.getNumero()
        );
    }
}
