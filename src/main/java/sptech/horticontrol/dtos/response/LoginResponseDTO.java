package sptech.horticontrol.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginResponseDTO {

    @Schema(description = "Token do usuário")
    private String token;

    @Schema(description = "ID do Usuário", example = "1")
    private Long idUsuario;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, Long idUsuario) {
        this.token = token;
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
}
