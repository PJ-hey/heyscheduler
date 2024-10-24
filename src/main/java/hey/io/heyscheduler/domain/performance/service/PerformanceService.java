package hey.io.heyscheduler.domain.performance.service;

import hey.io.heyscheduler.client.kopis.KopisFeignClient;
import hey.io.heyscheduler.client.kopis.dto.KopisPerformanceRequest;
import hey.io.heyscheduler.client.kopis.dto.KopisPerformanceResponse;
import hey.io.heyscheduler.client.kopis.dto.KopisPerformanceResponse.Relate;
import hey.io.heyscheduler.client.kopis.dto.KopisPlaceResponse;
import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.exception.notfound.EntityNotFoundException;
import hey.io.heyscheduler.domain.artist.entity.Artist;
import hey.io.heyscheduler.domain.artist.enums.ArtistStatus;
import hey.io.heyscheduler.domain.artist.enums.ArtistType;
import hey.io.heyscheduler.domain.artist.repository.ArtistRepository;
import hey.io.heyscheduler.domain.file.entity.File;
import hey.io.heyscheduler.domain.file.enums.EntityType;
import hey.io.heyscheduler.domain.file.enums.FileCategory;
import hey.io.heyscheduler.domain.file.enums.FileType;
import hey.io.heyscheduler.domain.file.repository.FileRepository;
import hey.io.heyscheduler.domain.performance.dto.PerformanceResponse;
import hey.io.heyscheduler.domain.performance.dto.PerformanceSearch;
import hey.io.heyscheduler.domain.performance.entity.Performance;
import hey.io.heyscheduler.domain.performance.entity.PerformancePrice;
import hey.io.heyscheduler.domain.performance.entity.PerformanceTicketing;
import hey.io.heyscheduler.domain.performance.entity.Place;
import hey.io.heyscheduler.domain.performance.repository.PerformanceRepository;
import hey.io.heyscheduler.domain.performance.repository.PlaceRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PlaceRepository placeRepository;
    private final ArtistRepository artistRepository;
    private final FileRepository fileRepository;
    private final KopisFeignClient kopisFeignClient;

    @Value("${client.kopis.api-key}")
    private String apiKey; // KOPIS API key

    @Value("${spring.task.execution.pool.core-size}")
    private Integer poolCoreSize; // 스레드 풀 크기

    /**
     * <p>공연 등록</p>
     *
     * @param searchDto 공연 조회 조건
     * @return 등록한 공연 목록
     */
    @Transactional
    public List<PerformanceResponse> createPerformances(PerformanceSearch searchDto) {
        List<PerformanceResponse> performanceList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(poolCoreSize);
        KopisPerformanceRequest request = KopisPerformanceRequest.of(searchDto.getStartDate(), searchDto.getEndDate(),
            searchDto.getName());

        // 1. KOPIS 공연 리스트 조회
        List<KopisPerformanceResponse> kopisPerformanceList = kopisFeignClient.searchPerformances(request, apiKey);

        // 2. 공연 정보 조회
        List<CompletableFuture<Performance>> futurePerformanceList = kopisPerformanceList.stream()
            .filter(this::checkExistPerformance) // 중복 공연 등록 생략
            .map(response -> CompletableFuture.supplyAsync(
                () -> getPerformance(response.mt20id()), executorService))
            .toList();

        // 3. 조회한 정보 취합
        List<Performance> searchPerformanceList = futurePerformanceList.stream()
            .map(CompletableFuture::join) // 각 Future의 결과를 기다림
            .collect(Collectors.toList());
        executorService.shutdown();

        // 4. 공연 정보 등록
        if (!searchPerformanceList.isEmpty()) {
            performanceList = insertPerformances(searchPerformanceList);
        } else {
            log.info("No new performances to insert.");
        }

        return performanceList;
    }

    private boolean checkExistPerformance(KopisPerformanceResponse response) {
        String performanceUid = response.mt20id();
        boolean exists = performanceRepository.existsPerformanceByPerformanceUid(performanceUid);
        if (exists) {
            log.info("Performance with UID: {} already exists, skipping...", performanceUid);
        }
        return !exists;
    }

    private List<PerformanceResponse> insertPerformances(List<Performance> performanceList) {
        // 공연 정보 등록
        List<Performance> resultList = performanceRepository.saveAll(performanceList);

        // 공연 이미지 등록
        resultList.forEach(performance -> {
            List<File> files = performance.getFiles();

            if (!files.isEmpty()) {
                files.forEach(file -> file.updatePerformanceFile(performance));
                fileRepository.saveAll(files);
            }
        });

        log.info("Inserted {} new performances.", performanceList.size());
        return resultList.stream()
            .map(PerformanceResponse::of)
            .toList();
    }

    /**
     * <p>공연 상세</p>
     *
     * @param performanceUid KOPIS 공연 ID
     * @return 공연 상세 정보 (장소, 가격, 예매, 아티스트, 파일 정보 포함)
     */
    private Performance getPerformance(String performanceUid) {
        log.info("New performance found with UID: {}", performanceUid);

        // 1. 공연 상세 조회
        KopisPerformanceResponse kopisPerformance = Optional.ofNullable(
                kopisFeignClient.getPerformance(performanceUid, apiKey).getFirst())
            .filter(response -> response.mt20id() != null)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PERFORMANCE_NOT_FOUND));

        // 2. 장소 조회 및 등록
        Place place = createPlace(kopisPerformance.mt10id());

        // 3. 공연 가격 설정
        List<PerformancePrice> priceList = setPerformancePrices(kopisPerformance.pcseguidance());

        // 4. 공연 예매 설정
        List<PerformanceTicketing> ticketingList = setPerformanceTicketings(kopisPerformance.relates());

        // 5. 공연 이미지 설정
        List<File> fileList = setPerformanceFiles(kopisPerformance.poster(), kopisPerformance.styurls());

        // 6. 아티스트 설정
        List<Artist> artistList = setPerformanceArtists(kopisPerformance.prfcast());

        // 7. 공연 엔티티 생성
        return kopisPerformance.toPerformance(place, priceList, ticketingList, fileList, artistList);
    }

    /**
     * <p>장소 조회 및 등록</p>
     *
     * @param placeUid KOPIS 장소 ID
     * @return 공연 장소 정보
     */
    private Place createPlace(String placeUid) {
        // 장소 정보 확인
        Optional<Place> existingPlace = placeRepository.findByPlaceUid(placeUid);
        if (existingPlace.isPresent()) {
            return existingPlace.get();
        }

        synchronized (this) {
            // 동기화 스레드 내에서 장소 정보 확인
            return placeRepository.findByPlaceUid(placeUid)
                .orElseGet(() -> { // 조회한 장소 정보가 없을 경우
                    // 장소 상세 조회
                    KopisPlaceResponse kopisPlace = Optional.ofNullable(
                            kopisFeignClient.getPlaces(placeUid, apiKey).getFirst())
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PLACE_NOT_FOUND));

                    // 장소 등록
                    Place place = kopisPlace.toPlace();
                    placeRepository.save(place);
                    log.info("New place registered with UID: {}", placeUid);
                    return place;
                });
        }
    }

    /**
     * <p>공연 가격 설정</p>
     *
     * @param priceText 외부 API로부터 조회된 공연 가격 정보 (쉼표로 구분된 문자열)
     * @return 공연 가격 정보가 포함된 List<PerformancePrice>
     */
    private List<PerformancePrice> setPerformancePrices(String priceText) {
        List<PerformancePrice> prices = new ArrayList<>();

        // 1. 가격 정보 + 금액 추출
        Pattern pattern = Pattern.compile("([가-힣a-zA-Z0-9 ]+) (([0-9,]+원?)|무료)");
        Matcher matcher = pattern.matcher(priceText);

        // 2. 매칭되는 가격 정보와 금액 파싱
        while (matcher.find()) {
            String priceInfo = matcher.group(1).trim(); // 가격 정보
            int priceAmount;

            if (matcher.group(2).equals("무료")) {
                priceAmount = 0;
            } else {
                // 금액 : 숫자만 추출하여 쉼표 제거 후 정수로 변환
                priceAmount = Integer.parseInt(matcher.group(3).replace(",", "").replace("원", ""));
            }

            PerformancePrice price = PerformancePrice.builder()
                .priceInfo(priceInfo)
                .priceAmount(priceAmount)
                .build();

            prices.add(price);
        }

        return prices;
    }

    /**
     * <p>공연 예매 설정</p>
     *
     * @param ticketRelates 외부 API로부터 조회된 공연 예매 정보
     * @return 공연 예매 정보가 포함된 List<PerformanceTicketing>
     */
    private List<PerformanceTicketing> setPerformanceTicketings(List<Relate> ticketRelates) {
        List<PerformanceTicketing> ticketings = new ArrayList<>();

        for (Relate ticketRelate : ticketRelates) {
            PerformanceTicketing ticketing = PerformanceTicketing.builder()
                .ticketingBooth(ticketRelate.getRelatenm())
                .ticketingUrl(ticketRelate.getRelateurl())
                .build();

            ticketings.add(ticketing);
        }

        return ticketings;
    }

    /**
     * <p>공연 이미지 설정</p>
     *
     * @param poster 포스터 이미지 경로
     * @param styurls 소개 이미지 목록
     * @return 공연 이미지 정보가 포함된 List<File>
     */
    private List<File> setPerformanceFiles(String poster, List<String> styurls) {
        List<File> files = new ArrayList<>();

        setPerformanceImg(poster, files);
        setPerformanceDetailImg(styurls, files);

        return files;
    }

    private void setPerformanceImg(String poster, List<File> files) {
        if (!StringUtils.isBlank(poster)) {
            File file = File.builder()
                .entityType(EntityType.PERFORMANCE)
                .fileType(FileType.IMAGE)
                .fileCategory(FileCategory.THUMBNAIL)
                .fileUrl(poster)
                .fileOrder(1)
                .build();

            files.add(file);
        }
    }

    private void setPerformanceDetailImg(List<String> styurls, List<File> files) {
        if (!styurls.isEmpty()) {
            for (int i = 0; i < styurls.size(); i++) {
                File file = File.builder()
                    .entityType(EntityType.PERFORMANCE)
                    .fileType(FileType.IMAGE)
                    .fileCategory(FileCategory.DETAIL)
                    .fileUrl(styurls.get(i))
                    .fileOrder(i + 1)
                    .build();

                files.add(file);
            }
        }
    }

    /**
     * <p>아티스트 설정</p>
     *
     * @param artistText 외부 API로부터 조회된 아티스트 정보 (쉼표로 구분된 문자열)
     * @return 아티스트 정보가 포함된 List<Artist>
     */
    private List<Artist> setPerformanceArtists(String artistText) {
        List<Artist> artists = new ArrayList<>();

        // 1. 아티스트 이름 목록 추출
        List<String> artistNames = getArtistName(artistText);
        if (artistNames.isEmpty()) {
            return artists;
        }

        // 2. 아티스트 조회 및 등록
        for (String artistName : artistNames) {
            artists.add(insertArtist(artistName));
        }

        return artists;
    }

    /**
     * <p>아티스트 등록</p>
     *
     * @param artistName 아티스트명
     * @return 아티스트 정보
     */
    private Artist insertArtist(String artistName) {
        // 아티스트 정보 확인
        Optional<Artist> existingArtist = artistRepository.findFirstByOrgName(artistName);
        if (existingArtist.isPresent()) {
            return existingArtist.get();
        }

        synchronized (this) {
            // 동기화 스레드 내에서 아티스트 정보 확인
            return artistRepository.findFirstByOrgName(artistName)
                .orElseGet(() -> {
                    // 아티스트가 없으면 새로 생성하여 저장
                    Artist newArtist = Artist.builder()
                        .name(artistName)
                        .orgName(artistName)
                        .artistType(ArtistType.SOLO)
                        .artistStatus(ArtistStatus.INIT)
                        .build();

                    artistRepository.save(newArtist);
                    log.info("New artist registered with name: {}", artistName);
                    return newArtist;
                });
        }
    }

    private List<String> getArtistName(String artistText) {
        if (StringUtils.isBlank(artistText)) {
            return new ArrayList<>();
        }

        // "등"으로 끝나는 경우 "등" 제거
        if (artistText.endsWith(" 등")) {
            artistText = artistText.substring(0, artistText.length() - 2).trim();
        }

        return Arrays.stream(artistText.split(","))
            .map(String::trim)
            .filter(name -> !name.isBlank())
            .toList();
    }
}