package hey.io.heyscheduler.domain.artist.dto;

import hey.io.heyscheduler.domain.artist.entity.Artist;

public record ArtistResponse(
    Long artistId,  // 아티스트 ID
    String name,  // 아티스트명
    String artistUid  // Spotify 아티스트 ID
) {

    public static ArtistResponse of(Artist artist) {
        return new ArtistResponse(
            artist.getArtistId(),
            artist.getName(),
            artist.getArtistUid()
        );
    }
}
