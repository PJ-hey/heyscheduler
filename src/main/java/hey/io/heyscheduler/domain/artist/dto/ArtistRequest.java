package hey.io.heyscheduler.domain.artist.dto;

import hey.io.heyscheduler.domain.artist.enums.ArtistStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ArtistRequest {

    private String name; // 아티스트명
    private String engName; // 아티스트 영문명
    private String artistUid; // Spotify 아티스트 ID
    private String artistUrl; // 아티스트 URL
    private Integer popularity; // 아티스트 인기도
    private ArtistStatus artistStatus; // 아티스트 상태
}