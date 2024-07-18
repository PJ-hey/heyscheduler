
package hey.io.heyscheduler.spotify.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import hey.io.heyscheduler.common.config.SpotifyConfig;
import hey.io.heyscheduler.domain.album.domain.AlbumEntity;
import hey.io.heyscheduler.domain.album.repository.AlbumRepository;
import hey.io.heyscheduler.domain.artist.domain.ArtistEntity;
import hey.io.heyscheduler.domain.artist.dto.ArtistListResponse;
import hey.io.heyscheduler.domain.artist.repository.ArtistRepository;
import hey.io.heyscheduler.domain.performance.domain.Performance;
import hey.io.heyscheduler.domain.performance.domain.PerformanceArtist;
import hey.io.heyscheduler.domain.performance.repository.PerformanceArtistRepository;
import hey.io.heyscheduler.domain.performance.repository.PerformanceRepository;
import hey.io.heyscheduler.fcm.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotifyService {
    private final ArtistRepository artistRepository;
    private final PerformanceRepository performanceRepository;
    private final PerformanceArtistRepository performanceArtistRepository;
    private final AlbumRepository albumRepository;
    private final FcmService fcmService;
    SpotifyApi spotifyApi = (new SpotifyApi.Builder()).setAccessToken(SpotifyConfig.accessToken()).build();

    @Transactional
    public int updateArtistsBatch() throws IOException, ParseException, SpotifyWebApiException {
        long startTime = System.currentTimeMillis();
        log.info("[Batch] Batch Updating Artists...");

        List<Performance> performanceList = performanceRepository.findAll();
        List<String> allIdList = artistRepository.findAllIds();
        HashSet<String> allIdSet = new HashSet<>(allIdList);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (Performance performance : performanceList) {
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                if (performance.getCast() != null) {
                    String artistNames = performance.getCast();
                    String[] artistNameArray = artistNames.split(", ");

                    if (artistNameArray[artistNameArray.length - 1].endsWith(" 등")) {
                        artistNameArray[artistNameArray.length - 1] = artistNameArray[artistNameArray.length - 1].replace(" 등", "");
                    }

                    List<String> artistNameList = new ArrayList<>(Arrays.asList(artistNameArray));

                    for (String artistName : artistNameList) {
                        try {
                            SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(artistName).limit(1).build();
                            Paging<Artist> searchArtistResponse = searchArtistsRequest.execute();
                            Artist[] searchArtistResult = searchArtistResponse.getItems();

                            if (searchArtistResult == null || searchArtistResult.length == 0) {
                                continue;
                            }

                            if (!allIdSet.contains(searchArtistResult[0].getId())) {
                                String artistImageUrl = null;
                                if (searchArtistResult[0].getImages() != null && searchArtistResult[0].getImages().length > 0) {
                                    artistImageUrl = searchArtistResult[0].getImages()[0].getUrl();
                                }
                                ArtistEntity artist = ArtistEntity.of(
                                        searchArtistResult[0].getId(),
                                        searchArtistResult[0].getName(),
                                        artistImageUrl,
                                        Arrays.asList(searchArtistResult[0].getGenres())
                                );

                                artistRepository.save(artist);
                                allIdSet.add(searchArtistResult[0].getId());

                                PerformanceArtist performanceArtist = PerformanceArtist.of(performance, artist);
                                performanceArtistRepository.save(performanceArtist);
                            }
                        } catch (IOException | SpotifyWebApiException | ParseException e) {
                            log.error("Error while searching artist: {}", artistName, e);
                        }
                    }
                }
                return null;
            }, executorService).thenAccept(aVoid -> {});
            futures.add(future);
        }

        futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        int updateCnt = allIdSet.size() - allIdList.size();
        log.info("[Batch] Artists have been Updated... Total Size: {}, Duration: {} ms", updateCnt, duration);

        return updateCnt;
    }

    public int sendArtistsNotification() throws FirebaseMessagingException {
        log.info("[Batch] Batch Send Artist Notification...");
        List<ArtistListResponse> artistList = performanceArtistRepository.getArtistsByPerformanceStartDate();
        int sendCnt = 0;
        for (ArtistListResponse artist : artistList) {
            fcmService.sendMessageByTopic(artist.getArtistName(), "D-1", "Artist");
            sendCnt++;
        }
        return sendCnt;
    }

    @Transactional
    public int updateAlbumsBatch() {
        log.info("[Batch] Batch Updating Albums...");
        long startTime = System.currentTimeMillis();

        Pageable pageable = PageRequest.of(0, 100);
        Page<ArtistEntity> artistPage;

        List<String> allIdList = albumRepository.findAllIds();
        HashSet<String> allIdSet = new HashSet<>(allIdList);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        do {
            artistPage = artistRepository.findAll(pageable);

            for (ArtistEntity artist : artistPage.getContent()) {
                CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                    String artistId = artist.getId();

                    try {
                        GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(artistId).build();
                        Paging<AlbumSimplified> getArtistsAlbumsResponse = getArtistsAlbumsRequest.execute();
                        AlbumSimplified[] albums = getArtistsAlbumsResponse.getItems();

                        for (AlbumSimplified albumSimplified : albums) {
                            String albumId = albumSimplified.getId();
                            if (!allIdSet.contains(albumId)) {
                                AlbumEntity albumEntity = albumRepository.findById(albumId).orElse(null);

                                if (albumEntity == null) {
                                    albumEntity = AlbumEntity.of(
                                            albumId,
                                            albumSimplified.getName(),
                                            albumSimplified.getImages()[0].getUrl(),
                                            albumSimplified.getReleaseDate()
                                    );
                                    albumEntity.setArtist(artist);
                                    albumRepository.save(albumEntity);
                                    allIdSet.add(albumId);
                                }
                            }
                        }
                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        log.error("Error updating albums for artist {}: {}", artistId, e.getMessage());
                    }
                    return null;
                }, executorService);
                futures.add(future);
            }

            pageable = artistPage.nextPageable();
        } while (artistPage.hasNext());

        futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        executorService.shutdown();

        int updateCnt = allIdSet.size() - allIdList.size();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("[Batch] Albums have been updated. Total Size: {}, Duration: {} ms", updateCnt, duration);

        return updateCnt;
    }

}