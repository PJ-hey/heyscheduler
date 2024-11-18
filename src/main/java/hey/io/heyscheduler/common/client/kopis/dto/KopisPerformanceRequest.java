package hey.io.heyscheduler.common.client.kopis.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KopisPerformanceRequest {

    private String stdate; // 공연 시작일
    private String eddate; // 공연 종료일
    private String shprfnm; // 공연명
    private int cpage; // 현재 페이지
    private int rows; // 페이지당 목록 수
    private String shcate; // 장르 코드

    public static KopisPerformanceRequest of(LocalDate startDate, LocalDate endDate, String name) {
        return KopisPerformanceRequest.builder()
            .stdate(startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .eddate(endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .shprfnm(name)
            .cpage(1)
            .rows(100)
            .shcate("CCCD")
            .build();
    }
}