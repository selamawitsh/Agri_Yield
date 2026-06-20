package com.agriyield.aiservice.application.port.outgoing;

import org.springframework.web.multipart.MultipartFile;

public interface WhisperPort {
    String transcribeAudio(MultipartFile audioFile, String language);
}






//package com.agriyield.aiservice.application.port.outgoing;
//
//import org.springframework.web.multipart.MultipartFile;
//
//public interface WhisperPort {
//    // Returns the transcribed text from audio
//    String transcribeAudio(MultipartFile audioFile, String language);
//}
//
//// ===== GeminiPort.java =====
//package com.agriyield.aiservice.application.port.outgoing;
//
//public interface GeminiPort {
//    // Text advisory: send prompt, get text response
//    String generateAdvisory(String systemPrompt, String userPrompt);
//
//    // Vision: send base64 image + prompt, get JSON diagnosis
//    String diagnoseCropImage(String base64Image, String cropType,
//                             Double currentNdvi, Integer daysPostPlanting);
//}
//
//// ===== TextToSpeechPort.java =====
//package com.agriyield.aiservice.application.port.outgoing;
//
//public interface TextToSpeechPort {
//    // Returns the audio bytes (MP3) for the given text and language
//    byte[] synthesizeSpeech(String text, String languageCode);
//}
//
//// ===== AdvisorySessionRepositoryPort.java =====
//package com.agriyield.aiservice.application.port.outgoing;
//
//import com.agriyield.aiservice.domain.model.AdvisorySession;
//import java.util.List;
//
//public interface AdvisorySessionRepositoryPort {
//    AdvisorySession save(AdvisorySession session);
//    List<AdvisorySession> findByFarmerId(String farmerId);
//    List<AdvisorySession> findByFarmId(String farmId);
//}
//
//// ===== CropDiagnosisRepositoryPort.java =====
//package com.agriyield.aiservice.application.port.outgoing;
//
//import com.agriyield.aiservice.domain.model.CropDiagnosis;
//import java.util.List;
//
//public interface CropDiagnosisRepositoryPort {
//    CropDiagnosis save(CropDiagnosis diagnosis);
//    List<CropDiagnosis> findByFarmId(String farmId);
//    List<CropDiagnosis> findByFarmerIdAndEscalateToAgronomist(String farmerId, boolean escalate);
//}
//
//// ===== GeospatialServicePort.java =====
//package com.agriyield.aiservice.application.port.outgoing;
//
//public interface GeospatialServicePort {
//    NdviData getLatestNdvi(String farmId);
//    FarmContext getFarmContext(String farmId);
//
//    record NdviData(double ndviValue, String recordedDate) {}
//    record FarmContext(String farmId, double ndviValue, double farmAreaHa,
//                       String cropType, int daysSincePlanting) {}
//}
//
//// ===== WeatherServicePort.java =====
//package com.agriyield.aiservice.application.port.outgoing;
//
//public interface WeatherServicePort {
//    WeatherData getCurrentWeather(String farmId);
//
//    record WeatherData(double temperatureC, double rainfallMm,
//                       double humidity, String forecastSummary) {}
//}
//
//// ===== FarmServicePort.java =====
//package com.agriyield.aiservice.application.port.outgoing;
//
//public interface FarmServicePort {
//    FarmData getFarmById(String farmId);
//
//    record FarmData(String farmId, String farmerId, String cropType,
//                    String region, double areaHectares, String status,
//                    String kebeleCode) {}
//}
//
//// ===== ModelStoragePort.java =====
//package com.agriyield.aiservice.application.port.outgoing;
//
//public interface ModelStoragePort {
//    // Returns path to the downloaded model file
//    String downloadModel(String modelName);
//    boolean modelExists(String modelName);
//}
//
//// ===== AudioStoragePort.java =====
//package com.agriyield.aiservice.application.port.outgoing;
//
//import org.springframework.web.multipart.MultipartFile;
//
//public interface AudioStoragePort {
//    String storeAudioInput(MultipartFile audioFile, String farmId);
//    String storeAudioResponse(byte[] audioBytes, String farmId, String sessionId);
//}