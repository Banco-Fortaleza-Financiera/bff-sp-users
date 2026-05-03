package com.bancofortaleza.users.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.domain.exceptions.ApiException;
import com.bancofortaleza.users.domain.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleApiExceptionShouldPreserveStatusCodeAndMessage() {
        MockHttpServletRequest request = request("/users/1");
        ApiException exception = new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found");

        ResponseEntity<ErrorResponse> result = handler.handleApiException(exception, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().code()).isEqualTo("USER_NOT_FOUND");
        assertThat(result.getBody().path()).isEqualTo("/users/1");
    }

    @Test
    void handleValidationShouldReturnFieldDetailsWithPublicHeaderNames() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "xUserid", "must not be null"));
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("methodWithParameter", Object.class), 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> result = handler.handleValidation(exception, request("/users"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(result.getBody().details()).containsExactly(
                new ErrorResponse.FieldError("x-userid", "must not be null"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleConstraintViolationShouldSanitizeNestedFieldNames() {
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("listUsers.xSession");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> result = handler.handleConstraintViolation(exception, request("/users"));

        assertThat(result.getBody().details()).containsExactly(
                new ErrorResponse.FieldError("x-session", "must not be blank"));
    }

    @Test
    void handleBadRequestShouldReturnInvalidRequestForUnreadableMessages() {
        ResponseEntity<ErrorResponse> result = handler.handleBadRequest(
                new HttpMessageNotReadableException("bad"), request("/users"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().code()).isEqualTo("BAD_REQUEST");
        assertThat(result.getBody().message()).isEqualTo("Invalid request");
    }

    @Test
    void handleBadRequestShouldReturnInvalidRequestForTypeMismatch() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("methodWithParameter", Object.class), 0);
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "abc", Integer.class, "id", parameter, new TypeMismatchException("abc", Integer.class));

        ResponseEntity<ErrorResponse> result = handler.handleBadRequest(exception, request("/users/abc"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().code()).isEqualTo("BAD_REQUEST");
    }

    @Test
    void handleMissingRequestParameterShouldIncludeParameterDetail() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("x-page", "Integer");

        ResponseEntity<ErrorResponse> result = handler.handleMissingRequestParameter(exception, request("/users"));

        assertThat(result.getBody().code()).isEqualTo("MISSING_REQUIRED_PARAMETER");
        assertThat(result.getBody().details()).containsExactly(
                new ErrorResponse.FieldError("x-page", "Parameter is required"));
    }

    @Test
    void handleMissingRequestHeaderShouldIncludeHeaderDetail() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("methodWithParameter", Object.class), 0);
        MissingRequestHeaderException exception = new MissingRequestHeaderException("x-session", parameter);

        ResponseEntity<ErrorResponse> result = handler.handleMissingRequestHeader(exception, request("/users"));

        assertThat(result.getBody().code()).isEqualTo("MISSING_REQUIRED_HEADER");
        assertThat(result.getBody().details()).containsExactly(
                new ErrorResponse.FieldError("x-session", "Header is required"));
    }

    @Test
    void handleNotFoundShouldReturnNotFoundForMissingHandlerAndResource() throws Exception {
        ResponseEntity<ErrorResponse> noHandler = handler.handleNotFound(
                new NoHandlerFoundException("GET", "/missing", null), request("/missing"));
        ResponseEntity<ErrorResponse> noResource = handler.handleNotFound(
                new NoResourceFoundException(HttpMethod.GET, "/missing"), request("/missing"));

        assertThat(noHandler.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(noResource.getBody().code()).isEqualTo("NOT_FOUND");
    }

    @Test
    void handleMethodNotAllowedShouldReturnMethodNotAllowed() {
        ResponseEntity<ErrorResponse> result = handler.handleMethodNotAllowed(
                new HttpRequestMethodNotSupportedException("PATCH"), request("/users"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(result.getBody().code()).isEqualTo("METHOD_NOT_ALLOWED");
    }

    @Test
    void handleUnexpectedShouldReturnInternalServerError() {
        ResponseEntity<ErrorResponse> result = handler.handleUnexpected(new RuntimeException("boom"), request("/users"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody().code()).isEqualTo("INTERNAL_SERVER_ERROR");
    }

    @SuppressWarnings("unused")
    private void methodWithParameter(Object parameter) {
    }

    private MockHttpServletRequest request(String uri) {
        return new MockHttpServletRequest("GET", uri);
    }
}
