package org.cynic.music_api.it;

import io.restassured.RestAssured;
import java.time.Clock;
import org.cynic.music_api.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    protected static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IjAxODkwN2QzMzllNDAxMzU0ZTdhMWIyMWNlOTg4ZWRiIn0.eyJpc3MiOiJpc3MiLCJpYXQiOjE2OTkyNzY0MTksImV4cCI6MTczMDgxMjQxOSwiYXVkIjoiYXVkIiwic3ViIjoic3ViIiwiZW1haWwiOiJraXJpbC5udWdtYW5vdkBnbWFpbC5jb20ifQ.f_Ux8_M-g3eOLncmBqm1wkZRUANKjwOv_kWlo5G3_kPub5wCvUjUkGdb-lpVYScl9M7GqIaRXnrjSYmRFcAY_19m8XzDKfajrVlF30-AP7bz1kx0i6nvtmdHsyx9vr4TbloHI25odpxqOJXnPpH9hkiF7LHxkFIhKb_EFiIpTpgSO5B6jY72AwdDhFJgEAAYkuEaweI0AOFgjebL0BEqYV09gPtQrMC9WSPoztJtZf2wHuDnTM1gKKEIK20Nb8vRt2Ifn0akJMsTDKg1keDk9nPKrK7eMr2p7TaAYt1vVdwjKuth1_o3byFdQqOkKgp5-KgUjpPgeYzydtSPdc2xRKC1d3YdcxJAPdC8XdxcmIFmH02jU40RK2Hf6cV_fHJgwj2B98C33NHvq5-eb6KcF7qDvXP4Wpk8hdUvdak1zEOlyvhWbSj-VjTWKqm6Pxfh6S163u5slmA8HgKdCkO5M4EfdKWYIcvF5U39AYt_kQHLr4OmQDLhLRmKV8dStKlo";

    @LocalServerPort
    private int serverPort;

    @Autowired
    protected Clock clock;

    @BeforeEach
    public void setup() {
        RestAssured.port = serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
