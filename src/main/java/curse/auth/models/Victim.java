package curse.auth.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "victim")
public class Victim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "victim_id")
    private Long victimId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "gender")
    private String gender;

    @Column(name = "birthdate")
    private LocalDateTime birthdate;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "info")
    private String info;

    @Column(name = "tags_answer", columnDefinition = "TEXT")
    private String tagsAnswer;
}
