package info.dexplore.dexplore.provider;

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
            
            //작품 설명이 한글인지 영어인지 체크 및 언어 코드 설정
            String languageCode = "";
            boolean languageCheck = artDescription.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
            if(languageCheck) languageCode = "ko-KR";
            else languageCode = "en-US";

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
}
