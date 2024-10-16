package org.cynic.music_api.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import org.cynic.music_api.Constants.ItunesJsonFieldPointer;
import org.cynic.music_api.domain.http.album.AlbumHttp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class AlbumMapper {

    protected abstract AlbumHttp internal(Optional<JsonNode> refId, Optional<JsonNode> title);

    public Optional<AlbumHttp> to(JsonNode item) {
        return Optional.ofNullable(internal(
            convert(item, ItunesJsonFieldPointer.ALBUM_REF_ID.fieldName()),
            convert(item, ItunesJsonFieldPointer.ALBUM_TITLE.fieldName())
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
