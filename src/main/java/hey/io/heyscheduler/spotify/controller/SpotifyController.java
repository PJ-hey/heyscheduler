
package hey.io.heyscheduler.spotify.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import hey.io.heyscheduler.common.response.SuccessResponse;
import hey.io.heyscheduler.spotify.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/batch"})
public class SpotifyController {
    private final SpotifyService spotifyService;

    @GetMapping({"/artists/notification"})
    public ResponseEntity<SuccessResponse<Integer>> sendArtistsNotification() throws FirebaseMessagingException {
        return SuccessResponse.of(this.spotifyService.sendArtistsNotification()).asHttp(HttpStatus.OK);
    }

    @GetMapping({"/artists"})
    public ResponseEntity<SuccessResponse<Integer>> updateArtistsBatch() throws IOException, ParseException, SpotifyWebApiException, ExecutionException, InterruptedException {
        return SuccessResponse.of(this.spotifyService.updateArtistsBatch()).asHttp(HttpStatus.OK);
    }

    @GetMapping({"/artists/album"})
    public ResponseEntity<SuccessResponse<Integer>> updateAlbumsBatch() throws IOException, ParseException, SpotifyWebApiException {
        return SuccessResponse.of(this.spotifyService.updateAlbumsBatch()).asHttp(HttpStatus.OK);
    }

}
