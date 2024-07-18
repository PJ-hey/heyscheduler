package hey.io.heyscheduler.domain.artist.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArtistListResponse {

    private String id;

    private String artistName;

    private String artistImage;

    @QueryProjection
    public ArtistListResponse(String id, String artistName, String artistImage) {
        this.id = id;
        this.artistName = artistName;
        this.artistImage = artistImage;
    }
}