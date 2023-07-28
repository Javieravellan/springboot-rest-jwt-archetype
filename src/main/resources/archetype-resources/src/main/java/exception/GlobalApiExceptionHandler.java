#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.exception;

import ${package}.api.response.ApiError;
import ${package}.exception.integration.AldeamoResponseException;
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

    @ExceptionHandler({GenericException.class, AldeamoResponseException.class, AccessDeniedException.class, MethodArgumentNotValidException.class})
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest webRequest) {
        HttpHeaders headers = new HttpHeaders();
        if(ex instanceof UsernameAlreadyExistsException uaeEx) {
            return handleUsernameAlreadyExistsException(uaeEx, headers, uaeEx.getStatus(), webRequest);
        } else if(ex instanceof InternalAuthenticationServiceException nfEx) {
            return handleUserNotFoundException(nfEx, headers, HttpStatus.NOT_FOUND, webRequest);
        } else if(ex instanceof AldeamoResponseException aldeamoEx)
            return handleAldeamoResponseException(aldeamoEx, headers, aldeamoEx.getErrorResponse().getStatusCode(), webRequest);
        else if (ex instanceof AccessDeniedException aex) {
            var apiError = new ApiError(HttpStatus.FORBIDDEN, aex.getMessage(), 403);
            return handleExceptionInternal(aex, apiError, headers, apiError.getStatusText(), webRequest);
        } else if (ex instanceof BadRequestException bre) {
            var apiError = new ApiError(bre.getStatus(), bre.getMessage(), 400);
            return handleExceptionInternal(bre, apiError, headers, apiError.getStatusText(), webRequest);
        } else if(ex instanceof MethodArgumentNotValidException me) {
            var errors = new ApiError();
            errors.setStatusText(HttpStatus.BAD_REQUEST);
            errors.setStatusCode(400);
            errors.setMessage("Error de validación de campos");
            setFieldErrorsIntoApiErrorObject(errors, me);
            return handleExceptionInternal(me, errors, headers, HttpStatus.BAD_REQUEST, webRequest);
        }
        else {
            var error = new ApiError();
            error.setStatusText(HttpStatus.INTERNAL_SERVER_ERROR);
            error.setStatusCode(500);
            return handleExceptionInternal(ex, error, headers, error.getStatusText(), webRequest);
        }
    }

    public ResponseEntity<ApiError> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        return handleExceptionInternal(ex, new ApiError(ex.getStatus(), ex.getMessage(), status.value()), headers, status, webRequest);
    }

    public ResponseEntity<ApiError> handleAldeamoResponseException(AldeamoResponseException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        var errorResponse = ex.getErrorResponse();
        var apiError = new ApiError(errorResponse.getStatusCode(), errorResponse.getErrors(), status.value());

        return handleExceptionInternal(ex, apiError, headers, apiError.getStatusText(), webRequest);
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
