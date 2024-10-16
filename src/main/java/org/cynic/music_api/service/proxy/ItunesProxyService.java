package org.cynic.music_api.service.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cynic.music_api.Constants;
import org.cynic.music_api.Constants.ItunesJsonFieldPointer;
import org.cynic.music_api.Constants.WrapperType;
import org.cynic.music_api.domain.ApplicationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ItunesProxyService {

    private final Supplier<RestTemplate> restTemplateCreator;

    private static final String BAD_RESPONSE_CODE = "error.http.bad-response";
    private static final String STATUS_CODE = "status";

    public ItunesProxyService(Supplier<RestTemplate> restTemplateCreator) {
        this.restTemplateCreator = restTemplateCreator;
    }

    @CircuitBreaker(name = Constants.PROXY_SPACE_NAME, fallbackMethod = Constants.PROXY_FALLBACK)
    @Cacheable(cacheNames = Constants.ARTIST_TOP_ALBUMS_SPACE_NAME)
    public List<JsonNode> findArtistTopAlbums(String artistRefId, Optional<Integer> limit) {
        return processRequest(
            it -> it.getForEntity(
                "/lookup?entity=album&amgArtistId={artistRefId}&limit={limit}",
                ObjectNode.class,
                artistRefId,
                limit.orElse(Constants.DEFAULT_TOP_LIMIT)
            ),
            item -> Optional.of(item)
                .map(it -> it.at(ItunesJsonFieldPointer.WRAPPER.fieldName()))
                .map(JsonNode::textValue)
                .map(it -> StringUtils.equals(it, WrapperType.ALBUM.propertyValue()))
                .orElse(Boolean.FALSE)
        );
    }

    @CircuitBreaker(name = Constants.PROXY_SPACE_NAME, fallbackMethod = Constants.PROXY_FALLBACK)
    @Cacheable(cacheNames = Constants.ARTISTS_SPACE_NAME)
    public List<JsonNode> findArtistBy(String search, Optional<Integer> limit) {
        return processRequest(
            it -> it.getForEntity(
                "/search?entity=allArtist&term={search}&limit={limit}",
                ObjectNode.class,
                search,
                limit.orElse(Constants.DEFAULT_LIMIT)
            ),
            item -> Optional.of(item)
                .map(it -> it.at(ItunesJsonFieldPointer.WRAPPER.fieldName()))
                .map(JsonNode::textValue)
                .map(it -> StringUtils.equals(it, WrapperType.ARTIST.propertyValue()))
                .orElse(Boolean.FALSE)
        );
    }

    private List<JsonNode> processRequest(Function<RestTemplate, ResponseEntity<ObjectNode>> operation, Predicate<JsonNode> filter) {
        try {
            return Optional.of(restTemplateCreator)
                .map(Supplier::get)
                .map(operation)
                .map(HttpEntity::getBody)
                .map(it -> it.withArray(ItunesJsonFieldPointer.RESULT.fieldName()))
                .map(JsonNode::iterator)
                .map(IteratorUtils::toList)
                .stream()
                .flatMap(Collection::stream)
                .filter(filter)
                .toList();
        } catch (RestClientException e) {
            throw new ApplicationException("error.proxy.call",
                e,
                Map.entry("message", ExceptionUtils.getRootCauseMessage(e))
            );
        }
    }

    public List<JsonNode> fallback(Throwable exception) {
        return Optional.of(exception)
            .filter(it -> ClassUtils.isAssignable(it.getClass(), ApplicationException.class))
            .map(ApplicationException.class::cast)
            .map(this::handeApplicationException)
            .orElse(Collections.emptyList());
    }

    private List<JsonNode> handeApplicationException(ApplicationException exception) {
        return Optional.of(exception)
            .filter(it -> StringUtils.equals(BAD_RESPONSE_CODE, it.getCode()))
            .map(ApplicationException::getValues)
            .map(Map::entrySet)
            .stream()
            .flatMap(Collection::stream)
            .filter(it -> StringUtils.equals(STATUS_CODE, it.getKey()))
            .findAny()
            .map(Entry::getValue)
            .map(HttpStatus.class::cast)
            .filter(HttpStatus.TOO_MANY_REQUESTS::equals)
            .map(_ -> Collections.<JsonNode>emptyList())
            .orElseThrow(() -> exception);
    }
}
