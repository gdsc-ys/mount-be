package gdsc.be.mount.storage.exception;

import gdsc.be.mount.global.common.Enum.ErrorCode;
import gdsc.be.mount.global.common.exception.BusinessException;

public class FileDeletionException extends BusinessException {
    public final static BusinessException EXCEPTION = new FileDeletionException();
    private FileDeletionException() {
        super(ErrorCode.FILE_DELETE_FAILED);
    }
}
