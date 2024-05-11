package info.dexplore.dexplore.dto.request.main;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SaveArtRequestDto {

    @NotNull
    private Long museumId;

    @NotBlank
    private String artName;

    @NotBlank
    private String artDescription;

    @NotBlank
    private String artYear;

    @NotBlank
    private String authName;

    //이하 공간정보

    @NotNull
    private BigDecimal latitude;

    @NotNull
    private BigDecimal longitude;

    @NotBlank
    private String level;

    private BigDecimal edgeLatitude1;
    private BigDecimal edgeLongitude1;
    private BigDecimal edgeLatitude2;
    private BigDecimal edgeLongitude2;
}
