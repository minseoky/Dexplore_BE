package info.dexplore.dexplore.dto.request.main.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeleteArtRequestDto {

    @NotNull
    private Long artId;

}
