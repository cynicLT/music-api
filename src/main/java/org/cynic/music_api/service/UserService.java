package org.cynic.music_api.service;

import jakarta.persistence.LockModeType;
import java.util.Map;
import org.cynic.music_api.domain.ApplicationException;
import org.cynic.music_api.domain.entity.User;
import org.cynic.music_api.domain.http.IdHttp;
import org.cynic.music_api.domain.http.user.CreateUserHttp;
import org.cynic.music_api.domain.http.user.UpdateUserHttp;
import org.cynic.music_api.domain.http.user.UserHttp;
import org.cynic.music_api.mapper.UserMapper;
import org.cynic.music_api.repository.UserRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserHttp itemBy(OidcUser oidcUser) {
        return userRepository.findOne(UserRepository.byEmail(oidcUser.getEmail()))
            .flatMap(userMapper::to)
            .orElseThrow(() ->
                new ApplicationException(
                    "error.user.not-found",
                    Map.entry("email", oidcUser.getEmail())
                )
            );
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public IdHttp<Long> create(CreateUserHttp param, OidcUser oidcUser) {
        if (!userRepository.exists(UserRepository.byEmail(oidcUser.getEmail()))) {
            return userMapper.to(oidcUser, param)
                .map(userRepository::save)
                .map(User::getId)
                .map(IdHttp::new)
                .orElseThrow(
                    () -> new ApplicationException(
                        "error.user.save",
                        Map.entry("email", oidcUser.getEmail())
                    )
                );
        } else {
            throw new ApplicationException(
                "error.user.exists",
                Map.entry("email", oidcUser.getEmail())
            );
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Lock(LockModeType.PESSIMISTIC_READ)
    public IdHttp<Long> update(UpdateUserHttp param, OidcUser oidcUser) {
        User user = userRepository.findOne(UserRepository.byEmail(oidcUser.getEmail()))
            .orElseThrow(() ->
                new ApplicationException(
                    "error.user.not-found",
                    Map.entry("email", oidcUser.getEmail())
                )
            );

        return userMapper.to(user, param)
            .map(userRepository::save)
            .map(User::getId)
            .map(IdHttp::new)
            .orElseThrow(
                () -> new ApplicationException(
                    "error.user.update",
                    Map.entry("email", oidcUser.getEmail())
                )
            );
    }
}