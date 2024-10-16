package org.cynic.music_api.controller;

import java.util.List;
import java.util.Optional;
import org.cynic.music_api.Constants;
import org.cynic.music_api.domain.http.artist.ArtistHttp;
import org.cynic.music_api.service.ArtistService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/artists")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public List<ArtistHttp> list(@RequestParam String query, @RequestParam(required = false) Optional<Integer> limit) {
        return artistService.listBy(query, limit);
    }
}
