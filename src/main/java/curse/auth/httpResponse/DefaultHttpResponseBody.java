package curse.auth.httpResponse;

import static curse.auth.constants.SysConst.EMPTY_STRING;

public class DefaultHttpResponseBody<T extends ResponseDto> extends HttpResponseBody<T> {
    public DefaultHttpResponseBody() {
        super(EMPTY_STRING);
    }
}
