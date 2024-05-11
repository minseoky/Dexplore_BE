package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.MuseumEntity;

public class GetNearestMuseumResponseDto extends ResponseDto {

    private MuseumEntity museum;

    public GetNearestMuseumResponseDto(MuseumEntity museum) {
        super();
        this.museum = museum;
    }

}
