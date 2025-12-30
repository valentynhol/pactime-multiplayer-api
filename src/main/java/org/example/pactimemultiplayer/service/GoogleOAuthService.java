package org.example.pactimemultiplayer.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleOAuthService {
    @Value("${security.google.client-id}")
    private String googleClientId;

    @Value("${security.google.client-secret}")
    private String googleClientSecret;

    @Value("${config.api-url}")
    private String apiUrl;

    public GoogleIdToken getGoogleIdToken(String code)
            throws IOException {
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance(),
                        googleClientId,
                        googleClientSecret,
                        code,
                        apiUrl + "/auth/google/callback"
                ).execute();

        return tokenResponse.parseIdToken();
    }
}
