package info.dexplore.dexplore.provider;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import info.dexplore.dexplore.entity.TtsEntity;
import info.dexplore.dexplore.repository.TtsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import com.google.cloud.texttospeech.v1.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


@Component
@RequiredArgsConstructor
public class TtsProvider {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final TtsRepository ttsRepository;

    /**
     * artDescription 기반으로 tts 생성 및 버킷에 저장, tts DB에 저장(TtsEntity 이용)
     * @return 저장된 tts의 id 반환
     */
    public Long generateTts(String artName, String artDescription) throws IOException{
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // TTS 변환 용 input 설정, 여기서 input은 작품 설명
            SynthesisInput input = SynthesisInput.newBuilder().setText(artDescription).build();

            String languageCode = "ko-KR";
            
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(languageCode)
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // 생성된 TTS 파일을 S3 버킷에 업로드
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();

            byte[] data = audioContents.toByteArray();
            String contentType = "audio/mpeg";

            // S3 업로드를 위한 mp3 파일로 변경
            MultipartFile file = new MockMultipartFile(artName, artName, contentType, data);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucket, artName + "_TTS", file.getInputStream(), metadata);
            amazonS3.getUrl(bucket, artName + "_TTS").toString();

            String ttsUrl = amazonS3.getUrl(bucket, artName + "_TTS").toString();
            // TTS_Url TTS 데이터 베이스에 저장 필요

            TtsEntity newTts = new TtsEntity(
                null,
                ttsUrl
            );
            ttsRepository.save(newTts);

            return newTts.getTtsId();
        }
    }

    public void updateTts(String artName, String newDescription, Long ttsId) throws IOException{

        TtsEntity tts = ttsRepository.findByTtsId(ttsId);

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // TTS 변환 용 input 설정, 여기서 input은 작품 설명
            SynthesisInput input = SynthesisInput.newBuilder().setText(newDescription).build();

            String languageCode = "ko-KR";

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(languageCode)
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // 생성된 TTS 파일을 S3 버킷에 업로드
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();

            byte[] data = audioContents.toByteArray();
            String contentType = "audio/mpeg";

            // S3 업로드를 위한 mp3 파일로 변경
            MultipartFile file = new MockMultipartFile(artName, artName, contentType, data);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 해당 ttsUrl tts 제거
            String ttsUrl = tts.getBucketUrl();
            URL url = new URL(ttsUrl);
            String[] parts = url.getPath().split("/", 2);
            String key = parts[1]; // 파일 키(경로) 추출

            // 버킷에서 파일 삭제
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));

            amazonS3.putObject(bucket, artName + "_TTS", file.getInputStream(), metadata);
            amazonS3.getUrl(bucket, artName + "_TTS").toString();

            ttsUrl = amazonS3.getUrl(bucket, artName + "_TTS").toString();
            // TTS_Url TTS 데이터 베이스에 저장 필요

            TtsEntity newTts = new TtsEntity(
                    tts.getTtsId(),
                    ttsUrl
            );
            ttsRepository.save(newTts);

        }

    }

    public void deleteTts(String ttsUrl) throws MalformedURLException {
        // 해당 ttsUrl tts 제거
        URL url = new URL(ttsUrl);
        String[] parts = url.getPath().split("/", 2);
        String key = parts[1]; // 파일 키(경로) 추출

        // 버킷에서 파일 삭제
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
    }
}
