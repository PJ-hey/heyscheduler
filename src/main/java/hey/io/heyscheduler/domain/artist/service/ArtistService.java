package hey.io.heyscheduler.domain.artist.service;

import hey.io.heyscheduler.client.spotify.SpotifyService;
import hey.io.heyscheduler.client.spotify.dto.SpotifyArtistResponse;
import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.exception.badrequest.InvalidParameterException;
import hey.io.heyscheduler.common.exception.notfound.EntityNotFoundException;
import hey.io.heyscheduler.domain.artist.dto.ArtistRequest;
import hey.io.heyscheduler.domain.artist.dto.ArtistResponse;
import hey.io.heyscheduler.domain.artist.entity.Artist;
import hey.io.heyscheduler.domain.artist.repository.ArtistRepository;
import hey.io.heyscheduler.domain.file.entity.File;
import hey.io.heyscheduler.domain.file.repository.FileRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

    private final SpotifyService spotifyService;
    private final ArtistRepository artistRepository;
    private final FileRepository fileRepository;

    /**
     * <p>
     * <b>아티스트 정보 일괄 수정</b> <br/>
     * Spotify의 아티스트 정보를 DB로 덮어쓰기
     * </p>
     *
     * @param artistUids Spotify 아티스트 ID 목록
     * @return 수정한 아티스트 정보 목록
     */
    @Transactional
    public List<ArtistResponse> modifyArtists(String[] artistUids) {
        if (artistUids.length == 0) {
            throw new InvalidParameterException(ErrorCode.INVALID_ARTIST_ID);
        }

        // 1. Spotify 아티스트 목록 조회
        List<SpotifyArtistResponse> spotifyArtistList = Optional.ofNullable(
                spotifyService.getArtists(artistUids))
            .filter(list -> !list.isEmpty())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ARTIST_NOT_FOUND));

        // 2. 아티스트 엔티티 설정
        List<Artist> artistList = setArtists(spotifyArtistList);

        // 3. 아티스트 이미지 설정
        List<File> fileList = setArtistFiles(artistList, spotifyArtistList);

        // 4. 아티스트 정보 수정
        return updateArtists(artistList, fileList);
    }

    /**
     * <p>아티스트 목록 설정</p>
     *
     * @param spotifyArtistList Spotify 조회 아티스트 목록
     * @return 아티스트 정보가 포함된 List<Artist>
     */
    private List<Artist> setArtists(List<SpotifyArtistResponse> spotifyArtistList) {
        // 매핑된 아티스트 엔티티 목록 조회
        List<Artist> artistList = artistRepository.findByArtistUidIn(SpotifyArtistResponse.ofId(spotifyArtistList));

        // SpotifyArtistResponse 데이터를 Artist 엔티티로 update
        return artistList.stream()
            .map(artist -> artist.updateArtist(getArtistRequest(spotifyArtistList, artist)))
            .toList();
    }

    /**
     * <p>
     * <b>아티스트 Request 생성</b> <br/>
     * SpotifyArtistResponse 정보를 해당 아티스트 엔티티에 적용 후 request 생성
     * </p>
     *
     * @param spotifyArtistList Spotify 조회 아티스트 목록
     * @param artist 아티스트 엔티티
     * @return 입력할 아티스트 정보
     */
    private ArtistRequest getArtistRequest(List<SpotifyArtistResponse> spotifyArtistList, Artist artist) {
        return spotifyArtistList.stream()
            .filter(spotifyArtist -> spotifyArtist.id().equals(artist.getArtistUid()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ARTIST_NOT_FOUND, "id: " + artist.getArtistUid()))
            .toRequest();
    }

    /**
     * <p>아티스트 이미지 설정</p>
     *
     * @param artistList 아티스트 엔티티 목록
     * @param spotifyArtistList Spotify 조회 아티스트 목록
     * @return 아티스트 이미지 정보가 포함된 List<File>
     */
    private List<File> setArtistFiles(List<Artist> artistList, List<SpotifyArtistResponse> spotifyArtistList) {
        return spotifyArtistList.stream()
            .map(spotifyArtistResponse -> spotifyArtistResponse.toFile(
                getArtistId(artistList, spotifyArtistResponse.id())))
            .toList();
    }

    /**
     * <p>
     * <b>아티스트 ID 조회</b> <br/>
     * Spotify 아티스트 ID로 Artist 엔티티의 artistId를 조회
     * </p>
     *
     * @param artistList 아티스트 엔티티 목록
     * @param id Spotify 아티스트 ID
     * @return Artist 엔티티 PK (= artistId)
     */
    private Long getArtistId(List<Artist> artistList, String id) {
        return artistList.stream()
            .filter(artist -> artist.getArtistUid().equals(id))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ARTIST_NOT_FOUND, "id: " + id))
            .getArtistId();
    }

    /**
     * <p>아티스트 수정</p>
     *
     * @param artistList 아티스트 엔티티 목록
     * @param fileList 아티스트 이미지 목록
     * @return 수정한 아티스트 목록
     */
    private List<ArtistResponse> updateArtists(List<Artist> artistList, List<File> fileList) {
        List<Artist> resultList = artistRepository.saveAll(artistList);
        fileRepository.saveAll(fileList);

        log.info("Updated {} new artists.", artistList.size());
        log.info("Updated {} new files.", fileList.size());
        return resultList.stream()
            .map(ArtistResponse::of)
            .toList();
    }
}