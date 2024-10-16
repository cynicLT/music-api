package org.cynic.music_api.mapper;

import java.util.Optional;
import org.cynic.music_api.domain.entity.User;
import org.cynic.music_api.domain.http.user.CreateUserHttp;
import org.cynic.music_api.domain.http.user.UpdateUserHttp;
import org.cynic.music_api.domain.http.user.UserHttp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class UserMapper {


    @Mapping(target = "favoriteArtistRefId", source = "param.favoriteArtistRefId")
    @Mapping(target = "email", source = "oidcUser.email")
    protected abstract User internal(OidcUser oidcUser, CreateUserHttp param);

    @Mapping(target = "favoriteArtistRefId", source = "favoriteArtistRefId")
    @Mapping(target = "email", ignore = true)
    protected abstract User internal(@MappingTarget User user, UpdateUserHttp param);

    @Mapping(target = "favoriteArtistRefId", source = "favoriteArtistRefId")
    protected abstract UserHttp internal(User user);

    public Optional<UserHttp> to(User user) {
        return Optional.ofNullable(internal(user));
    }

    public Optional<User> to(OidcUser oidcUser, CreateUserHttp param) {
        return Optional.ofNullable(internal(oidcUser, param));
    }

    public Optional<User> to(User user, UpdateUserHttp param) {
        return Optional.ofNullable(internal(user, param));
    }

    protected <T> T convertToItem(Optional<T> item) {
        return item.orElse(null);
    }

    protected <T> Optional<T> convertToOptional(T item) {
        return Optional.ofNullable(item);
    }
}
