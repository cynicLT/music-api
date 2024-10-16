package org.cynic.music_api.domain.http.user;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public record CreateUserHttp(Optional<String> favoriteArtistRefId) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}