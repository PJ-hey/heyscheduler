package hey.io.heyscheduler.common.exception.notfound;

import hey.io.heyscheduler.common.exception.BusinessException;
import hey.io.heyscheduler.common.exception.ErrorCode;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
