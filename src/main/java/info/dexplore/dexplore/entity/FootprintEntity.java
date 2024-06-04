package info.dexplore.dexplore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "footprint")
@Table(name = "footprints")
@IdClass(FootprintId.class)
public class FootprintEntity {

    @Id
    private String userId;
    @Id
    private Long artId;
}
