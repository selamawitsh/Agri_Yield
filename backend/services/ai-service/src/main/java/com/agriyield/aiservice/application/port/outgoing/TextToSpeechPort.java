package com.agriyield.aiservice.application.port.outgoing;

public interface TextToSpeechPort {
    byte[] synthesizeSpeech(String text, String languageCode);
}
