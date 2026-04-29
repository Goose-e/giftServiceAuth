package curse.auth.dto.recommendation;

import curse.auth.httpResponse.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDTO implements ResponseDto {
    private List<RecommendationDto> recommendations;
    private String recommendationsAnswer;

    public RecommendationResponseDTO(List<RecommendationDto> recommendations) {
        this.recommendations = recommendations;
        this.recommendationsAnswer = null;
    }
}