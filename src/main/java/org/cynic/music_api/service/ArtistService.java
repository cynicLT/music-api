package org.cynic.music_api.service;

import java.util.List;
import java.util.Optional;
import org.cynic.music_api.Constants;
import org.cynic.music_api.domain.http.artist.ArtistHttp;
import org.cynic.music_api.mapper.ArtistMapper;
import org.cynic.music_api.service.proxy.ItunesProxyService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ArtistService {

    private final ItunesProxyService itunesProxyService;
    private final ArtistMapper artistMapper;

    public ArtistService(ItunesProxyService itunesProxyService, ArtistMapper artistMapper) {
        this.itunesProxyService = itunesProxyService;
        this.artistMapper = artistMapper;
    }


    public List<ArtistHttp> listBy(String search, Optional<Integer> limit) {
        return itunesProxyService.findArtistBy(search, limit)
            .stream()
            .map(artistMapper::to)
            .flatMap(Optional::stream)
            .toList();
    }
}
