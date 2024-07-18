//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hey.io.heyscheduler.kopis.service;

import hey.io.heyscheduler.kopis.client.KopisFeignClient;
import hey.io.heyscheduler.kopis.client.dto.KopisBoxOfficeRequest;
import hey.io.heyscheduler.kopis.client.dto.KopisBoxOfficeResponse;
import hey.io.heyscheduler.kopis.client.dto.KopisPerformanceDetailResponse;
import hey.io.heyscheduler.kopis.client.dto.KopisPerformanceRequest;
import hey.io.heyscheduler.kopis.client.dto.KopisPerformanceResponse;
import hey.io.heyscheduler.kopis.client.dto.KopisPlaceDetailResponse;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KopisService {

    @Value("${kopis.api.key}")
    private String apiKey;


    private final KopisFeignClient kopisFeignClient;

    public List<KopisPerformanceResponse> getPerformances(KopisPerformanceRequest kopisPerformanceRequest) {
        return kopisFeignClient.getPerformances(kopisPerformanceRequest, apiKey);
    }

    public KopisPerformanceDetailResponse getPerformanceDetail(String performanceId) {
        KopisPerformanceDetailResponse kopisPerformanceDetailResponse = kopisFeignClient.getPerformanceDetail(performanceId, apiKey, "Y").get(0);
        if (kopisPerformanceDetailResponse.mt20id() == null) {
            throw new IllegalStateException("Fail to GET Performance Detail..");
        }
        return kopisPerformanceDetailResponse;
    }

    public List<KopisBoxOfficeResponse> getBoxOffice(KopisBoxOfficeRequest kopisBoxOfficeRequest) {
        return kopisFeignClient.getBoxOffice(kopisBoxOfficeRequest, apiKey);
    }

    public KopisPlaceDetailResponse getPlaceDetail(String placeId) {
        return kopisFeignClient.getPlaceDetail(placeId, apiKey).get(0);
    }
}