package org.cynic.music_api.service.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.music_api.Constants;
import org.cynic.music_api.Constants.ItunesJsonFieldPointer;
import org.cynic.music_api.Constants.WrapperType;
import org.cynic.music_api.domain.ApplicationException;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class ItunesProxyServiceTest {

    private ItunesProxyService itunesProxyService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectNode body;

    @Mock
    private ArrayNode array;

    @Mock
    private JsonNode item;


    @BeforeEach
    void setUp() {
        this.itunesProxyService = new ItunesProxyService(() -> restTemplate);
    }

    @Test
    void findArtistTopAlbumsWhenOk() {
        String artistRefId = Instancio.create(String.class);
        Optional<Integer> limit = Instancio.create(new TypeToken<>() {
        });
        ResponseEntity<ObjectNode> responseEntity = ResponseEntity.ok(body);

        Mockito.when(restTemplate.getForEntity(
                "/lookup?entity=album&amgArtistId={artistRefId}&limit={limit}",
                ObjectNode.class,
                artistRefId,
                limit.orElse(Constants.DEFAULT_TOP_LIMIT)
            ))
            .thenReturn(responseEntity);
        Mockito.when(body.withArray(ItunesJsonFieldPointer.RESULT.fieldName()))
            .thenReturn(array);
        Mockito.when(array.iterator()).thenReturn(List.of(item).iterator());
        Mockito.when(item.at(ItunesJsonFieldPointer.WRAPPER.fieldName()))
            .thenReturn(item);
        Mockito.when(item.textValue()).thenReturn(WrapperType.ALBUM.propertyValue());

        Assertions.assertThat(itunesProxyService.findArtistTopAlbums(artistRefId, limit))
            .containsOnly(item);

        Mockito.verify(restTemplate).getForEntity(
            "/lookup?entity=album&amgArtistId={artistRefId}&limit={limit}",
            ObjectNode.class,
            artistRefId,
            limit.orElse(Constants.DEFAULT_TOP_LIMIT)
        );
        Mockito.verify(body).withArray(ItunesJsonFieldPointer.RESULT.fieldName());
        Mockito.verify(array).iterator();
        Mockito.verify(item).at(ItunesJsonFieldPointer.WRAPPER.fieldName());
        Mockito.verify(item).textValue();

        Mockito.verifyNoMoreInteractions(restTemplate, body, array, item);
    }

    @Test
    void findArtistTopAlbumsWhenOkFiltered() {
        String artistRefId = Instancio.create(String.class);
        Optional<Integer> limit = Instancio.create(new TypeToken<>() {
        });
        ResponseEntity<ObjectNode> responseEntity = ResponseEntity.ok(body);

        Mockito.when(restTemplate.getForEntity(
                "/lookup?entity=album&amgArtistId={artistRefId}&limit={limit}",
                ObjectNode.class,
                artistRefId,
                limit.orElse(Constants.DEFAULT_TOP_LIMIT)
            ))
            .thenReturn(responseEntity);
        Mockito.when(body.withArray(ItunesJsonFieldPointer.RESULT.fieldName()))
            .thenReturn(array);
        Mockito.when(array.iterator()).thenReturn(List.of(item).iterator());
        Mockito.when(item.at(ItunesJsonFieldPointer.WRAPPER.fieldName()))
            .thenReturn(item);
        Mockito.when(item.textValue()).thenReturn(Instancio.create(String.class));

        Assertions.assertThat(itunesProxyService.findArtistTopAlbums(artistRefId, limit))
            .isEmpty();

        Mockito.verify(restTemplate).getForEntity(
            "/lookup?entity=album&amgArtistId={artistRefId}&limit={limit}",
            ObjectNode.class,
            artistRefId,
            limit.orElse(Constants.DEFAULT_TOP_LIMIT)
        );
        Mockito.verify(body).withArray(ItunesJsonFieldPointer.RESULT.fieldName());
        Mockito.verify(array).iterator();
        Mockito.verify(item).at(ItunesJsonFieldPointer.WRAPPER.fieldName());
        Mockito.verify(item).textValue();

        Mockito.verifyNoMoreInteractions(restTemplate, body, array, item);
    }

    @Test
    void findArtistTopAlbumsWhenErrorHttp() {
        String artistRefId = Instancio.create(String.class);
        Optional<Integer> limit = Instancio.create(new TypeToken<>() {
        });
        RestClientException cause = Instancio.create(RestClientException.class);

        Mockito.when(restTemplate.getForEntity(
                "/lookup?entity=album&amgArtistId={artistRefId}&limit={limit}",
                ObjectNode.class,
                artistRefId,
                limit.orElse(Constants.DEFAULT_TOP_LIMIT)
            ))
            .thenThrow(cause);

        Assertions.assertThatThrownBy(() -> itunesProxyService.findArtistTopAlbums(artistRefId, limit))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.proxy.call"))
            .matches(it -> cause.equals(it.getCause()))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.MAP)
            .containsOnly(
                Map.entry("message", ExceptionUtils.getRootCauseMessage(cause))
            );

        Mockito.verify(restTemplate).getForEntity(
            "/lookup?entity=album&amgArtistId={artistRefId}&limit={limit}",
            ObjectNode.class,
            artistRefId,
            limit.orElse(Constants.DEFAULT_TOP_LIMIT)
        );

        Mockito.verifyNoMoreInteractions(restTemplate, body, array, item);
    }

    @Test
    void findArtistByWhenOk() {
        String search = Instancio.create(String.class);
        Optional<Integer> limit = Instancio.create(new TypeToken<>() {
        });
        ResponseEntity<ObjectNode> responseEntity = ResponseEntity.ok(body);

        Mockito.when(restTemplate.getForEntity(
                "/search?entity=allArtist&term={search}&limit={limit}",
                ObjectNode.class,
                search,
                limit.orElse(Constants.DEFAULT_LIMIT)
            ))
            .thenReturn(responseEntity);
        Mockito.when(body.withArray(ItunesJsonFieldPointer.RESULT.fieldName()))
            .thenReturn(array);
        Mockito.when(array.iterator()).thenReturn(List.of(item).iterator());
        Mockito.when(item.at(ItunesJsonFieldPointer.WRAPPER.fieldName()))
            .thenReturn(item);
        Mockito.when(item.textValue()).thenReturn(WrapperType.ARTIST.propertyValue());

        Assertions.assertThat(itunesProxyService.findArtistBy(search, limit))
            .containsOnly(item);

        Mockito.verify(restTemplate).getForEntity(
            "/search?entity=allArtist&term={search}&limit={limit}",
            ObjectNode.class,
            search,
            limit.orElse(Constants.DEFAULT_LIMIT)
        );
        Mockito.verify(body).withArray(ItunesJsonFieldPointer.RESULT.fieldName());
        Mockito.verify(array).iterator();
        Mockito.verify(item).at(ItunesJsonFieldPointer.WRAPPER.fieldName());
        Mockito.verify(item).textValue();

        Mockito.verifyNoMoreInteractions(restTemplate, body, array, item);
    }

    @Test
    void findArtistByWhenOkFiltered() {
        String search = Instancio.create(String.class);
        Optional<Integer> limit = Instancio.create(new TypeToken<>() {
        });
        ResponseEntity<ObjectNode> responseEntity = ResponseEntity.ok(body);

        Mockito.when(restTemplate.getForEntity(
                "/search?entity=allArtist&term={search}&limit={limit}",
                ObjectNode.class,
                search,
                limit.orElse(Constants.DEFAULT_LIMIT)
            ))
            .thenReturn(responseEntity);
        Mockito.when(body.withArray(ItunesJsonFieldPointer.RESULT.fieldName()))
            .thenReturn(array);
        Mockito.when(array.iterator()).thenReturn(List.of(item).iterator());
        Mockito.when(item.at(ItunesJsonFieldPointer.WRAPPER.fieldName()))
            .thenReturn(item);
        Mockito.when(item.textValue()).thenReturn(Instancio.create(String.class));

        Assertions.assertThat(itunesProxyService.findArtistBy(search, limit))
            .isEmpty();

        Mockito.verify(restTemplate).getForEntity(
            "/search?entity=allArtist&term={search}&limit={limit}",
            ObjectNode.class,
            search,
            limit.orElse(Constants.DEFAULT_LIMIT)
        );
        Mockito.verify(body).withArray(ItunesJsonFieldPointer.RESULT.fieldName());
        Mockito.verify(array).iterator();
        Mockito.verify(item).at(ItunesJsonFieldPointer.WRAPPER.fieldName());
        Mockito.verify(item).textValue();

        Mockito.verifyNoMoreInteractions(restTemplate, body, array, item);
    }

    @Test
    void fallbackWhenErrorApplicationExceptionNotHttp() {
        ApplicationException exception = Instancio.create(ApplicationException.class);

        Assertions.assertThatThrownBy(() -> itunesProxyService.fallback(exception))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .isEqualTo(exception);
    }

    @Test
    void fallbackWhenErrorApplicationExceptionHttpNotLimit() {
        ApplicationException exception = new ApplicationException(
            "error.http.bad-response",
            Map.entry("status", HttpStatus.INTERNAL_SERVER_ERROR)
        );

        Assertions.assertThatThrownBy(() -> itunesProxyService.fallback(exception))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .isEqualTo(exception);
    }

    @Test
    void fallbackWhenOkApplicationExceptionHttpIsLimit() {
        ApplicationException exception = new ApplicationException(
            "error.http.bad-response",
            Map.entry("status", HttpStatus.TOO_MANY_REQUESTS)
        );

        Assertions.assertThat(itunesProxyService.fallback(exception))
            .isEqualTo(Collections.emptyList());
    }

    @Test
    void fallbackWhenOkCircuitException() {
        Throwable exception = Instancio.create(Throwable.class);

        Assertions.assertThat(itunesProxyService.fallback(exception))
            .isEqualTo(Collections.emptyList());
    }
}