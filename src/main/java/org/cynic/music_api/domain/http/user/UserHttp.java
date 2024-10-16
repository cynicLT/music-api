package org.cynic.music_api.domain.http.user;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public record UserHttp(Long id, String email, Optional<String> favoriteArtistRefId) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}