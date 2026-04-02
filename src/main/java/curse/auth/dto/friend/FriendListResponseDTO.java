package curse.auth.dto.friend;

import curse.auth.httpResponse.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FriendListResponseDTO implements ResponseDto {
    private List<FriendDto> friends;
}
