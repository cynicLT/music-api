package org.cynic.music_api.service;

import java.util.List;
import java.util.Optional;
import org.cynic.music_api.domain.http.album.AlbumHttp;
import org.cynic.music_api.mapper.AlbumMapper;
import org.cynic.music_api.service.proxy.ItunesProxyService;
import org.springframework.stereotype.Component;

@Component
public class AlbumService {

    private final ItunesProxyService itunesProxyService;
    private final AlbumMapper albumMapper;

    public AlbumService(ItunesProxyService itunesProxyService, AlbumMapper albumMapper) {
        this.itunesProxyService = itunesProxyService;
        this.albumMapper = albumMapper;
    }

    public List<AlbumHttp> listBy(String artistRefId, Optional<Integer> limit) {
        return itunesProxyService.findArtistTopAlbums(artistRefId, limit)
            .stream()
            .map(albumMapper::to)
            .flatMap(Optional::stream)
            .toList();
    }
}
