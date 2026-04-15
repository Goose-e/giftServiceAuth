package curse.auth.dto.gift;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GiftDto {
    private Long giftId;
    private String giftName;
    private Long avgPrice;
    private String tagName;
}
