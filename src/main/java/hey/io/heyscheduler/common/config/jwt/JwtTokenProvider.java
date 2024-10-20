package hey.io.heyscheduler.common.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰에서 인증 정보 추출
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = loadUserByToken(token); // 토큰에서 사용자 정보 로드
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parseClaim(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Request에서 헤더로부터 토큰 정보 추출
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰으로부터 사용자 정보 로드
    private UserDetails loadUserByToken(String token) {
        Claims claims = parseClaim(token);
        String username = claims.getSubject();

        // TODO : 사용자 정보 추후 연동
        return new User(username, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private Claims parseClaim(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
