package curse.auth.dto.victim;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VictimDto {
    private Long victimId;
    private String gender;
    private LocalDateTime birthdate;
    private String country;
    private String city;
    private String info;
}
