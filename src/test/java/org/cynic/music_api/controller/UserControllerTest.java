package org.cynic.music_api.controller;

import org.cynic.music_api.domain.http.IdHttp;
import org.cynic.music_api.domain.http.user.CreateUserHttp;
import org.cynic.music_api.domain.http.user.UpdateUserHttp;
import org.cynic.music_api.domain.http.user.UserHttp;
import org.cynic.music_api.service.UserService;
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
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;


    @Test
    void updateWhenOk() {
        UpdateUserHttp body = Instancio.create(UpdateUserHttp.class);
        OidcUser oidcUser = Instancio.create(DefaultOidcUser.class);
        IdHttp<Long> response = Instancio.create(new TypeToken<>() {
        });

        Mockito.when(userService.update(body, oidcUser))
            .thenReturn(response);

        userController.update(body, oidcUser);

        Mockito.verify(userService).update(body, oidcUser);

        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void createWhenOk() {
        CreateUserHttp body = Instancio.create(CreateUserHttp.class);
        OidcUser oidcUser = Instancio.create(DefaultOidcUser.class);
        IdHttp<Long> response = Instancio.create(new TypeToken<>() {
        });

        Mockito.when(userService.create(body, oidcUser))
            .thenReturn(response);

        userController.create(body, oidcUser);

        Mockito.verify(userService).create(body, oidcUser);

        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void itemWhenOk() {
        OidcUser oidcUser = Instancio.create(DefaultOidcUser.class);
        UserHttp response = Instancio.create(UserHttp.class);

        Mockito.when(userService.itemBy(oidcUser))
            .thenReturn(response);

        userController.item(oidcUser);

        Mockito.verify(userService).itemBy(oidcUser);

        Mockito.verifyNoMoreInteractions(userService);
    }
}