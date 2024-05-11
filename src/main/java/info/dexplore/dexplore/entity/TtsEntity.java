package info.dexplore.dexplore.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tts")
@Table(name = "tts")
public class TtsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tts_id")
    private Long ttsId;

    @Column(name = "bucket_url")
    private String bucketUrl;
}
