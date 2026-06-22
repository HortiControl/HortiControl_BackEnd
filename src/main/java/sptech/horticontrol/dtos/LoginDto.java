package sptech.horticontrol.dtos;

// DTO que recebe os dados do login no corpo da requisição (JSON)
// Usar @RequestBody com DTO é mais seguro que @RequestParam (evita expor dados na URL)
public record LoginDto(String email, String senha) {}
