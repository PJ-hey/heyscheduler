package hey.io.heyscheduler.common.client.kopis;

import hey.io.heyscheduler.common.client.kopis.dto.KopisPerformanceRequest;
import hey.io.heyscheduler.common.client.kopis.dto.KopisPerformanceResponse;
import hey.io.heyscheduler.common.client.kopis.dto.KopisPlaceResponse;
import hey.io.heyscheduler.common.config.FeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "kopisFeignClient",
    url = "${client.kopis.url:url}",
    configuration = {FeignConfig.class}
)
public interface KopisFeignClient {

    @GetMapping(
        value = {"/pblprfr"},
        produces = {"application/xml;charset=UTF-8"}
    )
    List<KopisPerformanceResponse> searchPerformances(@SpringQueryMap KopisPerformanceRequest request, @RequestParam("service") String apiKey);

    @GetMapping(
        value = {"/pblprfr/{mt20id}"},
        produces = {"application/xml;charset=UTF-8"}
    )
    List<KopisPerformanceResponse> getPerformance(@PathVariable("mt20id") String mt20id, @RequestParam("service") String apiKey);

    @GetMapping(
        value = {"/prfplc/{mt10id}"},
        produces = {"application/xml;charset=UTF-8"}
    )
    List<KopisPlaceResponse> getPlaces(@PathVariable("mt10id") String mt10id, @RequestParam("service") String apiKey);
}
