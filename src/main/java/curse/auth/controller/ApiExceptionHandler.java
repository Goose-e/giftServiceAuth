package curse.auth.controller;

import curse.auth.dto.common.ErrorResponseDTO;
import curse.auth.httpResponse.DefaultHttpResponseBody;
import curse.auth.httpResponse.HttpResponseBody;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public HttpResponseBody<ErrorResponseDTO> handleBadRequest(IllegalArgumentException ex) {
        DefaultHttpResponseBody<ErrorResponseDTO> response = new DefaultHttpResponseBody<>();
        response.setResponseCode("BAD_REQUEST");
        response.setMessage(ex.getMessage());
        response.setError(ex.getMessage());
        response.setResponseEntity(new ErrorResponseDTO(ex.getMessage()));
        return response;
    }

    @ExceptionHandler(JwtException.class)
    public HttpResponseBody<ErrorResponseDTO> handleJwt(JwtException ex) {
        DefaultHttpResponseBody<ErrorResponseDTO> response = new DefaultHttpResponseBody<>();
        response.setResponseCode("UNAUTHORIZED");
        response.setMessage(ex.getMessage());
        response.setError(ex.getMessage());
        response.setResponseEntity(new ErrorResponseDTO(ex.getMessage()));
        return response;
    }
}
