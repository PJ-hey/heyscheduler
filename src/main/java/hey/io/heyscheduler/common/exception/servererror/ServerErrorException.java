package hey.io.heyscheduler.common.exception.servererror;

import hey.io.heyscheduler.common.exception.BusinessException;
import hey.io.heyscheduler.common.exception.ErrorCode;

public class ServerErrorException extends BusinessException {

    public ServerErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
