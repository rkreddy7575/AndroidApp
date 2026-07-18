package com.trailbook.media;

import com.trailbook.config.CloudinaryProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class CloudinaryService {

    private final CloudinaryProperties properties;

    public CloudinaryService(CloudinaryProperties properties) {
        this.properties = properties;
    }

    public CloudinarySignatureResponse generateSignature() {
        long timestamp = System.currentTimeMillis() / 1000;
        String paramsToSign = "timestamp=" + timestamp;
        String signature = sign(paramsToSign);
        return new CloudinarySignatureResponse(
                properties.cloudName(),
                properties.apiKey(),
                timestamp,
                signature
        );
    }

    private String sign(String params) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(properties.apiSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] hash = mac.doFinal(params.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign Cloudinary request", e);
        }
    }
}
