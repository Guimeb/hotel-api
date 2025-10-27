package com.sishotel.hotel_api.dto; // PACOTE AJUSTADO

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GuestRequestDTO {

    @NotBlank(message = "O nome completo é obrigatório")
    @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
    private String fullName;

    @NotBlank(message = "O documento é obrigatório")
    @Size(max = 30)
    private String document;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    @Size(max = 120)
    private String email;

    @Size(max = 30, message = "O telefone deve ter no máximo 30 caracteres")
    private String phone;
}