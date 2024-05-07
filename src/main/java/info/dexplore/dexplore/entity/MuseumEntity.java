package info.dexplore.dexplore.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "museum_id")
    private String museumId;

    @Column(name = "museum_name")
    private String museumName;

    @Column(name = "location_id")
    private String locationId;

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
}
