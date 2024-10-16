package org.cynic.music_api.it;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Optional;
import org.cynic.music_api.domain.http.user.CreateUserHttp;
import org.cynic.music_api.domain.http.user.UpdateUserHttp;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class UserControllerIT extends BaseIT {

    @Test
    void givenUserWithoutFavoriteWhenOk() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, Token.WITHOUT_FAVORITE.bearerToken())
            .get("/users/me")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Is.is(2))
            .body("id", Is.is(2))
            .body("email", Is.is("vasia.pupkin@gmail.com"));
    }

    @Test
    void givenUserWithFavoriteWhenOk() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, Token.WITH_FAVORITE_ARTIST.bearerToken())
            .get("/users/me")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Is.is(3))
            .body("id", Is.is(1))
            .body("email", Is.is("kiril.nugmanov@gmail.com"))
            .body("favoriteArtistRefId", Matchers.oneOf("13", "3492"));
    }


    @Test
    void givenUserExistsWhenError() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, Token.WITH_FAVORITE_ARTIST.bearerToken())
            .contentType(ContentType.JSON)
            .body(
                new CreateUserHttp(Optional.of("13"))
            )
            .post("/users")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
            .assertThat()
            .body("size()", Is.is(2))
            .body("code", Is.is("error.user.exists"))
            .body("values.size()", Is.is(1))
            .body("values.email", Is.is("kiril.nugmanov@gmail.com"));
    }

    @Test
    void givenUserUpdateWhenOk() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, Token.WITH_FAVORITE_ARTIST.bearerToken())
            .contentType(ContentType.JSON)
            .body(
                new UpdateUserHttp(Optional.of("13"))
            )
            .put("/users")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Is.is(1))
            .body("id", Is.is(1));
    }

    @Test
    void givenUserNotExistsWhenOk() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, Token.NEW.bearerToken())
            .contentType(ContentType.JSON)
            .body(
                new CreateUserHttp(Optional.of("13"))
            )
            .post("/users")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Is.is(1))
            .body("id", Is.is(3));
    }
}
