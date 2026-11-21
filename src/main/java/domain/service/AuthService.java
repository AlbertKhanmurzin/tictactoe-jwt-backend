package domain.service;

import domain.model.signup.SignUp;
import web.model.jwt.JwtRequest;
import web.model.jwt.JwtResponse;
import web.security.JwtAuthentication;


public interface AuthService {

    boolean register(SignUp request);

    JwtResponse login(JwtRequest request);

    JwtResponse getAccessToken(String refreshToken);

    JwtResponse refresh(String refreshToken);

    JwtAuthentication getAuthInfo();

    default JwtResponse authenticate(JwtRequest request) {
        return login(request);
    }

    default JwtResponse refreshAccessToken(String refreshToken) {
        return getAccessToken(refreshToken);
    }

    default JwtResponse refreshRefreshToken(String refreshToken) {
        return refresh(refreshToken);
    }

    default JwtAuthentication getAuthentication() {
        return getAuthInfo();
    }
}