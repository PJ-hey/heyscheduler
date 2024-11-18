package hey.io.heyscheduler.common.client.kopis.dto;

import hey.io.heyscheduler.domain.artist.entity.Artist;
import hey.io.heyscheduler.domain.file.entity.File;
import hey.io.heyscheduler.domain.performance.entity.Performance;
import hey.io.heyscheduler.domain.performance.entity.PerformancePrice;
import hey.io.heyscheduler.domain.performance.entity.PerformanceTicketing;
import hey.io.heyscheduler.domain.performance.entity.Place;
import hey.io.heyscheduler.domain.performance.enums.PerformanceStatus;
import hey.io.heyscheduler.domain.performance.enums.PerformanceType;
import hey.io.heyscheduler.domain.performance.enums.TicketStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

public record KopisPerformanceResponse(
    String mt20id, // 공연 ID
    String prfnm, // 공연명
    String prfpdfrom, // 공연 시작일
    String prfpdto, // 공연 종료일
    String prfcast, // 공연 아티스트
    String prfruntime, // 공연 런타임
    String prfage, // 공연 관람 연령
    String pcseguidance, // 티켓 가격
    String poster, // 포스터 이미지경로
    String visit, // 방한 여부
    String festival, // 페스티벌 여부
    String prfstate, // 공연 상태
    List<String> styurls, // 소개이미지 목록
    String mt10id, // 공연시설 ID
    List<Relate> relates // 티켓 예매 정보
) {

    @Getter
    public static class Relate {

        String relatenm; // 티켓 예매처
        String relateurl; // 티켓 링크
    }

    public Performance toPerformance(Place place, List<PerformancePrice> priceList,
        List<PerformanceTicketing> ticketingList, List<File> fileList, List<Artist> artistList) {
        return Performance.builder()
            .performanceUid(mt20id)
            .performanceType(getPerformanceType(visit, festival))
            .name(prfnm)
            .startDate(LocalDate.parse(prfpdfrom, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .endDate(LocalDate.parse(prfpdto, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .place(place)
            .runningTime(prfruntime.isBlank() ? null : prfruntime)
            .viewingAge(prfage.isBlank() ? null : prfage)
            .ticketStatus(TicketStatus.READY)
            .performanceStatus(PerformanceStatus.INIT)
            .prices(priceList)
            .ticketings(ticketingList)
            .performanceFiles(fileList)
            .performanceArtists(artistList)
            .build();
    }

    private PerformanceType getPerformanceType(String visit, String festival) {
        if (Objects.equals(visit, "N") && Objects.equals(festival, "N")) {
            return PerformanceType.CONCERT_IN;
        }
        if (Objects.equals(visit, "Y") && Objects.equals(festival, "N")) {
            return PerformanceType.CONCERT_EX;
        }
        if (Objects.equals(visit, "N") && Objects.equals(festival, "Y")) {
            return PerformanceType.FESTIVAL_IN;
        }
        if (Objects.equals(visit, "Y") && Objects.equals(festival, "Y")) {
            return PerformanceType.FESTIVAL_EX;
        }
        return null;
    }
}