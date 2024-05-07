package info.dexplore.dexplore.dto.request.main;


import jakarta.validation.constraints.NotBlank;

public class MuseumRequestDto {

    @NotBlank
    private String museumId;

}
