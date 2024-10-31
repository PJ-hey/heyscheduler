package hey.io.heyscheduler.common.exception.unauthorized;

import hey.io.heyscheduler.common.exception.BusinessException;
import hey.io.heyscheduler.common.exception.ErrorCode;

public class UnAuthorizedException extends BusinessException {

    public UnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
