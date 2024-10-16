package org.cynic.music_api.framework.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cynic.music_api.domain.ApplicationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class CustomResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    protected void handleError(ClientHttpResponse response, HttpStatusCode statusCode) throws IOException {
        throw new ApplicationException(
            "error.http.bad-response",
            Map.entry("status", statusCode),
            Map.entry("body", IOUtils.toString(response.getBody(), StandardCharsets.UTF_8))
        );
    }
}
