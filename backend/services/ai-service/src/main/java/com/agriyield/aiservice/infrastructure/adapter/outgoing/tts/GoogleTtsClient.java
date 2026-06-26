package com.agriyield.aiservice.infrastructure.adapter.outgoing.tts;

import com.agriyield.aiservice.application.port.outgoing.TextToSpeechPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Local TTS using espeak-ng + ffmpeg.
 * No API key needed. Runs entirely on the machine.
 * Install: sudo apt-get install -y espeak-ng ffmpeg
 */
@Slf4j
@Component
public class GoogleTtsClient implements TextToSpeechPort {

    @Override
    public byte[] synthesizeSpeech(String text, String languageCode) {
        log.info("Local TTS: languageCode={} textLength={}", languageCode, text.length());

        // Map language codes to espeak-ng voice names
        String espeakVoice = switch (languageCode) {
            case "am-ET" -> "am";       // Amharic
            case "om-ET" -> "om";       // Oromiffa
            case "ti-ET" -> "ti";       // Tigrinya
            default      -> "en";       // English fallback
        };

        // Truncate to avoid very long synthesis
        String truncated = text.length() > 3000
                ? text.substring(0, 3000) + "."
                : text;

        // Clean text — remove special chars that break shell
        String cleanText = truncated
                .replace("\"", " ")
                .replace("'", " ")
                .replace("`", " ")
                .replace("\\", " ")
                .replace("\n", ". ")
                .replace("\r", "");

        String tmpId   = UUID.randomUUID().toString();
        String wavPath = "/tmp/tts_" + tmpId + ".wav";
        String mp3Path = "/tmp/tts_" + tmpId + ".mp3";

        try {
            // Step 1: espeak-ng → WAV
            ProcessBuilder espeak = new ProcessBuilder(
                    "espeak-ng",
                    "-v", espeakVoice,
                    "-s", "130",        // speaking rate (words per minute)
                    "-a", "150",        // amplitude
                    "-w", wavPath,      // output WAV file
                    cleanText
            );
            espeak.redirectErrorStream(true);
            Process espeakProcess = espeak.start();
            int espeakExit = espeakProcess.waitFor();

            if (espeakExit != 0) {
                log.warn("espeak-ng exited with code {}, trying English fallback", espeakExit);
                // Retry with English if the voice isn't installed
                ProcessBuilder fallback = new ProcessBuilder(
                        "espeak-ng", "-v", "en", "-s", "130", "-a", "150", "-w", wavPath, cleanText
                );
                fallback.redirectErrorStream(true);
                fallback.start().waitFor();
            }

            // Step 2: WAV → MP3 via ffmpeg
            ProcessBuilder ffmpeg = new ProcessBuilder(
                    "ffmpeg", "-y",
                    "-i", wavPath,
                    "-codec:a", "libmp3lame",
                    "-qscale:a", "4",
                    mp3Path
            );
            ffmpeg.redirectErrorStream(true);
            Process ffmpegProcess = ffmpeg.start();
            ffmpegProcess.waitFor();

            // Step 3: Read MP3 bytes
            File mp3File = new File(mp3Path);
            if (!mp3File.exists() || mp3File.length() == 0) {
                log.error("TTS output MP3 is empty or missing");
                return new byte[0];
            }

            byte[] audioBytes = Files.readAllBytes(mp3File.toPath());
            log.info("Local TTS produced {} bytes of MP3 audio", audioBytes.length);
            return audioBytes;

        } catch (Exception e) {
            log.error("Local TTS failed: {}", e.getMessage(), e);
            return new byte[0];
        } finally {
            // Clean up temp files
            new File(wavPath).delete();
            new File(mp3Path).delete();
        }
    }
}
