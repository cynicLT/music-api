package org.cynic.music_api.domain.http;

import java.io.Serial;
import java.io.Serializable;

public record IdHttp<T>(T id) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
