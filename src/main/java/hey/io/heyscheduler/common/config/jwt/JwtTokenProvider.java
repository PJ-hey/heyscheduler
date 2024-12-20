package hey.io.heyscheduler.common.config.jwt;

import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.exception.unauthorized.UnAuthorizedException;
import hey.io.heyscheduler.domain.user.dto.TokenDTO;
import hey.io.heyscheduler.domain.user.dto.UserDTO;
import hey.io.heyscheduler.domain.user.entity.User;
import hey.io.heyscheduler.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessTokenTime;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenTime;

    private final UserService userService;

    // 토큰에서 인증 정보 추출
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaim(token);

        // 사용자 정보 조회
        String userId = claims.getSubject();
        UserDTO userDTO = userService.loadUserByUsername(userId);

        // 권한 정보 조회 및 변환
        @SuppressWarnings("unchecked")
        List<LinkedHashMap<String, String>> authoritiesMap = (List<LinkedHashMap<String, String>>) claims.get("authorities");
        List<? extends GrantedAuthority> authorities = authoritiesMap.stream()
            .map(authMap -> new SimpleGrantedAuthority(authMap.get("authority")))
            .toList();

        return new UsernamePasswordAuthenticationToken(userDTO, token, authorities);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parseClaim(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new UnAuthorizedException(ErrorCode.MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw new UnAuthorizedException(ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new UnAuthorizedException(ErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new UnAuthorizedException(ErrorCode.ILLEGAL_JWT);
        }
    }

    // Request 에서 헤더로부터 토큰 정보 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 다음의 토큰 값만 추출
        }
        return null;
    }

    // 인증 정보로 토큰 생성
    public TokenDTO createToken(User user) {
        String userId = user.getUserId();
        Date now = new Date();

        // Access Token 생성
        String accessToken = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(userId)
            .addClaims(getClaims(user))
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + accessTokenTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + refreshTokenTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();

        return TokenDTO.builder()
            .userId(userId)
            .grantType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(formatExpirationTime(getExpirationTime(accessToken)))
            .build();
    }

    // 인증 정보로 claims 생성
    private Claims getClaims(User user) {
        List<SimpleGrantedAuthority> authorities = user.getAuthorities();

        Claims claims = Jwts.claims();
        claims.put("userInfo", UserDTO.of(user, authorities));
        claims.put("authorities", authorities);
        return claims;
    }

    // 토큰 만료 시간 반환
    private Date getExpirationTime(String token) {
        Claims claims = parseClaim(token);
        return claims.getExpiration();
    }

    // 만료 시간 포맷팅
    private String formatExpirationTime(Date expirationTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(expirationTime);
    }

    // Claims 파싱
    private Claims parseClaim(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // 서명 키 반환
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
