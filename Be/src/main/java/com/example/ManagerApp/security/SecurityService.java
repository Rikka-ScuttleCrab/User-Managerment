package com.example.ManagerApp.security;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import com.example.ManagerApp.domain.User;
import com.example.ManagerApp.dao.PermissionDAO;
import com.nimbusds.jose.util.Base64;
@Service
public class SecurityService {
    private final JwtEncoder jwtEncoder;
    private final PermissionDAO permissionDAO;
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    @Value("${managerapplication.jwt.base64-secret}")
    public String jwtKey;
    @Value("${managerapplication.jwt.access-token-validity-in-seconds}")
    public long accessTokenExpirationTime;
    @Value("${managerapplication.jwt.refresh-token-validity-in-seconds}")
    public long refreshTokenExpirationTime;
    public SecurityService(
            JwtEncoder jwtEncoder,
            PermissionDAO permissionDAO
    ) {
        this.jwtEncoder = jwtEncoder;
        this.permissionDAO = permissionDAO;
    }
    // ================= ACCESS TOKEN =================
    public String createAccessToken(User user) {
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenExpirationTime, ChronoUnit.SECONDS);
        List<String> permissions =
                permissionDAO.getPermissionsByUserId(user.getId());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(user.getUsername())
                .claim("user_id", user.getId())
                .claim("email", user.getEmail())
                .claim("permission", permissions)
                .build();
        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
    // ================= REFRESH TOKEN =================
    public String createRefreshToken(User user) {
        Instant now = Instant.now();
        Instant validity = now.plus(refreshTokenExpirationTime, ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(user.getUsername())
                .claim("user_id", user.getId())
                .build();
        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
    // ================= CURRENT USER =================
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(
                extractPrincipal(context.getAuthentication())
        );
    }
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) return null;
        if (authentication.getPrincipal() instanceof UserDetails u)
            return u.getUsername();
        if (authentication.getPrincipal() instanceof Jwt jwt)
            return jwt.getSubject();
        if (authentication.getPrincipal() instanceof String s)
            return s;
        return null;
    }
    // ================= VALIDATE TOKEN =================
    public Jwt checkValidToken(String token) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(getSecretKey())
                .macAlgorithm(JWT_ALGORITHM)
                .build();
        try {
            return decoder.decode(token);
        } catch (JwtException e) {
            throw e;
        }
    }
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(
                keyBytes,
                0,
                keyBytes.length,
                JWT_ALGORITHM.getName()
        );
    }
}