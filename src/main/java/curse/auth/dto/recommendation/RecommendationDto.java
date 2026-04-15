package curse.auth.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationDto {
    private Long giftId;
    private String giftName;
    private String reason;
    private String tagName;
    private Long avgPrice;
}
