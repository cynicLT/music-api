package org.cynic.music_api.domain.http;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public record ErrorHttp(String code, Map<String, ?> values) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @SafeVarargs
    public ErrorHttp(String code, Map.Entry<String, ?>... values) {
        this(code,
            Arrays.stream(values)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue))
        );
    }
}
