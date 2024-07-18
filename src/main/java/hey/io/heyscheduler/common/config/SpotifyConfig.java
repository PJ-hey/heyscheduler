

package hey.io.heyscheduler.common.config;

import java.io.IOException;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

public class SpotifyConfig {
    private static final String CLIENT_ID = "7440c1d8b36a41d497dfb7f65f715a1b";
    private static final String CLIENT_SECRET = "5285e1b026384c5d96ab5563b8699f3f";
    private static final SpotifyApi spotifyApi = (new SpotifyApi.Builder()).setClientId("7440c1d8b36a41d497dfb7f65f715a1b").setClientSecret("5285e1b026384c5d96ab5563b8699f3f").build();

    public SpotifyConfig() {
    }

    public static String accessToken() {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

        try {
            ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            return spotifyApi.getAccessToken();
        } catch (SpotifyWebApiException | ParseException | IOException var2) {
            System.out.println("Error: " + var2.getMessage());
            return "error";
        }
    }
}
