package hey.io.heyscheduler.common.client.kopis.dto;

import hey.io.heyscheduler.domain.performance.entity.Place;

public record KopisPlaceResponse(
    String mt10id, // 공연 장소 ID
    String fcltynm, // 공연 장소명
    String adres, // 주소
    Double la, // 위도
    Double lo // 경도
) {

    public Place toPlace() {
        return Place.builder()
            .placeUid(mt10id)
            .name(fcltynm)
            .address(adres)
            .latitude(la)
            .longitude(lo)
            .build();
    }
}