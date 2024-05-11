package info.dexplore.dexplore.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "spot")
@Table(name = "spot")
public class SpotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spot_id")
    private Long spotId;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private String level;

    @Column(name = "edge_latitude1")
    private BigDecimal edgeLatitude1;

    @Column(name = "edge_longitude1")
    private BigDecimal edgeLongitude1;

    @Column(name = "edge_latitude2")
    private BigDecimal edgeLatitude2;

    @Column(name = "edge_longitude2")
    private BigDecimal edgeLongitude2;

}
