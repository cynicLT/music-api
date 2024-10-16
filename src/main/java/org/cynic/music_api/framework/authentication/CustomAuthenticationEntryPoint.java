package org.cynic.music_api.framework.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cynic.music_api.domain.http.ErrorHttp;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
        throws IOException {

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ServletOutputStream outputStream = response.getOutputStream();

        outputStream.write(
            objectMapper.writeValueAsBytes(
                new ErrorHttp(
                    "error.authentication",
                    Map.of("message", ExceptionUtils.getRootCauseMessage(exception))
                )
            )
        );
    }
}
