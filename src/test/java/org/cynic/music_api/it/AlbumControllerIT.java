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
public class AlbumControllerIT extends BaseIT {

    @Test
    void givenOKWhenAlbumsThenOk() {
        WireMock.stubFor(
            WireMock.get(UrlPattern.ANY)
                .willReturn(
                    WireMock
                        .ok()
                        .withBody("""
                                    {
                                        "resultCount":2,
                                        "results":[
                                           {
                                              "wrapperType":"artist",
                                              "artistType":"Artist",
                                              "artistName":"ABBA",
                                              "artistLinkUrl":"https://music.apple.com/us/artist/abba/372976?uo=4",
                                              "artistId":372976,
                                              "amgArtistId":3492,
                                              "primaryGenreName":"Pop",
                                              "primaryGenreId":14
                                           },
                                           {
                                              "wrapperType":"collection",
                                              "collectionType":"Album",
                                              "artistId":372976,
                                              "collectionId":1422648512,
                                              "amgArtistId":3492,
                                              "artistName":"ABBA",
                                              "collectionName":"ABBA Gold: Greatest Hits",
                                              "collectionCensoredName":"ABBA Gold: Greatest Hits",
                                              "artistViewUrl":"https://music.apple.com/us/artist/abba/372976?uo=4",
                                              "collectionViewUrl":"https://music.apple.com/us/album/abba-gold-greatest-hits/1422648512?uo=4",
                                              "artworkUrl60":"https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/60/f8/a6/60f8a6bc-e875-238d-f2f8-f34a6034e6d2/14UMGIM07615.rgb.jpg/60x60bb.jpg",
                                              "artworkUrl100":"https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/60/f8/a6/60f8a6bc-e875-238d-f2f8-f34a6034e6d2/14UMGIM07615.rgb.jpg/100x100bb.jpg",
                                              "collectionPrice":9.99,
                                              "collectionExplicitness":"notExplicit",
                                              "trackCount":19,
                                              "copyright":"This Compilation ℗ 2014 Polar Music International AB",
                                              "country":"USA",
                                              "currency":"USD",
                                              "releaseDate":"1992-09-21T07:00:00Z",
                                              "primaryGenreName":"Pop"
                                           },
                                           {
                                              "wrapperType":"collection",
                                              "collectionType":"Album",
                                              "artistId":376154,
                                              "collectionId":1380464428,
                                              "amgArtistId":51752,
                                              "artistName":"Benny Andersson, Björn Ulvaeus & Lily James",
                                              "collectionName":"Mamma Mia! Here We Go Again (The Movie Soundtrack feat. the Songs of ABBA)",
                                              "collectionCensoredName":"Mamma Mia! Here We Go Again (The Movie Soundtrack feat. the Songs of ABBA)",
                                              "artistViewUrl":"https://music.apple.com/us/artist/benny-andersson/376154?uo=4",
                                              "collectionViewUrl":"https://music.apple.com/us/album/mamma-mia-here-we-go-again-the-movie-soundtrack-feat/1380464428?uo=4",
                                              "artworkUrl60":"https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/3e/be/e2/3ebee2d8-56a0-98b2-2427-b384ea708725/00602567666585.rgb.jpg/60x60bb.jpg",
                                              "artworkUrl100":"https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/3e/be/e2/3ebee2d8-56a0-98b2-2427-b384ea708725/00602567666585.rgb.jpg/100x100bb.jpg",
                                              "collectionPrice":11.99,
                                              "collectionExplicitness":"notExplicit",
                                              "trackCount":19,
                                              "copyright":"A Polydor Records release; ℗ 2018 Littlestar Services Limited, under exclusive licence to Universal Music Operations Limited",
                                              "country":"USA",
                                              "currency":"USD",
                                              "releaseDate":"2018-07-13T07:00:00Z",
                                              "primaryGenreName":"Musicals"
                                           }
                                        ]
                                     }
                            """)
                        .withStatus(200)
                )
        );

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
