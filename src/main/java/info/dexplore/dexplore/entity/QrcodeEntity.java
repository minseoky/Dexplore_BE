package info.dexplore.dexplore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "qrcode")
@Table(name = "qrcode")
public class QrcodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qrcode_id")
    private Long qrcodeId;

    @Column(name = "qrcode_name")
    private String qrcodeName;

    @Column(name = "qrcode_hashkey")
    private String qrcodeHashkey;
}
