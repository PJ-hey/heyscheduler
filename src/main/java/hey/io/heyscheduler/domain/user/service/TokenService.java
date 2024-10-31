package hey.io.heyscheduler.domain.user.service;

import hey.io.heyscheduler.common.config.jwt.JwtTokenProvider;
import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.exception.notfound.EntityNotFoundException;
import hey.io.heyscheduler.common.exception.unauthorized.UnAuthorizedException;
import hey.io.heyscheduler.domain.user.dto.TokenDTO;
import hey.io.heyscheduler.domain.user.dto.UserRequest;
import hey.io.heyscheduler.domain.user.entity.User;
import hey.io.heyscheduler.domain.user.repository.TokenRepository;
import hey.io.heyscheduler.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * <p>사용자 정보로 JWT 토큰 발급</p>
     *
     * @param userRequest 로그인 사용자 정보
     * @return 발급된 토큰 정보
     */
    @Transactional
    public TokenDTO getAccessToken(UserRequest userRequest) {
        String userId = userRequest.getUserId();
        String password = userRequest.getPassword();

        // 사용자 정보 검증
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "id: " + userId));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnAuthorizedException(ErrorCode.INCORRECT_USER);
        }

        // JWT 토큰 생성
        TokenDTO tokenDTO = jwtTokenProvider.createToken(user);
        tokenRepository.save(tokenDTO.toToken());

        return tokenDTO;
    }
}
