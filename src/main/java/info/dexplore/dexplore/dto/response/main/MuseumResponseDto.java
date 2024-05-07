package info.dexplore.dexplore.dto.response.main;

import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.MuseumEntity;

public class MuseumResponseDto extends ResponseDto {

    private MuseumEntity museum;

    public MuseumResponseDto(MuseumEntity museum) {
        super();
        this.museum = museum;
    }

}
