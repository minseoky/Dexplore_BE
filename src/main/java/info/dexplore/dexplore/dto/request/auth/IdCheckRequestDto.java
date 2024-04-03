package info.dexplore.dexplore.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdCheckRequestDto {

    @Size(min = 4, max = 60, message = "4~60")
    @NotBlank
    String id;

}
