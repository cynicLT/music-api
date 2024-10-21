package org.cynic.music_api.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.RestAssured;
import java.net.URL;
import org.cynic.music_api.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {Configuration.class, Configuration.SecurityAutoConfiguration.class}
)
@ActiveProfiles("it")
@Tag("it")
public class BaseIT {

    protected enum Token {
        WITH_FAVORITE_ARTIST(
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImUwMWIzZjg3MzViNjQ4YzA5MDhkMWI5ZmFkMzZjMDJkIn0.eyJzdWIiOiJzdWJqZWN0IiwiZW1haWwiOiJraXJpbC5udWdtYW5vdkBnbWFpbC5jb20iLCJyb2xlcyI6WyJ1c2VyIiwiYWRtaW4iXX0.SwKMsnUqHRBzEVEK01_AIwODxEb15OAxq7awJ3eM3ldEh1Ub3sDz57e-Jc1_Rk_gZ7yQbxe9osrtI5SDFXKWYw"),
        WITHOUT_FAVORITE(
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiIsImtpZCI6IjhiZGU5YTMwNjg1NDEwNGU5ZDJlYTNkMWNiN2ZkM2MxIn0.eyJzdWIiOiJzdWJqZWN0IiwiZW1haWwiOiJ2YXNpYS5wdXBraW5AZ21haWwuY29tIiwicm9sZXMiOlsidXNlciIsImFkbWluIl19.o2eFHYObIZi3_7umDfMKd9HURvTevf2Bd3RxPaI9RiJPzc8EeIDFh3FcmNcG0GRJjFjYBERivZk4wQobCoQmUA"),
        NEW("eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiIsImtpZCI6IjhiZGU5YTMwNjg1NDEwNGU5ZDJlYTNkMWNiN2ZkM2MxIn0.eyJzdWIiOiJzdWJqZWN0IiwiZW1haWwiOiJqdWFuLmdvbnNhbGV6bkBnbWFpbC5jb20iLCJyb2xlcyI6WyJ1c2VyIiwiYWRtaW4iXX0.ujssR721LZ_L01Tst6JcYWK3Yl95PDI7eQdb0zlOywUdaO8nhne967ffF_1_zKDmQSrBnUC00BZgdxBfNDRysw");
        private final String jwt;

        Token(String jwt) {
            this.jwt = jwt;
        }

        public String bearerToken() {
            return "bearer " + jwt;
        }
    }

    @LocalServerPort
    private int serverPort;


    @Value("${api.uri}")
    private URL api;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        RestAssured.port = serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(api.getPort()));
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }
}
