package org.cynic.music_api.controller.advice;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.music_api.domain.ApplicationException;
import org.cynic.music_api.domain.http.ErrorHttp;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Execution(ExecutionMode.SAME_THREAD)
@Tag("unit")
class GlobalErrorAdviceTest {

    private GlobalErrorAdvice globalErrorAdvice;
    private ListAppender<ILoggingEvent> listAppender;

    @SuppressWarnings("RedundantClassCall")
    @BeforeEach
    void setUp() {

        this.globalErrorAdvice = new GlobalErrorAdvice();

        listAppender = new ListAppender<>();
        listAppender.start();

        Logger logger = Logger.class.cast(LoggerFactory.getLogger(globalErrorAdvice.getClass()));
        logger.detachAndStopAllAppenders();
        logger.addAppender(listAppender);
        logger.setLevel(Level.ERROR);
    }

    @Test
    void handleApplicationExceptionWhenOK() {
        ApplicationException exception = Instancio.create(ApplicationException.class);

        Assertions.assertThat(globalErrorAdvice.handleApplicationException(exception))
            .isEqualTo(new ErrorHttp(exception.getCode(), exception.getValues()));

        Assertions.assertThat(listAppender)
            .extracting(it -> it.list)
            .matches(it -> it.size() == 1)
            .extracting(List::getFirst)
            .matches(it -> StringUtils.EMPTY.equals(it.getMessage()))
            .matches(it -> Level.ERROR.equals(it.getLevel()))
            .extracting(ILoggingEvent::getThrowableProxy)
            .extracting("throwable")
            .asInstanceOf(InstanceOfAssertFactories.throwable(exception.getClass()))
            .isEqualTo(exception);
    }

    @Test
    void handleMissingParameterExceptionWhenOK() {
        MissingServletRequestParameterException exception = Instancio.create(
            MissingServletRequestParameterException.class);

        Assertions.assertThat(globalErrorAdvice.handleMissingParameterException(exception))
            .isEqualTo(
                new ErrorHttp(
                    "error.parameter.missing",
                    Map.entry("name", exception.getParameterName()),
                    Map.entry("type", exception.getParameterType())
                )
            );

        Assertions.assertThat(listAppender)
            .extracting(it -> it.list)
            .matches(it -> it.size() == 1)
            .extracting(List::getFirst)
            .matches(it -> StringUtils.EMPTY.equals(it.getMessage()))
            .matches(it -> Level.ERROR.equals(it.getLevel()))
            .extracting(ILoggingEvent::getThrowableProxy)
            .extracting("throwable")
            .asInstanceOf(InstanceOfAssertFactories.throwable(exception.getClass()))
            .isEqualTo(exception);
    }

    @Test
    void handleTypeExceptionWhenOK() {
        MethodArgumentTypeMismatchException exception = Instancio.create(MethodArgumentTypeMismatchException.class);

        Assertions.assertThat(globalErrorAdvice.handleTypeException(exception))
            .isEqualTo(
                new ErrorHttp(
                    "error.parameter.invalid-type",
                    Map.entry("name", exception.getName()),
                    Map.entry("value", Objects.toString(exception.getValue(), StringUtils.EMPTY)),
                    Map.entry("type", ClassUtils.getSimpleName(exception.getRequiredType()))
                )
            );

        Assertions.assertThat(listAppender)
            .extracting(it -> it.list)
            .matches(it -> it.size() == 1)
            .extracting(List::getFirst)
            .matches(it -> StringUtils.EMPTY.equals(it.getMessage()))
            .matches(it -> Level.ERROR.equals(it.getLevel()))
            .extracting(ILoggingEvent::getThrowableProxy)
            .extracting("throwable")
            .asInstanceOf(InstanceOfAssertFactories.throwable(exception.getClass()))
            .isEqualTo(exception);
    }


    @Test
    void handleArgumentConversionExceptionWhenOK() {
        MethodArgumentConversionNotSupportedException exception = Instancio.create(
            MethodArgumentConversionNotSupportedException.class);

        Assertions.assertThat(globalErrorAdvice.handleArgumentConversionException(exception))
            .isEqualTo(
                new ErrorHttp(
                    "error.parameter.non-convertable",
                    Map.entry("name", exception.getName()),
                    Map.entry("value", Objects.toString(exception.getValue(), StringUtils.EMPTY)),
                    Map.entry("type", ClassUtils.getSimpleName(exception.getRequiredType()))
                )
            );

        Assertions.assertThat(listAppender)
            .extracting(it -> it.list)
            .matches(it -> it.size() == 1)
            .extracting(List::getFirst)
            .matches(it -> StringUtils.EMPTY.equals(it.getMessage()))
            .matches(it -> Level.ERROR.equals(it.getLevel()))
            .extracting(ILoggingEvent::getThrowableProxy)
            .extracting("throwable")
            .asInstanceOf(InstanceOfAssertFactories.throwable(exception.getClass()))
            .isEqualTo(exception);
    }

    @Test
    void handleHttpMethodNotSupportedExceptionWhenOK() {
        HttpRequestMethodNotSupportedException exception = Instancio.create(
            HttpRequestMethodNotSupportedException.class);

        Assertions.assertThat(globalErrorAdvice.handleHttpMethodNotSupportedException(exception))
            .isEqualTo(
                new ErrorHttp(
                    "error.http-method.not-supported",
                    Map.entry("method", exception.getMethod())
                )
            );

        Assertions.assertThat(listAppender)
            .extracting(it -> it.list)
            .matches(it -> it.size() == 1)
            .extracting(List::getFirst)
            .matches(it -> StringUtils.EMPTY.equals(it.getMessage()))
            .matches(it -> Level.ERROR.equals(it.getLevel()))
            .extracting(ILoggingEvent::getThrowableProxy)
            .extracting("throwable")
            .asInstanceOf(InstanceOfAssertFactories.throwable(exception.getClass()))
            .isEqualTo(exception);
    }

    @Test
    void handleDataAccessExceptionWhenOK() {
        DataAccessException exception = Instancio.create(BadSqlGrammarException.class);

        Assertions.assertThat(globalErrorAdvice.handleDataAccessException(exception))
            .isEqualTo(
                new ErrorHttp(
                    "error.database", Map.entry("message", ExceptionUtils.getRootCauseMessage(exception))
                )
            );

        Assertions.assertThat(listAppender)
            .extracting(it -> it.list)
            .matches(it -> it.size() == 1)
            .extracting(List::getFirst)
            .matches(it -> StringUtils.EMPTY.equals(it.getMessage()))
            .matches(it -> Level.ERROR.equals(it.getLevel()))
            .extracting(ILoggingEvent::getThrowableProxy)
            .extracting("throwable")
            .asInstanceOf(InstanceOfAssertFactories.throwable(exception.getClass()))
            .isEqualTo(exception);
    }

    @Test
    void handleUnknownExceptionWhenOK() {
        Exception exception = new Exception();

        Assertions.assertThat(globalErrorAdvice.handleUnknownException(exception))
            .isEqualTo(
                new ErrorHttp(
                    "error.unknown",
                    Map.entry("message", ExceptionUtils.getRootCauseMessage(exception)),
                    Map.entry("stack", ExceptionUtils.getStackTrace(exception))
                )
            );

        Assertions.assertThat(listAppender)
            .extracting(it -> it.list)
            .matches(it -> it.size() == 1)
            .extracting(List::getFirst)
            .matches(it -> StringUtils.EMPTY.equals(it.getMessage()))
            .matches(it -> Level.ERROR.equals(it.getLevel()))
            .extracting(ILoggingEvent::getThrowableProxy)
            .extracting("throwable")
            .asInstanceOf(InstanceOfAssertFactories.throwable(exception.getClass()))
            .isEqualTo(exception);
    }

    @Test
    void handleAccessDeniedExceptionWhenAuthenticated() {
        OidcUser oidcUser =
            new DefaultOidcUser(
                Instancio.createList(SimpleGrantedAuthority.class),
                new OidcIdToken(
                    Instancio.create(String.class),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().past())
                        .create(),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().future())
                        .create(),
                    Map.of("sub", "subject",
                        "email", "email",
                        "iss", "http://localhost.com")
                )
            );

        AccessDeniedException exception = Instancio.create(AccessDeniedException.class);

        Assertions.assertThat(globalErrorAdvice.handleAccessDeniedException(oidcUser, exception))
            .matches(it -> HttpStatus.FORBIDDEN.isSameCodeAs(it.getStatusCode()))
            .extracting(HttpEntity::getBody)
            .isEqualTo(
                new ErrorHttp("error.access.forbidden",
                    Map.entry("subject", oidcUser.getSubject()),
                    Map.entry("issuer", oidcUser.getIssuer()),
                    Map.entry("roles", oidcUser.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","))
                    )
                )
            );
        Assertions.assertThat(listAppender)
            .extracting(it -> it.list)
            .matches(it -> it.size() == 1)
            .extracting(List::getFirst)
            .matches(it -> StringUtils.EMPTY.equals(it.getMessage()))
            .matches(it -> Level.ERROR.equals(it.getLevel()))
            .extracting(ILoggingEvent::getThrowableProxy)
            .extracting("throwable")
            .asInstanceOf(InstanceOfAssertFactories.throwable(exception.getClass()))
            .isEqualTo(exception);
    }


    @Test
    void handleAccessDeniedExceptionWhenUnAuthenticated() {
        AccessDeniedException exception = Instancio.create(AccessDeniedException.class);

        Assertions.assertThat(globalErrorAdvice.handleAccessDeniedException(null, exception))
            .matches(it -> HttpStatus.UNAUTHORIZED.isSameCodeAs(it.getStatusCode()))
            .extracting(HttpEntity::getBody)
            .isEqualTo(
                new ErrorHttp("error.access.denied")
            );

        Assertions.assertThat(listAppender)
            .extracting(it -> it.list)
            .matches(it -> it.size() == 1)
            .extracting(List::getFirst)
            .matches(it -> StringUtils.EMPTY.equals(it.getMessage()))
            .matches(it -> Level.ERROR.equals(it.getLevel()))
            .extracting(ILoggingEvent::getThrowableProxy)
            .extracting("throwable")
            .asInstanceOf(InstanceOfAssertFactories.throwable(exception.getClass()))
            .isEqualTo(exception);
    }
}