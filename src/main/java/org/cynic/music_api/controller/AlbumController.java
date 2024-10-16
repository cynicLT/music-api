package org.cynic.music_api.controller;

import java.util.List;
import java.util.Optional;
import org.cynic.music_api.Constants;
import org.cynic.music_api.domain.http.album.AlbumHttp;
import org.cynic.music_api.service.AlbumService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/artists/{artistRefId}/albums")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
@CrossOrigin
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public List<AlbumHttp> list(@PathVariable String artistRefId, @RequestParam(required = false) Optional<Integer> limit) {
        return albumService.listBy(artistRefId, limit);
    }
}
