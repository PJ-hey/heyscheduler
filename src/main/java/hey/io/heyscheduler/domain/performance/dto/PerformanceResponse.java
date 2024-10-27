package hey.io.heyscheduler.domain.performance.dto;

import hey.io.heyscheduler.domain.performance.entity.Performance;
import java.util.List;

public record PerformanceResponse(
    int count,  // 등록 공연 수
    List<PerformanceResult> resultList // 등록된 공연 목록
) {

    public static PerformanceResponse of(List<Performance> performances) {
        List<PerformanceResult> performanceResults = performances.stream()
            .map(PerformanceResult::of)
            .toList();

        return new PerformanceResponse(
            performanceResults.size(),
            performanceResults
        );
    }

    public record PerformanceResult(
        Long performanceId,  // 공연 ID
        String name  // 공연명
    ) {

        public static PerformanceResult of(Performance performance) {
            return new PerformanceResult(
                performance.getPerformanceId(),
                performance.getName()
            );
        }
    }
}
