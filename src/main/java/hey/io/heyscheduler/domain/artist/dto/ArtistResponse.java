package hey.io.heyscheduler.domain.artist.dto;

import hey.io.heyscheduler.domain.artist.entity.Artist;
import java.util.List;

public record ArtistResponse(
    int count,  // 변경된 아티스트 수
    List<ArtistResult> artistList // 변경된 아티스트 목록
) {

    public static ArtistResponse of(List<Artist> artists) {
        List<ArtistResult> artistResults = artists.stream()
            .map(ArtistResult::of)
            .toList();

        return new ArtistResponse(
            artistResults.size(),
            artistResults
        );
    }

    public record ArtistResult(
        Long artistId,  // 아티스트 ID
        String name,    // 아티스트명
        String artistUid  // Spotify 아티스트 ID
    ) {

        public static ArtistResult of(Artist artist) {
            return new ArtistResult(
                artist.getArtistId(),
                artist.getName(),
                artist.getArtistUid()
            );
        }
    }
}

