package org.cynic.music_api.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import org.cynic.music_api.Constants.ItunesJsonFieldPointer;
import org.cynic.music_api.domain.http.artist.ArtistHttp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class ArtistMapper {

    protected abstract ArtistHttp internal(Optional<JsonNode> refId, Optional<JsonNode> title);

    public Optional<ArtistHttp> to(JsonNode item) {
        return Optional.ofNullable(internal(
            convert(item, ItunesJsonFieldPointer.ARTIST_REF_ID.fieldName()),
            convert(item, ItunesJsonFieldPointer.ARTIST_TITLE.fieldName())
        ));
    }

    protected Optional<JsonNode> convert(JsonNode item, String path) {
        return Optional.ofNullable(item)
            .map(it -> it.at(path));
    }

    protected Optional<String> convertToString(Optional<JsonNode> item) {
        return item.map(JsonNode::asText);
    }

    protected <T> T convertToItem(Optional<T> item) {
        return item.orElse(null);
    }
}
