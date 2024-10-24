package hey.io.heyscheduler.domain.performance.controller;

import hey.io.heyscheduler.common.response.SuccessResponse;
import hey.io.heyscheduler.domain.performance.dto.PerformanceResponse;
import hey.io.heyscheduler.domain.performance.dto.PerformanceSearch;
import hey.io.heyscheduler.domain.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "1. Performance", description = "공연 관련 API")
public class PerformanceController {

    private final PerformanceService performanceService;

    /**
     * <p>공연 등록</p>
     *
     * @param searchDto 공연 조회 조건
     * @return 등록한 공연 목록
     */
    @Operation(summary = "공연 등록", description = "KOPIS 공연 데이터를 조회 후 등록합니다.")
    @PostMapping("/performances")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> createPerformances(
        @RequestBody PerformanceSearch searchDto) {

        List<PerformanceResponse> performanceResponses = performanceService.createPerformances(searchDto);

        // 응답에 포함할 데이터 (performanceResponses와 총 Insert 개수)
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("performances", performanceResponses);
        responseData.put("insertedCount", performanceResponses.size());

        return SuccessResponse.of(responseData).asHttp(HttpStatus.CREATED);
    }
}