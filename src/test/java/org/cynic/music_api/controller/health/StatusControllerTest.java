package org.cynic.music_api.controller.health;

import org.assertj.core.api.Assertions;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class StatusControllerTest {

    @InjectMocks
    private StatusController statusController;

    @Test
    void indexWhenOK() {
        Assertions.assertThat(statusController.index())
            .isEqualTo("OK");
    }
}