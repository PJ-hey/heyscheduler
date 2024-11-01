package hey.io.heyscheduler.domain.user.service;

import hey.io.heyscheduler.common.config.component.AvailableRoleHierarchy;
import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.exception.notfound.EntityNotFoundException;
import hey.io.heyscheduler.domain.user.dto.UserDTO;
import hey.io.heyscheduler.domain.user.entity.User;
import hey.io.heyscheduler.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AvailableRoleHierarchy availableRoleHierarchy;

    /**
     * <p>사용자 정보 조회</p>
     *
     * @param userId 사용자 아이디
     * @return 사용자 정보
     * @throws EntityNotFoundException throws UsernameNotFoundException
     */
    @Override
    public UserDTO loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "id: " + userId));

        // 권한 정보 설정
        List<GrantedAuthority> authorities = loadUserAuthorities(userId);

        return UserDTO.of(user, authorities);
    }

    /**
     * <p>사용자 권한 정보</p>
     *
     * @param userId 사용자 아이디
     * @return 사용자 권한 목록
     */
    public List<GrantedAuthority> loadUserAuthorities(String userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 사용자별 권한 조회
        List<String> userAuthList = userRepository.selectUserAuthList(userId);
        userAuthList.forEach(authId -> authorities.add(new SimpleGrantedAuthority(authId)));

        // 연결된 모든 하위 계층 권한 포함
        return availableRoleHierarchy.getReachableAuthorities(authorities);
    }
}
