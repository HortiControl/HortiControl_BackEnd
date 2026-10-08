package sptech.horticontrol.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sptech.horticontrol.dtos.request.MercadoRequestDTO;
import sptech.horticontrol.dtos.response.MercadoResponseDTO;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.enums.TipoMercado;
import sptech.horticontrol.exceptions.RecursoNaoEncontradoException;
import sptech.horticontrol.repository.MercadoRepository;


@Service
public class MercadoService {

    private final MercadoRepository mercadoRepository;

    public MercadoService(MercadoRepository mercadoRepository) {
        this.mercadoRepository = mercadoRepository;
    }

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
        return mercadoRepository.findAll(pageable)
                .map(this::converterParaResponse);
    }

    public Page<MercadoResponseDTO> mercadosConsignados(Pageable pageable) {
        return mercadoRepository.findByTipoMercado(TipoMercado.CONSIGNADO, pageable)
                .map(this::converterParaResponse);
    }

    public Page<MercadoResponseDTO> mercadosNormais(Pageable pageable) {
        return mercadoRepository.findByTipoMercado(TipoMercado.NORMAL, pageable)
                .map(this::converterParaResponse);
    }

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

    public void excluirMercado(Long id) {
        if (!mercadoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Mercado não encontrado");
        }
        mercadoRepository.deleteById(id);
    }

    private MercadoResponseDTO converterParaResponse(Mercado m) {
        return new MercadoResponseDTO(
                m.getId(),
                m.getNome(),
                m.getTipoMercado(),
                m.getCep(),
                m.getNumero()
        );
    }
}
