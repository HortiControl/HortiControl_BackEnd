package sptech.horticontrol.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import sptech.horticontrol.dtos.request.MercadoRequestDTO;
import sptech.horticontrol.dtos.response.MercadoResponseDTO;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.repository.MercadoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MercadoService {

    @Autowired
    private MercadoRepository mercadoRepository;

    public MercadoResponseDTO criarMercado(MercadoRequestDTO dto) {

        Mercado novoMercado = new Mercado();
        novoMercado.setNome(dto.getNome());
        novoMercado.setTipoMercado(dto.getTipoMercado());
        novoMercado.setObservacao(dto.getObservacao());

        Mercado salvo = mercadoRepository.save(novoMercado);
        return converterParaResponse(salvo);

    }

    public List<MercadoResponseDTO> listarMercados() {

        return mercadoRepository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());

    }

    public MercadoResponseDTO atualizarMercado(Long id, MercadoRequestDTO dto) {

        Mercado mercadoExistente = mercadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mercado não encontrado"));

        mercadoExistente.setNome(dto.getNome());
        mercadoExistente.setTipoMercado(dto.getTipoMercado());
        mercadoExistente.setObservacao(dto.getObservacao());

        Mercado atualizado = mercadoRepository.save(mercadoExistente);
        return converterParaResponse(atualizado);

    }

    public void excluirMercado(Long id) {

        if (!mercadoRepository.existsById(id)) {
            throw new RuntimeException("ID não existe");
        }
        mercadoRepository.deleteById(id);

    }

    private MercadoResponseDTO converterParaResponse(Mercado m) {

        return new MercadoResponseDTO(m.getId(), m.getNome(), m.getTipoMercado(), m.getObservacao());

    }

}
