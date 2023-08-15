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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

@ControllerAdvice
@Log4j2
public class GlobalApiExceptionHandler {

    @ExceptionHandler({
            Exception.class,
            GenericException.class,
            AccessDeniedException.class,
            MethodArgumentNotValidException.class,
    })
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest webRequest) {
        HttpHeaders headers = new HttpHeaders();
        log.debug("Error", ex);
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
            var errors = ApiError.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Error de validación de campos")
                    .statusText(HttpStatus.BAD_REQUEST)
                    .build();
            setFieldErrorsIntoApiErrorObject(errors, me);
            return handleExceptionInternal(me, errors, headers, HttpStatus.BAD_REQUEST, webRequest);
        }
        else if (ex instanceof GenericException ge) {
            var apiError = ApiError.builder()
                    .statusCode(ge.getStatus().value())
                    .message(ge.getMessage())
                    .statusText(ge.getStatus())
                    .build();

            return handleExceptionInternal(ge, apiError, headers, ge.getStatus(), webRequest);
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
