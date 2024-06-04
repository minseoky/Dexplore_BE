package info.dexplore.dexplore.dto.request.main.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetViewingRateRequestDto {

    @NotNull
    private Long museumId;

}
