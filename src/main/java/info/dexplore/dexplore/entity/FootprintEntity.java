package info.dexplore.dexplore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "footprint")
@Table(name = "footprints")
public class FootprintEntity {

    @Id
    private String userId;
    @Id
    private Long artId;
}
