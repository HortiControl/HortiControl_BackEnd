package sptech.horticontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.entity.Produto;
import sptech.horticontrol.repository.ProdutoRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    @Autowired
    private ProdutoRepository repository;

    @PostMapping
    public ResponseEntity criar(@RequestBody Produto produto) {
        repository.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @GetMapping
    public ResponseEntity recuperar() {
        if (repository.count() > 0) {
            List<Produto> lista = repository.findAll();
            return ResponseEntity.ok(lista);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity recuperar(@PathVariable("id") int id) {
        Optional<Produto> registro = repository.findById(id);
        if (registro.isPresent()) {
            return ResponseEntity.ok(registro.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizar(@PathVariable("id") int id, @RequestBody Produto produto) {
        if (repository.existsById(id)) {
            produto.setId(id);
            repository.save(produto);
            return ResponseEntity.ok(produto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity excluir(@PathVariable("id") int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
