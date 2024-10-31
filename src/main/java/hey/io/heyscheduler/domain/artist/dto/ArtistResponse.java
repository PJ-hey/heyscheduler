package hey.io.heyscheduler.domain.artist.dto;

import hey.io.heyscheduler.domain.artist.entity.Artist;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "변경된 아티스트 목록")
public record ArtistResponse(
    @Schema(description = "변경된 아티스트 수", example = "1")
    int count,

    @Schema(description = "아티스트 목록", nullable = true)
    List<ArtistResult> artistList
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
        @Schema(description = "아티스트 ID", example = "35")
        Long artistId,

        @Schema(description = "아티스트명", example = "LUCY")
        String name,

        @Schema(description = "Spotify 아티스트 ID", example = "4eh2JeBpQaScfHKKXZh5vO")
        String artistUid
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

