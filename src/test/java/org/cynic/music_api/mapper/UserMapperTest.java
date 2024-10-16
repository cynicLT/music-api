package org.cynic.music_api.mapper;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.cynic.music_api.domain.entity.User;
import org.cynic.music_api.domain.http.user.CreateUserHttp;
import org.cynic.music_api.domain.http.user.UpdateUserHttp;
import org.cynic.music_api.domain.http.user.UserHttp;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = new UserMapperImpl();
    }

    @Test
    void toHttpWhenOK() {
        User user = Instancio.create(User.class);
        UserHttp expected = new UserHttp(
            user.getId(),
            user.getEmail(),
            Optional.ofNullable(user.getFavoriteArtistRefId())
        );

        Assertions.assertThat(mapper.to(user))
            .contains(expected);
    }

    @Test
    void toEntityWhenCreateOk() {
        OidcUser oidcUser = Instancio.create(DefaultOidcUser.class);
        CreateUserHttp param = Instancio.create(CreateUserHttp.class);

        User expected = new User();
        expected.setEmail(oidcUser.getEmail());
        expected.setFavoriteArtistRefId(param.favoriteArtistRefId().orElse(StringUtils.EMPTY));

        Assertions.<Optional<User>>assertThat(mapper.to(oidcUser, param))
            .extracting(Optional::get)
            .usingRecursiveComparison()
            .comparingOnlyFields(
                "email",
                "favoriteArtistRefId"
            )
            .isEqualTo(expected);
    }

    @Test
    void toEntityWhenUpdateOk() {
        User user = Instancio.create(User.class);
        UpdateUserHttp param = Instancio.create(UpdateUserHttp.class);

        User expected = new User();
        expected.setFavoriteArtistRefId(param.favoriteArtistRefId().orElse(StringUtils.EMPTY));

        Assertions.<Optional<User>>assertThat(mapper.to(user, param))
            .extracting(Optional::get)
            .usingRecursiveComparison()
            .comparingOnlyFields(
                "favoriteArtistRefId"
            )
            .isEqualTo(expected);
    }
}