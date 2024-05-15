package info.dexplore.dexplore.common;

public interface ResponseMessage {

    String SUCCESS = "Success.";

    String VALIDATION_FAIL = "Validation failed.";

    String DUPLICATE_ID = "Duplicate Id.";

    String SIGN_IN_FAIL = "Login information mismatch.";

    String CERTIFICATION_FAIL = "Certification failed.";

    String MAIL_FAIL = "Mail send failed";

    String DATABASE_ERROR = "Database error.";

    String WRONG_ROLE = "Wrong role.";

    String MUSEUM_NOT_FOUND = "Museum not found.";

    String ID_NOT_MATCHING = "User id not matching.";

    String ID_NOT_FOUND = "Id not found.";

    String DUPLICATE_MUSEUM_NAME = "Duplicated museum name.";

    String ART_NOT_FOUND = "Art not found.";

    String TTS_NOT_FOUND = "Tts not found.";

    String QR_NOT_FOUND = "Qrcode not found";

}
