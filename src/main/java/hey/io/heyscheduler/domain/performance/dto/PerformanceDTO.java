package hey.io.heyscheduler.domain.performance.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heyscheduler.domain.push.enums.PushType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PerformanceDTO {

    @Getter
    @NoArgsConstructor
    public static class PerformanceTicketingDTO {

        private Long performanceId;
        @Setter
        private String performanceName;
        @Setter
        private PushType ticketingType;
        private LocalDateTime openDatetime;

        @QueryProjection
        public PerformanceTicketingDTO(Long performanceId, LocalDateTime openDatetime) {
            this.performanceId = performanceId;
            this.openDatetime = openDatetime;
        }
    }

    @Getter
    public static class PerformanceArtistDTO {

        private final Long artistId;
        private final String artistName;

        @QueryProjection
        public PerformanceArtistDTO(Long artistId, String artistName) {
            this.artistId = artistId;
            this.artistName = artistName;
        }
    }
}
