package org.cynic.music_api.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.api.Assertions;
import org.cynic.music_api.Constants.ItunesJsonFieldPointer;
import org.cynic.music_api.domain.http.artist.ArtistHttp;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class ArtistMapperTest {

    private ArtistMapper mapper;

    @Mock
    private JsonNode item;

    @BeforeEach
    void setUp() {
        this.mapper = new ArtistMapperImpl();
    }

    @Test
    void toWhenOk() {
        String refId = Instancio.create(String.class);
        JsonNode refIdNode = Mockito.mock(JsonNode.class);
        Mockito.when(refIdNode.asText()).thenReturn(refId);

        String title = Instancio.create(String.class);
        JsonNode titleNode = Mockito.mock(JsonNode.class);
        Mockito.when(titleNode.asText()).thenReturn(title);

        Mockito.when(item.at(ItunesJsonFieldPointer.ARTIST_REF_ID.fieldName())).thenReturn(refIdNode);
        Mockito.when(item.at(ItunesJsonFieldPointer.ARTIST_TITLE.fieldName())).thenReturn(titleNode);

        ArtistHttp expected = new ArtistHttp(refId, title);

        Assertions.assertThat(mapper.to(item))
            .contains(expected);

        Mockito.verify(item).at(ItunesJsonFieldPointer.ARTIST_REF_ID.fieldName());
        Mockito.verify(refIdNode).asText();
        Mockito.verify(item).at(ItunesJsonFieldPointer.ARTIST_TITLE.fieldName());
        Mockito.verify(titleNode).asText();

        Mockito.verifyNoMoreInteractions(item, refIdNode, titleNode);
    }
}