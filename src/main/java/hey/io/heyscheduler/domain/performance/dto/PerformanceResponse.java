package hey.io.heyscheduler.domain.performance.dto;

import hey.io.heyscheduler.domain.performance.entity.Performance;

public record PerformanceResponse(
    Long performanceId,  // 공연 ID
    String name  // 공연명
) {

    public static PerformanceResponse of(Performance performance) {
        return new PerformanceResponse(
            performance.getPerformanceId(),
            performance.getName()
        );
    }
}
