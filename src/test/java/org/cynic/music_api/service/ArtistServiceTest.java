package org.cynic.music_api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.music_api.domain.http.artist.ArtistHttp;
import org.cynic.music_api.mapper.ArtistMapper;
import org.cynic.music_api.service.proxy.ItunesProxyService;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class ArtistServiceTest {

    @InjectMocks
    private ArtistService artistService;

    @Mock
    private ItunesProxyService itunesProxyService;

    @Mock
    private ArtistMapper artistMapper;


    @Test
    void listByWhenOk() {
        String search = Instancio.create(String.class);
        Optional<Integer> limit = Instancio.create(new TypeToken<>() {
        });
        ObjectNode json = Instancio.create(ObjectNode.class);
        ArtistHttp item = Instancio.create(ArtistHttp.class);

        Mockito.when(itunesProxyService.findArtistBy(search, limit)).thenReturn(List.of(json));
        Mockito.when(artistMapper.to(json)).thenReturn(Optional.of(item));

        Assertions.assertThat(artistService.listBy(search, limit))
            .containsOnly(item);

        Mockito.verify(itunesProxyService).findArtistBy(search, limit);
        Mockito.verify(artistMapper).to(json);

        Mockito.verifyNoMoreInteractions(itunesProxyService, artistMapper);
    }
}