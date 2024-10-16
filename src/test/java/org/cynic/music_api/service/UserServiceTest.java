package org.cynic.music_api.service;

import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.music_api.domain.ApplicationException;
import org.cynic.music_api.domain.entity.User;
import org.cynic.music_api.domain.http.IdHttp;
import org.cynic.music_api.domain.http.user.CreateUserHttp;
import org.cynic.music_api.domain.http.user.UpdateUserHttp;
import org.cynic.music_api.domain.http.user.UserHttp;
import org.cynic.music_api.mapper.UserMapper;
import org.cynic.music_api.repository.UserRepository;
import org.instancio.Instancio;
import org.instancio.Select;
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
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    void itemByWhenOk() {
        String email = Instancio.create(String.class);
        OidcUser oidcUser = Instancio.of(DefaultOidcUser.class)
            .set(Select.field(DefaultOAuth2User.class, "attributes"), Map.of("email", email))
            .create();
        User user = Instancio.create(User.class);
        UserHttp http = Instancio.create(UserHttp.class);

        Mockito.when(userRepository.findOne(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.to(user)).thenReturn(Optional.of(http));

        Assertions.assertThat(userService.itemBy(oidcUser))
            .isEqualTo(http);

        Mockito.verify(userRepository).findOne(Mockito.any());
        Mockito.verify(userMapper).to(user);

        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void itemByWhenErrorNotFound() {
        String email = Instancio.create(String.class);
        OidcUser oidcUser = Instancio.of(DefaultOidcUser.class)
            .set(Select.field(DefaultOAuth2User.class, "attributes"), Map.of("email", email))
            .create();

        Mockito.when(userRepository.findOne(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.itemBy(oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.user.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.MAP)
            .containsOnly(
                Map.entry("email", email)
            );

        Mockito.verify(userRepository).findOne(Mockito.any());

        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void createWhenOk() {
        String email = Instancio.create(String.class);
        OidcUser oidcUser = Instancio.of(DefaultOidcUser.class)
            .set(Select.field(DefaultOAuth2User.class, "attributes"), Map.of("email", email))
            .create();
        CreateUserHttp param = Instancio.create(CreateUserHttp.class);
        User user = Instancio.create(User.class);
        IdHttp<Long> expected = new IdHttp<>(user.getId());

        Mockito.when(userRepository.exists(Mockito.any())).thenReturn(false);
        Mockito.when(userMapper.to(oidcUser, param)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Assertions.assertThat(userService.create(param, oidcUser))
            .isEqualTo(expected);

        Mockito.verify(userRepository).exists(Mockito.any());
        Mockito.verify(userMapper).to(oidcUser, param);
        Mockito.verify(userRepository).save(user);

        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void createWhenErrorSave() {
        String email = Instancio.create(String.class);
        OidcUser oidcUser = Instancio.of(DefaultOidcUser.class)
            .set(Select.field(DefaultOAuth2User.class, "attributes"), Map.of("email", email))
            .create();
        CreateUserHttp param = Instancio.create(CreateUserHttp.class);
        User user = Instancio.create(User.class);

        Mockito.when(userRepository.exists(Mockito.any())).thenReturn(false);
        Mockito.when(userMapper.to(oidcUser, param)).thenReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> userService.create(param, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.user.save"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.MAP)
            .containsOnly(
                Map.entry("email", email)
            );

        Mockito.verify(userRepository).exists(Mockito.any());
        Mockito.verify(userMapper).to(oidcUser, param);
        Mockito.verify(userRepository).save(user);

        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void createWhenErrorExists() {
        String email = Instancio.create(String.class);
        OidcUser oidcUser = Instancio.of(DefaultOidcUser.class)
            .set(Select.field(DefaultOAuth2User.class, "attributes"), Map.of("email", email))
            .create();
        CreateUserHttp param = Instancio.create(CreateUserHttp.class);

        Mockito.when(userRepository.exists(Mockito.any())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> userService.create(param, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.user.exists"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.MAP)
            .containsOnly(
                Map.entry("email", email)
            );

        Mockito.verify(userRepository).exists(Mockito.any());

        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateWhenOk() {
        String email = Instancio.create(String.class);
        OidcUser oidcUser = Instancio.of(DefaultOidcUser.class)
            .set(Select.field(DefaultOAuth2User.class, "attributes"), Map.of("email", email))
            .create();
        UpdateUserHttp param = Instancio.create(UpdateUserHttp.class);
        User user = Instancio.create(User.class);
        IdHttp<Long> expected = new IdHttp<>(user.getId());

        Mockito.when(userRepository.findOne(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.to(user, param)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Assertions.assertThat(userService.update(param, oidcUser))
            .isEqualTo(expected);

        Mockito.verify(userRepository).findOne(Mockito.any());
        Mockito.verify(userMapper).to(user, param);
        Mockito.verify(userRepository).save(user);

        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateWhenErrorUpdate() {
        String email = Instancio.create(String.class);
        OidcUser oidcUser = Instancio.of(DefaultOidcUser.class)
            .set(Select.field(DefaultOAuth2User.class, "attributes"), Map.of("email", email))
            .create();
        UpdateUserHttp param = Instancio.create(UpdateUserHttp.class);
        User user = Instancio.create(User.class);

        Mockito.when(userRepository.findOne(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.to(user, param)).thenReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> userService.update(param, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.user.update"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.MAP)
            .containsOnly(
                Map.entry("email", email)
            );

        Mockito.verify(userRepository).findOne(Mockito.any());
        Mockito.verify(userMapper).to(user, param);
        Mockito.verify(userRepository).save(user);

        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateWhenErrorNotFound() {
        String email = Instancio.create(String.class);
        OidcUser oidcUser = Instancio.of(DefaultOidcUser.class)
            .set(Select.field(DefaultOAuth2User.class, "attributes"), Map.of("email", email))
            .create();
        UpdateUserHttp param = Instancio.create(UpdateUserHttp.class);

        Mockito.when(userRepository.findOne(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.update(param, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.user.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.MAP)
            .containsOnly(
                Map.entry("email", email)
            );

        Mockito.verify(userRepository).findOne(Mockito.any());

        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }
}