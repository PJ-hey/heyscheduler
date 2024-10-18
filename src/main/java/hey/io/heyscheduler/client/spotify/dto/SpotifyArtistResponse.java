package hey.io.heyscheduler.client.spotify.dto;

import hey.io.heyscheduler.domain.artist.dto.ArtistRequest;
import hey.io.heyscheduler.domain.artist.enums.ArtistStatus;
import hey.io.heyscheduler.domain.file.entity.File;
import hey.io.heyscheduler.domain.file.enums.EntityType;
import hey.io.heyscheduler.domain.file.enums.FileCategory;
import hey.io.heyscheduler.domain.file.enums.FileType;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Image;

public record SpotifyArtistResponse (
    String id, // Spotify 아티스트 ID
    String name, // 아티스트명
    String[] genres, // 아티스트 장르
    String externalUrl, // Spotify 아티스트 URL
    List<ArtistImage> artistImages, // 아티스트 이미지
    Integer popularity // 인기도
) {

    @Getter
    @Builder
    private static class ArtistImage {
        private String url; // 이미지 URL
        private Integer width; // 이미지 너비
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