package com.bancofortaleza.users.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.bancofortaleza.users.domain.exceptions.ApiException;
import com.bancofortaleza.users.services.UserValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

class AdminOnlyInterceptorTest {

    private final UserValidationService userValidationService = mock(UserValidationService.class);
    private final AdminOnlyInterceptor interceptor = new AdminOnlyInterceptor(userValidationService);
    private final HttpServletResponse response = mock(HttpServletResponse.class);

    @Test
    void preHandleShouldAllowNonHandlerMethodsWithoutValidation() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verifyNoInteractions(userValidationService);
    }

    @Test
    void preHandleShouldValidateAdminWhenMethodHasAnnotation() throws NoSuchMethodException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("x-userid")).thenReturn("15");
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("adminAction"));

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertThat(result).isTrue();
        verify(userValidationService).validateAdminUser(15);
    }

    @Test
    void preHandleShouldValidateAdminWhenGeneratedUnderscoreMethodMapsToAnnotatedImplementation()
            throws NoSuchMethodException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("x-userid")).thenReturn("16");
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("_adminAction"));

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertThat(result).isTrue();
        verify(userValidationService).validateAdminUser(16);
    }

    @Test
    void preHandleShouldAllowHandlerMethodsWithoutAdminAnnotation() throws NoSuchMethodException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("publicAction"));

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertThat(result).isTrue();
        verifyNoInteractions(userValidationService);
    }

    @Test
    void preHandleShouldAllowGeneratedMethodWhenImplementationMethodDoesNotExist() throws NoSuchMethodException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HandlerMethod handlerMethod = new HandlerMethod(
                new TestController(), TestController.class.getMethod("_generatedOnlyAction"));

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertThat(result).isTrue();
        verifyNoInteractions(userValidationService);
    }

    @Test
    void preHandleShouldThrowWhenAdminHeaderIsMissing() throws NoSuchMethodException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("adminAction"));

        assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
                .isInstanceOf(ApiException.class)
                .hasMessage("Required header 'x-userid' is missing")
                .extracting("status", "code")
                .containsExactly(HttpStatus.BAD_REQUEST, "MISSING_REQUIRED_HEADER");
    }

    @Test
    void preHandleShouldThrowWhenAdminHeaderIsNotPositiveInteger() throws NoSuchMethodException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("x-userid")).thenReturn("0");
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("adminAction"));

        assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
                .isInstanceOf(ApiException.class)
                .hasMessage("Header 'x-userid' must be a positive integer")
                .extracting("status", "code")
                .containsExactly(HttpStatus.BAD_REQUEST, "INVALID_HEADER");
    }

    @Test
    void preHandleShouldThrowWhenAdminHeaderIsNotNumeric() throws NoSuchMethodException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("x-userid")).thenReturn("abc");
        HandlerMethod handlerMethod = new HandlerMethod(new TestController(), TestController.class.getMethod("adminAction"));

        assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
                .isInstanceOf(ApiException.class)
                .hasMessage("Header 'x-userid' must be a positive integer")
                .extracting("status", "code")
                .containsExactly(HttpStatus.BAD_REQUEST, "INVALID_HEADER");
    }

    static class TestController {
        @AdminOnly
        public void adminAction() {
        }

        public void _adminAction() {
        }

        public void publicAction() {
        }

        public void _generatedOnlyAction() {
        }
    }
}
