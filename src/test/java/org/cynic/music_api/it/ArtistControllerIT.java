package org.cynic.music_api.it;

import io.restassured.RestAssured;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


public class ArtistControllerIT extends BaseIT {

    @Test
    void givenNoQueryParamWhenArtistsThenError() {
        RestAssured.given()
            .when()
            .get("/artists")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
            .assertThat()
            .body("size()", Is.is(2))
            .body("code", Is.is("error.parameter.missing"))
            .body("values.size()", Is.is(2))
            .body("values.name", Is.is("query"))
            .body("values.type", Is.is("String"));
    }

    @Test
    void givenNotAuthorizationHeaderWhenArtistsThenError() {
        RestAssured.given()
            .when()
            .queryParam("query", "abba")
            .get("/artists")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .and()
            .assertThat()
            .body("size()", Is.is(1))
            .body("code", Is.is("error.access.denied"));
    }

    @Test
    void givenOkWhenArtistsThenOk() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, Token.WITH_FAVORITE_ARTIST.bearerToken())
            .queryParam("query", "abba")
            .get("/artists")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Is.is(2))
            .body("[0].refId", Is.is("3492"))
            .body("[0].title", Is.is("ABBA"))
            .body("[1].refId", Is.is("51752"))
            .body("[1].title", Is.is("Benny Andersson"));
    }


    @Test
    void givenOkWhenArtistsAndLimitThenOk() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, Token.WITH_FAVORITE_ARTIST.bearerToken())
            .queryParam("query", "sel")
            .queryParam("limit", "13")
            .get("/artists")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Is.is(0));
    }
}
