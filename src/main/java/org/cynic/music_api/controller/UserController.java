package org.cynic.music_api.controller;

import org.cynic.music_api.Constants;
import org.cynic.music_api.domain.http.IdHttp;
import org.cynic.music_api.domain.http.user.CreateUserHttp;
import org.cynic.music_api.domain.http.user.UpdateUserHttp;
import org.cynic.music_api.domain.http.user.UserHttp;
import org.cynic.music_api.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping
    public IdHttp<Long> update(@RequestBody UpdateUserHttp body, @AuthenticationPrincipal OidcUser oidcUser) {
        return userService.update(body, oidcUser);
    }

    @PostMapping
    public IdHttp<Long> create(@RequestBody CreateUserHttp body, @AuthenticationPrincipal OidcUser oidcUser) {
        return userService.create(body, oidcUser);
    }

    @GetMapping("/me")
    public UserHttp item(@AuthenticationPrincipal OidcUser oidcUser) {
        return userService.itemBy(oidcUser);
    }
}
