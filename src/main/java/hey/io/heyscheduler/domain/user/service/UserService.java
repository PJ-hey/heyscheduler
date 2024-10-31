package hey.io.heyscheduler.domain.user.service;

import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.exception.notfound.EntityNotFoundException;
import hey.io.heyscheduler.domain.user.dto.UserDTO;
import hey.io.heyscheduler.domain.user.entity.User;
import hey.io.heyscheduler.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * <p>사용자 정보 조회</p>
     *
     * @param userId 사용자 아이디
     * @return 사용자 정보
     * @throws UsernameNotFoundException throws UsernameNotFoundException
     */
    @Override
    public UserDTO loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "id: " + userId));

        return UserDTO.of(user);
    }
}
