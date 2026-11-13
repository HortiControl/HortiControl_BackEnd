package sptech.horticontrol.cache;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

// O PageImpl não é desserializável pelo Jackson, então o Redis guarda
// apenas o conteúdo e o total; o Page é remontado com o Pageable da requisição.
public record PaginaCache<T>(List<T> content, long totalElements) {

    public static <T> PaginaCache<T> de(Page<T> pagina) {
        return new PaginaCache<>(pagina.getContent(), pagina.getTotalElements());
    }

    public Page<T> paraPage(Pageable pageable) {
        return new PageImpl<>(content, pageable, totalElements);
    }
}
