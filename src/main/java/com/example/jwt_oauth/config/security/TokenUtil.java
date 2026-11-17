package com.example.jwt_oauth.config.security;

import com.example.jwt_oauth.dto.TokenDTO;
import com.example.jwt_oauth.model.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

public class TokenUtil {

    private static final String ISSUER = "LuizFelipe";
    private static final long EXPIRATION = 60 * 1000; //60 segundos
    private static final String SECRET_KEY = "0123456789012345678901234567890123456789";
    private static final String GOOGLE_CLIENT_ID = "";


    public static Authentication decode(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        IO.println("Request header:" + header);

        if (header != null) {
            String token = header.replace("Bearer ", "");
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            JwtParser parser = Jwts.parser().verifyWith(key).build();
            Claims claim = (Claims) parser.parse(token).getPayload();

            String subject = claim.getSubject();
            String issuer = claim.getIssuer();
            Date exp = claim.getExpiration();

            if (issuer.equals(ISSUER) && subject.length() > 0 && exp.after(new Date(System.currentTimeMillis())))
                return new UsernamePasswordAuthenticationToken("ok", null, Collections.emptyList());
        }

        return null;
    }

    public static TokenDTO encode(User user) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        String jwtToken = Jwts.builder()
                .subject(user.getUsername())
                .issuer(ISSUER)
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION))
                .signWith(key)
                .compact();
        return new TokenDTO(jwtToken);
    }

    public static GoogleIdToken.Payload decodeGoogleToken(String token) {
        try {
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            // O método verify faz TODAS as validações: Assinatura, iss, aud e exp
            GoogleIdToken idToken = verifier.verify(token);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // Extraindo dados úteis do usuário
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                String name = (String) payload.get("name");
                String googleId = payload.getSubject(); // ID único do usuário no Google

                if (!emailVerified) {
                    throw new SecurityException("E-mail do Google não verificado.");
                }

                return payload; // Token válido e verificado!
            } else {
                throw new SecurityException("Token do Google inválido.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao validar o token do Google: " + e.getMessage(), e);
        }
    }

}
