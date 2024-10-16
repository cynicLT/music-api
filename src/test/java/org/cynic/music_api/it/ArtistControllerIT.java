package org.cynic.music_api.it;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.restassured.RestAssured;
import org.hamcrest.core.Is;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


@WireMockTest(httpPort = 9090)
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
        WireMock.stubFor(
            WireMock.get(UrlPattern.ANY)
                .willReturn(
                    WireMock
                        .ok()
                        .withBody("""
                                    {
                                        "resultCount": 2,
                                        "results": [
                                            {
                                                "wrapperType": "artist",
                                                "artistType": "Artist",
                                                "artistName": "ABBA",
                                                "artistLinkUrl": "https://music.apple.com/us/artist/abba/372976?uo=4",
                                                "artistId": 372976,
                                                "amgArtistId": 3492,
                                                "primaryGenreName": "Pop",
                                                "primaryGenreId": 14
                                            },
                                            {
                                                "wrapperType": "artist",
                                                "artistType": "Artist",
                                                "artistName": "Benny Andersson",
                                                "artistLinkUrl": "https://music.apple.com/us/artist/benny-andersson/376154?uo=4",
                                                "artistId": 376154,
                                                "amgArtistId": 51752,
                                                "primaryGenreName": "Pop",
                                                "primaryGenreId": 14
                                            }
                                        ]
                                    }
                            """)
                        .withStatus(200)
                )
        );

        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, Token.WITH_FAVORITE_ARTIST.bearerToken())
            .queryParam("query", Instancio.create(String.class))
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
}
