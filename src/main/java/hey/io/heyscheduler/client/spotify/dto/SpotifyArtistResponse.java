package hey.io.heyscheduler.client.spotify.dto;

import hey.io.heyscheduler.domain.artist.dto.ArtistRequest;
import hey.io.heyscheduler.domain.artist.enums.ArtistStatus;
import hey.io.heyscheduler.domain.file.entity.File;
import hey.io.heyscheduler.domain.file.enums.EntityType;
import hey.io.heyscheduler.domain.file.enums.FileCategory;
import hey.io.heyscheduler.domain.file.enums.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Schema(description = "Spotify 아티스트 목록")
public record SpotifyArtistResponse(

    @Schema(description = "Spotify 아티스트 ID", example = "0qr7Rhj0yU7BPySYecNUlm")
    String id,

    @Schema(description = "Spotify 아티스트명", example = "권은비")
    String name,

    @Schema(description = "Spotify 아티스트 장르", nullable = true, example = "[\"k-pop\", \"dance rock\"]")
    String[] genres,

    @Schema(description = "Spotify 아티스트 URL", example = "https://open.spotify.com/artist/0qr7Rhj0yU7BPySYecNUlm")
    String externalUrl,

    @Schema(description = "Spotify 아티스트 이미지", nullable = true)
    List<ArtistImage> artistImages, // 아티스트 이미지

    @Schema(description = "Spotify 아티스트 인기도", example = "48")
    Integer popularity
) {

    @Getter
    @Builder
    private static class ArtistImage {

        @Schema(description = "Spotify 아티스트 이미지 URL", nullable = true, example = "https://i.scdn.co/image/ab6761610000e5ebbbaf965d883863da863e60f8")
        private String url;

        @Schema(description = "Spotify 아티스트 이미지 너비", nullable = true, example = "640")
        private Integer width; // 이미지 너비

        @Schema(description = "Spotify 아티스트 이미지 높이", nullable = true, example = "640")
        private Integer height; // 이미지 높이

        private static ArtistImage of(Image image) {
            return ArtistImage.builder()
                .url(image.getUrl())
                .width(image.getWidth())
                .height(image.getHeight())
                .build();
        }
    }

    public static SpotifyArtistResponse of(Artist spotifyArtist) {
        return new SpotifyArtistResponse(
            spotifyArtist.getId(),
            spotifyArtist.getName(),
            spotifyArtist.getGenres(),
            spotifyArtist.getExternalUrls().get("spotify"),
            Arrays.stream(spotifyArtist.getImages()).map(ArtistImage::of).toList(),
            spotifyArtist.getPopularity()
        );
    }

    public static List<String> ofId(List<SpotifyArtistResponse> spotifyArtistList) {
        return spotifyArtistList.stream()
            .map(SpotifyArtistResponse::id)
            .toList();
    }

    public ArtistRequest toRequest() {
        return ArtistRequest.builder()
            .name(name)
            .engName(name)
            .artistUid(id)
            .artistUrl(externalUrl)
            .popularity(popularity)
            .artistStatus(ArtistStatus.PATCH)
            .build();
    }

    public File toFile(Long artistId) {
        ArtistImage image = artistImages.getFirst();

        return File.builder()
            .entityType(EntityType.ARTIST)
            .entityId(artistId)
            .fileType(FileType.IMAGE)
            .fileCategory(FileCategory.THUMBNAIL)
            .fileUrl(image.url)
            .width(image.width)
            .height(image.height)
            .fileOrder(1)
            .build();
    }
}