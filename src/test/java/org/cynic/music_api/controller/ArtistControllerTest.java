package org.cynic.music_api.controller;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.music_api.domain.http.artist.ArtistHttp;
import org.cynic.music_api.service.ArtistService;
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
class ArtistControllerTest {

    @InjectMocks
    private ArtistController artistController;

    @Mock
    private ArtistService artistService;


    @Test
    void listWhenOk() {
        String artistRefId = Instancio.create(String.class);
        Optional<Integer> limit = Instancio.create(new TypeToken<>() {
        });
        List<ArtistHttp> response = Instancio.createList(ArtistHttp.class);

        Mockito.when(artistService.listBy(artistRefId, limit)).thenReturn(response);

        Assertions.assertThat(artistController.list(artistRefId, limit))
            .isEqualTo(response);

        Mockito.verify(artistService).listBy(artistRefId, limit);

        Mockito.verifyNoMoreInteractions(artistService);
    }
}