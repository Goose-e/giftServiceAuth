package curse.auth.dto.common;

import curse.auth.httpResponse.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDTO implements ResponseDto {
    private String error;
}
