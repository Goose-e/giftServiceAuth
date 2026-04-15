package curse.auth.dto.victim;

import curse.auth.httpResponse.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VictimListResponseDTO implements ResponseDto {
    private List<VictimDto> victims;
}
