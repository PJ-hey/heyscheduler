package hey.io.heyscheduler.kopis.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import hey.io.heyscheduler.domain.performance.domain.BoxOfficeRank;
import hey.io.heyscheduler.domain.performance.domain.Performance;
import hey.io.heyscheduler.domain.performance.domain.PerformancePrice;
import hey.io.heyscheduler.domain.performance.domain.Place;
import hey.io.heyscheduler.domain.performance.domain.enums.PerformanceStatus;
import hey.io.heyscheduler.domain.performance.domain.enums.TimePeriod;
import hey.io.heyscheduler.domain.performance.dto.PerformanceDetailResponse;
import hey.io.heyscheduler.domain.performance.mapper.PerformanceMapper;
import hey.io.heyscheduler.domain.performance.repository.BoxOfficeRankRepository;
import hey.io.heyscheduler.domain.performance.repository.PerformancePriceRepository;
import hey.io.heyscheduler.domain.performance.repository.PerformanceRepository;
import hey.io.heyscheduler.domain.performance.repository.PlaceRepository;
import hey.io.heyscheduler.fcm.service.FcmService;
import hey.io.heyscheduler.kopis.client.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static hey.io.heyscheduler.common.config.RedisCacheKey.PERFORMANCE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KopisBatchService {


    private final PerformancePriceRepository performancePriceRepository;
    private final PerformanceRepository performanceRepository;
    private final BoxOfficeRankRepository boxOfficeRankRepository;
    private final PlaceRepository placeRepository;
    private final KopisService kopisService;
    private final FcmService fcmService;
    private final CacheManager cacheManager;

    @CacheEvict(value = PERFORMANCE, allEntries = true)
    @Transactional
    public int updatePerformancesBatch(LocalDate from, LocalDate to, int rows) {
        long startTime = System.currentTimeMillis();

        log.info("[Batch] Batch Updating Performances...");
        logCacheState();

        KopisPerformanceRequest kopisPerformanceRequest = KopisPerformanceRequest.builder()
                .stdate(from.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .eddate(to.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .cpage(1)
                .rows(rows)
                .shcate("CCCD")
                .build();

        List<KopisPerformanceResponse> kopisPerformanceResponseList = kopisService.getPerformances(kopisPerformanceRequest);

        List<String> allIdList = performanceRepository.findAllIds();
        HashSet<String> allIdSet = new HashSet<>(allIdList);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        List<Performance> newPerformances = new ArrayList<>();

        for (KopisPerformanceResponse kopisPerformanceResponse : kopisPerformanceResponseList) {
            String performanceId = kopisPerformanceResponse.mt20id();
            if (!allIdSet.contains(performanceId)) {
                CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                    KopisPerformanceDetailResponse kopisPerformanceDetailResponse = kopisService.getPerformanceDetail(performanceId);
                    Performance performance = kopisPerformanceDetailResponse.toEntity();
                    String placeId = kopisPerformanceDetailResponse.mt10id();
                    KopisPlaceDetailResponse kopisPlaceDetailResponse = kopisService.getPlaceDetail(placeId);
                    Place place = kopisPlaceDetailResponse.toEntity();
                    placeRepository.save(place);
                    performance.updatePlace(place);
                    return performance;
                }, executorService).thenAccept(newPerformances::add);

                futures.add(future);
            }
        }

        futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        List<Performance> performances = performanceRepository.saveAll(newPerformances);
        List<PerformancePrice> performancePrices = performances.stream()
                .filter(performance -> StringUtils.hasText(performance.getPrice()))
                .flatMap(performance -> getPerformancePrice(performance).stream())
                .collect(Collectors.toList());

        performancePriceRepository.saveAll(performancePrices);

        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("[Batch] Performance has been Updated... Total Size: {}, New Performances Size: {}, Duration: {} ms", kopisPerformanceResponseList.size(), newPerformances.size(), duration);

        return performances.size();
    }

    private void logCacheState() {
        cacheManager.getCache("PERFORMANCE").clear();
        log.info("Cache 'PERFORMANCE' has been cleared.");
    }


    @Transactional
    public int updatePerformanceStatusBatch() {
        log.info("[Batch] Batch Updating Performance status");
        List<Performance> performanceList = performanceRepository.findAll();
        int updateCnt = 0;
        for (Performance performance : performanceList) {
            LocalDate today = LocalDate.now();
            LocalDate startDate = performance.getStartDate();
            LocalDate endDate = performance.getEndDate();
            PerformanceStatus performanceStatus;
            if (today.isAfter(endDate)) {
                performanceStatus = PerformanceStatus.COMPLETED;
            } else if (today.isBefore(startDate)) {
                performanceStatus = PerformanceStatus.UPCOMING;
            } else {
                performanceStatus = PerformanceStatus.ONGOING;
            }

            if (performance.getStatus() != performanceStatus) {
                performance.updateStatus(performanceStatus);
                updateCnt++;
            }
        }
        log.info("[Batch] Performance State has been Updated... Updated Count : {}", updateCnt);
        return updateCnt;
    }

    public int updateBoxOfficeRankBatch() {
        long startTime = System.currentTimeMillis();
        log.info("[Batch] Batch Updating Performance Rank...");
        boxOfficeRankRepository.deleteAll();
        TimePeriod[] timePeriods = TimePeriod.values();

        List<CompletableFuture<BoxOfficeRank>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (TimePeriod timePeriod : timePeriods) {
            CompletableFuture<BoxOfficeRank> future = CompletableFuture.supplyAsync(() -> {
                KopisBoxOfficeRequest kopisBoxOfficeRequest = KopisBoxOfficeRequest.builder()
                        .ststype(timePeriod.getValue())
                        .date(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .catecode("CCCD")
                        .build();

                List<KopisBoxOfficeResponse> kopisBoxOfficeResponseList = kopisService.getBoxOffice(kopisBoxOfficeRequest);

                String ids = kopisBoxOfficeResponseList.stream()
                        .filter(response -> checkIfNotCompleted(response.prfpd()))
                        .map(KopisBoxOfficeResponse::mt20id)
                        .collect(Collectors.joining("|"));

                return BoxOfficeRank.builder()
                        .timePeriod(timePeriod)
                        .performanceIds(ids)
                        .build();
            }, executorService);
            futures.add(future);
        }

        List<BoxOfficeRank> boxOfficeRankList = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        boxOfficeRankRepository.saveAll(boxOfficeRankList);

        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("[Batch] Box Office Rank has been Updated... size : {}, Duration: {} ms", boxOfficeRankList.size(), duration);
        return boxOfficeRankList.size();
    }


    public int sendPerformancesNotification() throws FirebaseMessagingException {
        log.info("[Batch] Batch Send Performance Notification...");
        List<Performance> performanceList = performanceRepository.getPerformancesByStartDate();
        int sendCnt = 0;
        for (Performance performance : performanceList) {
            fcmService.sendMessageByTopic(performance.getTitle(), "D-1", "Performance");
            sendCnt++;
        }
        return sendCnt;
    }

    private PerformanceDetailResponse getPerformanceDetailResponse(Performance performance) {
        PerformanceDetailResponse performanceDetailResponse = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
        performanceDetailResponse.updateStoryUrls(performance.getStoryUrls());

        Place place = performance.getPlace();
        if (!ObjectUtils.isEmpty(place)) {
            performanceDetailResponse.updateLocation(place.getLatitude(), place.getLongitude());
            performanceDetailResponse.setAddress(place.getAddress());
            performanceDetailResponse.setPlaceId(place.getId());
        }
        return performanceDetailResponse;
    }

    private List<PerformancePrice> getPerformancePrice(Performance performance) {
        String price = performance.getPrice();
        String replacedPrice = price.replace(",", "");
        String[] splitString = replacedPrice.split(" ");

        return Arrays.stream(splitString)
                .filter(str -> str.endsWith("00원"))
                .map(str -> PerformancePrice.builder()
                        .price(Integer.parseInt(str.substring(0, str.length() - 1)))
                        .performance(performance)
                        .build())
                .collect(Collectors.toList());
    }

    private boolean checkIfNotCompleted(String period) {
        String strEndDate = period.contains("~") ? period.split("~")[1] : period;
        LocalDate endDate = LocalDate.parse(strEndDate, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        return LocalDate.now().isBefore(endDate) || LocalDate.now().isEqual(endDate);
    }
}