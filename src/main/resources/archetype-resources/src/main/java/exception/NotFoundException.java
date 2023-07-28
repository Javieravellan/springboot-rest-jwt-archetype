#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotFoundException extends GenericException {

    private String message;
    private HttpStatus statusCode;

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
        this.message = message;
        this.statusCode = HttpStatus.NOT_FOUND;
    }

}
