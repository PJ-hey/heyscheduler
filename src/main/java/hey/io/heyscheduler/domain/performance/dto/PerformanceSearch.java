package hey.io.heyscheduler.domain.performance.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceSearch {

    private LocalDate startDate; // 공연 시작일
    private LocalDate endDate; // 공연 종료일
    private String name; // 공연명

    public static PerformanceSearch of(LocalDate startDate, LocalDate endDate, String name) {
        return PerformanceSearch.builder()
            .startDate(startDate)
            .endDate(endDate)
            .name(name)
            .build();
    }
}