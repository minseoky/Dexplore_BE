package info.dexplore.dexplore.entity;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FootprintId implements Serializable {
    private String userId;
    private Long artId;
}
