package hey.io.heyscheduler.domain.performance.dto;

import com.querydsl.core.annotations.QueryProjection;

public record PerformanceArtistDTO(
    Long artistId,
    String artistName
) {

    @QueryProjection
    public PerformanceArtistDTO {
    }
}
