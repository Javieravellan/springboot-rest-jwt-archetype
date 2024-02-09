#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.api.request;

import ${package}.domain.enums.ERole;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
public class SignUpRequest {
    @NotBlank(message = "El campo username no debe estar vacío.")
    @Size(max = 50, message = "El campo username debe tener una longitud máxima de 50 caracteres.")
    private String username;
    @NotBlank(message = "El campo password no debe estar vacío.")
    @Size(max = 20, message = "El campo password debe tener una longitud máxima de 20 caracteres.")
    private String password;
    private Set<ERole> authorities;
    @Pattern(regexp = "^[\\w-.]+@([\\w-]+.)+[\\w-]{2,4}$", message = "Email inválido.")
    @Size(max = 100, message = "El campo email debe tener una logitud máxima de 100 caracteres.")
    @NotBlank(message = "El campo email no debe estar vacío.")
    private String email;
    private boolean enabled = true;
}
