package sptech.horticontrol.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.entity.Produto;
import sptech.horticontrol.repository.ProdutoRepository;

import java.util.List;

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
}
