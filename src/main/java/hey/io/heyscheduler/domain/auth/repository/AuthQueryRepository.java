package hey.io.heyscheduler.domain.auth.repository;

import hey.io.heyscheduler.domain.auth.dto.AuthDTO;
import java.util.List;

public interface AuthQueryRepository {

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return 계층화 권한 목록
     */
    List<AuthDTO> selectHierarchicalAuthList();
}
