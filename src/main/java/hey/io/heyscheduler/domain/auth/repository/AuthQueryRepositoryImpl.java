package hey.io.heyscheduler.domain.auth.repository;

import static hey.io.heyscheduler.domain.auth.entity.QAuth.auth;

import com.querydsl.core.types.Projections;
import hey.io.heyscheduler.common.repository.Querydsl5RepositorySupport;
import hey.io.heyscheduler.domain.auth.dto.AuthDTO;
import hey.io.heyscheduler.domain.auth.entity.Auth;
import java.util.List;

public class AuthQueryRepositoryImpl extends Querydsl5RepositorySupport implements AuthQueryRepository {

    public AuthQueryRepositoryImpl() {
        super(Auth.class);
    }

    /**
     * <p>계층화 권한 목록</p>
     *
     * @return 계층화 권한 목록
     */
    @Override
    public List<AuthDTO> selectHierarchicalAuthList() {
        return select(Projections.fields(AuthDTO.class,
            auth.authId,
            auth.upperAuth.authId.as("upperAuthId")))
            .from(auth)
            .where(auth.enabled.eq(true), auth.upperAuth.authId.isNotNull())
            .fetch();
    }
}
