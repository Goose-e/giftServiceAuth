package curse.auth.dto.jwt;


import curse.auth.httpResponse.HttpResponseBody;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;

public class RefreshResponse extends HttpResponseBody<RefreshResponseDTO> {
    private String httpRequestId = EMPTY_STRING;

    public RefreshResponse() {
        super(EMPTY_STRING);
    }

    public String getHttpRequestId() {
        return httpRequestId;
    }
}
