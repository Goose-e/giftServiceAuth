package curse.auth.httpResponse;

import com.example.companyReputationManagement.constants.SysConst;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.MediaType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class HttpResponseBody<T extends ResponseDto> implements Serializable {

    private final String requestId;
    private String responseCode;
    private String message;
    private String error = SysConst.STRING_NULL;
    private T responseEntity;
    private final List<ErrorInfo> errors = new ArrayList<>();

    @JsonIgnore
    private final MediaType contentType = MediaType.APPLICATION_JSON;

    protected HttpResponseBody(String requestId) {
        this.requestId = requestId;
    }

    //==================================================================================================================

    @JsonIgnore
    public boolean haveErrors() {
        return (!responseCode.equalsIgnoreCase(SysConst.OC_OK)) || !errors.isEmpty();
    }

    @JsonIgnore
    public boolean haveNoErrors() {
        return !haveErrors();
    }

    private int addErrorInfo(ErrorInfo errorInfo) {
        errors.add(errorInfo);

        if (message == null || message.isEmpty()) {
            message = errorInfo.getErrorMsg();
        }

        if (error == null || error.isEmpty()) {
            error = errorInfo.getErrorMsg();
            responseEntity = null;
        }

        return errors.size();
    }

    public int addErrorInfo(String errorCode, String error, String errorMsg) {
        addErrorInfo(ErrorInfo.create(error, errorMsg));

        if (responseCode == null || responseCode.isEmpty()) {
            responseCode = errorCode;
        } else if (SysConst.OC_OK.equals(responseCode)) {
            responseCode = errorCode;
            message = errorMsg;
        }

        return errors.size();
    }

    private void setErrorMessage(String errorMsg) {
        if (error == null || error.isEmpty()) {
            error = errorMsg;
        }
    }

    public void assignErrors(List<ErrorInfo> errors) {
        this.errors.addAll(errors);
        if (!errors.isEmpty()) {
            responseCode = SysConst.INVALID_ENTITY_ATTR;
            if (message == null || message.isEmpty()) {
                message = errors.stream().findFirst().orElseThrow().getErrorMsg().toUpperCase();
            }
            setErrorMessage(message);
        }
    }

    public String toString2() {
        return "code='" + responseCode + "', message='" + message + "', error='" + error + "', errors=" + errors +
                ", " + this.getClass().getSimpleName() + "(" + responseEntity + ")";
    }


    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        errors.add(new ErrorInfo("Error", error));
        this.error = error;
    }

    public T getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(T responseEntity) {
        this.responseEntity = responseEntity;
    }

    public List<ErrorInfo> getErrors() {
        return errors;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public static final long serialVersionUID = 1000L;
}
