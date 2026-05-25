package sptech.horticontrol.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import sptech.horticontrol.dtos.request.MercadoRequestDTO;
import sptech.horticontrol.dtos.response.MercadoResponseDTO;
import sptech.horticontrol.dtos.response.ProdutoResponseDTO;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.enums.TipoMercado;
import sptech.horticontrol.enums.TipoProduto;
import sptech.horticontrol.exceptions.RecursoNaoEncontradoException;
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
        novoMercado.setCep(dto.getCep());
        novoMercado.setNumero(dto.getNumero());

        Mercado salvo = mercadoRepository.save(novoMercado);
        return converterParaResponse(salvo);
    }

    public List<MercadoResponseDTO> listarMercados() {
        return mercadoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public List<MercadoResponseDTO> mercadosConsignados() {
        return mercadoRepository.findByTipoMercado(TipoMercado.CONSIGNADO)
                .stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public List<MercadoResponseDTO> mercadosNormais() {
        return mercadoRepository.findByTipoMercado(TipoMercado.NORMAL)
                .stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
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
