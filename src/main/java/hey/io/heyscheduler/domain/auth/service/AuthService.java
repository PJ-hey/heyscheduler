package hey.io.heyscheduler.domain.auth.service;

import hey.io.heyscheduler.domain.auth.dto.AuthDTO;
import hey.io.heyscheduler.domain.auth.repository.AuthRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return List 구조로 계층화한 권한 목록
     */
    public List<AuthDTO> getAllHierarchicalAuthList() {
        return authRepository.selectHierarchicalAuthList();
    }
}
