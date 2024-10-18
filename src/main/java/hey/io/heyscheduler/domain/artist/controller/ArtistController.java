package hey.io.heyscheduler.domain.artist.controller;

import hey.io.heyscheduler.client.spotify.SpotifyService;
import hey.io.heyscheduler.client.spotify.dto.SpotifyArtistResponse;
import hey.io.heyscheduler.common.response.SuccessResponse;
import hey.io.heyscheduler.domain.artist.dto.ArtistResponse;
import hey.io.heyscheduler.domain.artist.service.ArtistService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ArtistController {

    private final ArtistService artistService;
    private final SpotifyService spotifyService;

    /**
     * <p>Spotify 아티스트 목록</p>
     *
     * @param name 아티스트명
     * @return 조회한 아티스트 목록
     */
    @GetMapping("/spotify/artists")
    public ResponseEntity<SuccessResponse<List<SpotifyArtistResponse>>> searchSpotifyArtists(@RequestParam String name) {
        return SuccessResponse.of(spotifyService.searchArtists(name)).asHttp(HttpStatus.OK);
    }

    /**
     * <p>아티스트 정보 일괄 수정</p>
     *
     * @param artistUids Spotify 아티스트 ID 목록
     * @return 수정한 아티스트 정보 목록
     */
    @PutMapping("/artists")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> modifyArtists(@RequestBody String[] artistUids) {
        List<ArtistResponse> artistResponses = artistService.modifyArtists(artistUids);

        // 응답에 포함할 데이터 (artistResponses 총 Insert 개수)
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("artists", artistResponses);
        responseData.put("insertedCount", artistResponses.size());

        return SuccessResponse.of(responseData).asHttp(HttpStatus.CREATED);
    }
}