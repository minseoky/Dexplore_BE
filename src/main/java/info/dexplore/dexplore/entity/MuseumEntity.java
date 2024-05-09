package info.dexplore.dexplore.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "museum")
@Table(name = "museum")
public class MuseumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "museum_id")
    private Long museumId;

    @Column(name = "museum_name")
    private String museumName;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "ent_price")
    private String entPrice;

    @Column(name = "museum_email")
    private String museumEmail;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "closing_day")
    private String closingDay;

    private String description;

    private String phone;

    @Column(name = "img_url")
    private String imgUrl;
}
