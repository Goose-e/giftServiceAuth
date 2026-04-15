package curse.auth.dto.gift;

import curse.auth.httpResponse.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GiftListResponseDTO implements ResponseDto {
    private List<GiftDto> gifts;
}
