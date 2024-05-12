package info.dexplore.dexplore.dto.request.main.user.test;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TestRequestDto {

    @NotBlank
    private String msg;

}
