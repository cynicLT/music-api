package org.cynic.music_api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.music_api.domain.http.album.AlbumHttp;
import org.cynic.music_api.mapper.AlbumMapper;
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
class AlbumServiceTest {

    @InjectMocks
    private AlbumService albumService;

    @Mock
    private ItunesProxyService itunesProxyService;

    @Mock
    private AlbumMapper albumMapper;


    @Test
    void listByWhenOk() {
        String artistRefId = Instancio.create(String.class);
        Optional<Integer> limit = Instancio.create(new TypeToken<>() {
        });
        ObjectNode json = Instancio.create(ObjectNode.class);
        AlbumHttp item = Instancio.create(AlbumHttp.class);

        Mockito.when(itunesProxyService.findArtistTopAlbums(artistRefId, limit)).thenReturn(List.of(json));
        Mockito.when(albumMapper.to(json)).thenReturn(Optional.of(item));

        Assertions.assertThat(albumService.listBy(artistRefId, limit))
            .containsOnly(item);

        Mockito.verify(itunesProxyService).findArtistTopAlbums(artistRefId, limit);
        Mockito.verify(albumMapper).to(json);

        Mockito.verifyNoMoreInteractions(itunesProxyService, albumMapper);
    }
}