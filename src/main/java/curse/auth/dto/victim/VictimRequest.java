package curse.auth.dto.victim;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VictimRequest {
    private String gender;
    private LocalDateTime birthdate;
    private String country;
    private String city;
    private String info;
}
