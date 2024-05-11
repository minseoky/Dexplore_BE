package info.dexplore.dexplore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "art")
@Table(name = "art")
public class ArtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_it")
    private Long artId;

    @Column(name = "museum_id")
    private Long museumId;

    @Column(name = "spot_id")
    private Long spotId;

    @Column(name = "qrcode_id")
    private Long qrcodeId;

    @Column(name = "tts_id")
    private Long ttsId;

    @Column(name = "art_name")
    private String artName;
    @Column(name = "art_description")
    private String artDescription;
    @Column(name = "art_year")
    private String artYear;
    @Column(name = "auth_name")
    private String authName;
    @Column(name = "img_url")
    private String imgUrl;


}
