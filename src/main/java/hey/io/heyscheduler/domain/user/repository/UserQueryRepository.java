package hey.io.heyscheduler.domain.user.repository;

import java.util.List;

public interface UserQueryRepository {

    /**
     * <p>사용자 권한 정보</p>
     *
     * @param userId 사용자 ID
     * @return 사용자 권한 목록
     */
    List<String> selectUserAuthList(String userId);
}
