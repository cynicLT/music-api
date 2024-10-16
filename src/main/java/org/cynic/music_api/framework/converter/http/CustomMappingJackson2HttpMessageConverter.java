package org.cynic.music_api.framework.converter.http;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import java.util.List;

public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public CustomMappingJackson2HttpMessageConverter() {
        setSupportedMediaTypes(List.of(MediaType.ALL));
    }
}
