package sptech.horticontrol.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO que recebe os dados do login no corpo da requisição (JSON)
// Usar @RequestBody com DTO é mais seguro que @RequestParam (evita expor dados na URL)

// Record é uma forma mais curta de criar uma classe
// usada apenas para transportar dados.

// O Java cria automaticamente construtor e métodos
// email() e senha().

public record LoginDto(

        /*
         * Não permite null, string vazia
         * ou formada apenas por espaços.
         */
        @NotBlank(message = "email é obrigatório")

        /*
         * Valida o formato básico do e-mail.
         */
        @Email(message = "email inválido")

        /*
         * Limita entradas excessivamente grandes.
         */
        @Size(
                max = 254,
                message = "email deve ter no máximo 254 caracteres"
        )
        String email,

        @NotBlank(message = "senha é obrigatória")

        @Size(
                max = 128,
                message = "senha deve ter no máximo 128 caracteres"
        )
        String senha

) {
}