#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.exception;

import ${package}.api.response.ApiError;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;

@ControllerAdvice
@Log4j2
public class GlobalApiExceptionHandler {

    @ExceptionHandler({
            Exception.class,
            GenericException.class,
            AccessDeniedException.class,
            MethodArgumentNotValidException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest webRequest) {
        HttpHeaders headers = new HttpHeaders();
        log.error("Error", ex);
        if(ex instanceof UsernameAlreadyExistsException uaeEx) {
            return handleUsernameAlreadyExistsException(uaeEx, headers, uaeEx.getStatus(), webRequest);
        }
        else if(ex instanceof InternalAuthenticationServiceException nfEx) {
            return handleUserNotFoundException(nfEx, headers, HttpStatus.NOT_FOUND, webRequest);
        }
        else if (ex instanceof AccessDeniedException aex) {
            var apiError = new ApiError(HttpStatus.FORBIDDEN, aex.getMessage(), 403);
            return handleExceptionInternal(aex, apiError, headers, apiError.getStatusText(), webRequest);
        }
        else if (ex instanceof BadRequestException bre) {
            var apiError = new ApiError(bre.getStatus(), bre.getMessage(), 400);
            return handleExceptionInternal(bre, apiError, headers, apiError.getStatusText(), webRequest);
        }
        else if(ex instanceof MethodArgumentNotValidException me) {
            return this.handleMethodArgumentNotValidException(me, headers, webRequest);
        }
        else if (ex instanceof GenericException ge) {
            return this.handleGenericException(ge, headers, webRequest);
        }
        else if (ex instanceof HttpRequestMethodNotSupportedException exNotSupported) {
            return this.handleMethodNotAllowedException(exNotSupported, headers, webRequest);
        }
        else {
            var error = ApiError.builder()
                    .statusCode(500)
                    .statusText(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(ex.getMessage())
                    .build();
            return handleExceptionInternal(ex, error, headers, error.getStatusText(), webRequest);
        }
    }

    public ResponseEntity<ApiError> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        return handleExceptionInternal(ex, new ApiError(ex.getStatus(), ex.getMessage(), status.value()), headers, status, webRequest);
    }

    public ResponseEntity<ApiError> handleUserNotFoundException(InternalAuthenticationServiceException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        var cause = (NotFoundException) ex.getCause();
        var apiError = new ApiError(cause.getStatusCode(), ex.getMessage(), status.value());
        return handleExceptionInternal(ex, apiError, headers, status, webRequest);
    }

    public ResponseEntity<ApiError> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, WebRequest webRequest) {
        var httpStatus = HttpStatus.valueOf(ex.getStatusCode().value());
        var message = String.format("Método '%s' no soportado. Solo se admiten los métodos: '%s'.", ex.getMethod(), ex.getSupportedHttpMethods());
        var error = ApiError.builder()
                .message(message)
                .statusCode(httpStatus.value())
                .statusText(httpStatus)
                .build();

        return handleExceptionInternal(ex, error, headers, httpStatus, webRequest);
    }

    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpHeaders headers, WebRequest webRequest) {
        var errors = ApiError.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Error de validación de campos")
                .statusText(HttpStatus.BAD_REQUEST)
                .fieldErrors(new ArrayList<>())
                .build();
        setFieldErrorsIntoApiErrorObject(errors, ex);
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, webRequest);
    }

    public ResponseEntity<ApiError> handleGenericException(GenericException ex, HttpHeaders headers, WebRequest webRequest) {
        var apiError = ApiError.builder()
                .statusCode(ex.getStatus().value())
                .message(ex.getMessage())
                .statusText(ex.getStatus())
                .build();

        return handleExceptionInternal(ex, apiError, headers, ex.getStatus(), webRequest);
    }

    protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(body, headers, status);
    }

    private void setFieldErrorsIntoApiErrorObject(ApiError apiErrorObject, MethodArgumentNotValidException ex) {
        ex.getAllErrors().forEach(objectError -> apiErrorObject.getFieldErrors().add(objectError.getDefaultMessage()));
    }
}
