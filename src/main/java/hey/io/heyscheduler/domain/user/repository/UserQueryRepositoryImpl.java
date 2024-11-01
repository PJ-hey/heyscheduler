package hey.io.heyscheduler.domain.user.repository;

import static hey.io.heyscheduler.domain.user.entity.QUserAuth.userAuth;

import hey.io.heyscheduler.common.repository.Querydsl5RepositorySupport;
import hey.io.heyscheduler.domain.user.entity.User;
import java.util.List;

public class UserQueryRepositoryImpl extends Querydsl5RepositorySupport implements UserQueryRepository {

    public UserQueryRepositoryImpl() {
        super(User.class);
    }

    /**
     * <p>사용자 권한 정보</p>
     *
     * @param userId 사용자 ID
     * @return 사용자 권한 목록
     */
    @Override
    public List<String> selectUserAuthList(String userId) {
        return select(userAuth.auth.authId)
            .from(userAuth)
            .where(userAuth.user.userId.eq(userId))
            .fetch();
    }
}