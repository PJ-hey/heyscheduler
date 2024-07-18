package hey.io.heyscheduler.kopis.client.dto;

import hey.io.heyscheduler.domain.performance.domain.Place;

public record KopisPlaceDetailResponse(
        String fcltynm,
        String mt10id,
        String adres,
        Double la,
        Double lo
) {

    public Place toEntity() {
        return Place.builder()
                .id(mt10id)
                .name(fcltynm)
                .address(adres)
                .latitude(la)
                .longitude(lo)
                .build();
    }

}