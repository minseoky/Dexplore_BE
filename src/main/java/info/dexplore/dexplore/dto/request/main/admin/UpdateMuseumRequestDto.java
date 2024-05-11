package info.dexplore.dexplore.dto.request.main.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMuseumRequestDto {

    @NotNull
    private Long museumId;

    @NotBlank
    private String museumName;

    @NotBlank
    private String entPrice;

    @NotBlank
    @Email
    private String museumEmail;

    @NotBlank
    private String startTime;

    @NotBlank
    private String endTime;

    @NotBlank
    private String closingDay;

    @Size(max = 2000, message = "2000자 제한")
    private String description;

    @NotBlank
    private String phone;


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
