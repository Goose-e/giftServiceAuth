package curse.auth.dto.recommendation;

import curse.auth.httpResponse.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationResponseDTO implements ResponseDto {
    private List<RecommendationDto> recommendations;
}
