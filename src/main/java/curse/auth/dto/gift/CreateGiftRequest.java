package curse.auth.dto.gift;

import lombok.Data;

@Data
public class CreateGiftRequest {
    private String giftName;
    private Long giftAvgPrice;
    private String tagName;
}
