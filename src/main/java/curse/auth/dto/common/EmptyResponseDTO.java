package curse.auth.dto.common;

import curse.auth.httpResponse.ResponseDto;

public class EmptyResponseDTO implements ResponseDto {
    public static final EmptyResponseDTO INSTANCE = new EmptyResponseDTO();

    private EmptyResponseDTO() {
    }
}
