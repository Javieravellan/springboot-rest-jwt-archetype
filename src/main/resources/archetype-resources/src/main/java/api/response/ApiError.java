#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private HttpStatus statusText;
    private String message;
    private int statusCode;
    @Builder.Default
    private List<String> fieldErrors = new ArrayList<>();

    public ApiError(HttpStatus statusText, String message, int statusCode) {
        this.statusText = statusText;
        this.message = message;
        this.statusCode = statusCode;
    }
}
