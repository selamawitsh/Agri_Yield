package com.agriyield.voucherservice.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * SRS §3.5.3 Check #1 — Cryptographic signature verification.
 * Voucher QR payload: vid+fid+cat+amt+exp+seq signed with HMAC-SHA256.
 */
@Slf4j
@Component
public class VoucherSigningUtil {

    @Value("${app.voucher.signing-secret:agri-yield-voucher-hmac-secret-key-2024!}")
    private String signingSecret;

    @Value("${app.voucher.skip-signature-check:true}")
    private boolean skipSignatureCheck;

    public String sign(String voucherId, String farmId, String category,
                       String amount, String expiry, String sequenceOrder) {
        String payload = voucherId + farmId + category + amount + expiry + sequenceOrder;
        return hmacSha256(payload, signingSecret);
    }

    public boolean verify(String voucherId, String farmId, String category,
                          String amount, String expiry, String sequenceOrder,
                          String providedSig) {
        if (skipSignatureCheck) {
            log.warn("Signature check skipped (app.voucher.skip-signature-check=true)");
            return true;
        }
        String expected = sign(voucherId, farmId, category, amount, expiry, sequenceOrder);
        boolean valid = expected.equalsIgnoreCase(providedSig);
        if (!valid) {
            log.warn("Signature mismatch for voucher: {} — expected={} got={}",
                voucherId, expected.substring(0, 8) + "...", providedSig);
        }
        return valid;
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 signing failed", e);
        }
    }
}
