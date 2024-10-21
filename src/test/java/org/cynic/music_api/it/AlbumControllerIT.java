package org.cynic.music_api.it;

import io.restassured.RestAssured;
import org.hamcrest.core.Is;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class AlbumControllerIT extends BaseIT {

    @Test
    void givenOKWhenAlbumsThenOk() {
        RestAssured.given()
            .when()
            .pathParam("artistRefId", Instancio.create(String.class))
            .header(HttpHeaders.AUTHORIZATION, Token.WITH_FAVORITE_ARTIST.bearerToken())
            .when()
            .get("/artists/{artistRefId}/albums")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Is.is(2))
            .body("[0].refId", Is.is("1422648512"))
            .body("[0].title", Is.is("ABBA Gold: Greatest Hits"))
            .body("[1].refId", Is.is("1380464428"))
            .body("[1].title", Is.is("Mamma Mia! Here We Go Again (The Movie Soundtrack feat. the Songs of ABBA)"));
    }

}
