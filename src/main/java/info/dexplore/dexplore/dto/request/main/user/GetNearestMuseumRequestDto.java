package info.dexplore.dexplore.dto.request.main.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class GetNearestMuseumRequestDto {

    @NotNull
    private BigDecimal latitude;

    @NotNull
    private BigDecimal longitude;
}
