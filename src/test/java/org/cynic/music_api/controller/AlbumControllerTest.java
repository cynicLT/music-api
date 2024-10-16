package org.cynic.music_api.controller;


import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.music_api.domain.http.album.AlbumHttp;
import org.cynic.music_api.service.AlbumService;
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
class AlbumControllerTest {

    @InjectMocks
    private AlbumController albumController;

    @Mock
    private AlbumService albumService;

    @Test
    void listWhenOk() {
        String artistRefId = Instancio.create(String.class);
        Optional<Integer> limit = Instancio.create(new TypeToken<>() {
        });
        List<AlbumHttp> response = Instancio.createList(AlbumHttp.class);

        Mockito.when(albumService.listBy(artistRefId, limit)).thenReturn(response);

        Assertions.assertThat(albumController.list(artistRefId, limit))
            .isEqualTo(response);

        Mockito.verify(albumService).listBy(artistRefId, limit);

        Mockito.verifyNoMoreInteractions(albumService);
    }
}