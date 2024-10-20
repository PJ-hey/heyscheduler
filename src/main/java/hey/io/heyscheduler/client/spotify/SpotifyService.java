package hey.io.heyscheduler.client.spotify;

import hey.io.heyscheduler.client.spotify.dto.SpotifyArtistResponse;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

@Service
@Slf4j
public class SpotifyService {

    @Value("${client.spotify.client-id}")
    private String clientId; // Spotify Client ID

    @Value("${client.spotify.client-secret}")
    private String clientSecret; // Spotify Client Secret

    private SpotifyApi spotifyApi;
    private ClientCredentialsRequest clientCredentialsRequest;
    private long tokenExpiryTime = 0; // 토큰 만료 시간 (Epoch Time)

    @PostConstruct
    private void init() {
        spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();

        clientCredentialsRequest = spotifyApi.clientCredentials().build();
        setAccessToken(); // 초기 Access Token 설정
    }

    /**
     * <p>Spotify Access Token 발급</p>
     */
    private void setAccessToken() {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            // 현재 시간 + 토큰 유효 기간을 계산하여 만료 시간 설정
            tokenExpiryTime = System.currentTimeMillis() + (clientCredentials.getExpiresIn() * 1000);
            log.info("Access Token set. Expires in: {} seconds", clientCredentials.getExpiresIn());
        } catch (SpotifyWebApiException | ParseException | IOException e) {
            log.error("Error while retrieving access token: {}", e.getMessage());
        }
    }

    /**
     * <p>
     * <b>Spotify Access Token 설정</b> <br/>
     * Access Token이 유효한지 체크하고, 유효하지 않으면 새로 발급
     * </p>
     */
    private void checkAccessToken() {
        if (System.currentTimeMillis() >= tokenExpiryTime) {
            log.info("Access Token has expired. Refreshing...");
            setAccessToken();
        }
    }

    /**
     * <p>Spotify 아티스트 목록</p>
     *
     * @param name 아티스트명
     * @return 아티스트 정보가 포함된 List<SpotifyArtistResponse>
     */
    public List<SpotifyArtistResponse> searchArtists(String name) {
        List<SpotifyArtistResponse> artists = new ArrayList<>();
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Artist name must not be blank");
        }

        try {
            checkAccessToken();

            Artist[] spotifyArtistList = Optional.ofNullable(
                    spotifyApi.searchArtists(name).build().execute().getItems())
                .filter(list -> list.length > 0)
                .orElseThrow(() -> new SpotifyWebApiException("No artists found"));

            artists = Arrays.stream(spotifyArtistList)
                .map(SpotifyArtistResponse::of)
                .toList();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error while searching artist: {}", name, e);
        }

        return artists;
    }

    /**
     * <p>Spotify 아티스트 상세</p>
     *
     * @param id Spotify 고유 ID
     * @return 아티스트 상세 정보
     */
    public SpotifyArtistResponse getArtist(String id) {
        SpotifyArtistResponse artist = null;
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Artist id must not be blank");
        }

        try {
            checkAccessToken();

            artist = SpotifyArtistResponse.of(spotifyApi.getArtist(id).build().execute());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error while getting artist by: {}", id, e);
        }

        return artist;
    }

    /**
     * <p>Spotify 아티스트 상세 목록</p>
     *
     * @param ids Spotify 고유 ID 목록
     * @return 아티스트 상세 정보
     */
    public List<SpotifyArtistResponse> getArtists(String[] ids) {
        List<SpotifyArtistResponse> artists = new ArrayList<>();
        if (ids.length == 0) {
            throw new IllegalArgumentException("Artist ids must not be blank");
        }
        if (ids.length > 50) {
            throw new IllegalArgumentException("The number of Artist ids must be less than 50");
        }

        try {
            checkAccessToken();

            artists = Arrays.stream(spotifyApi.getSeveralArtists(ids).build().execute())
                .map(SpotifyArtistResponse::of)
                .toList();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error while getting artists: {}", Arrays.toString(ids), e);
        }

        return artists;
    }
}
