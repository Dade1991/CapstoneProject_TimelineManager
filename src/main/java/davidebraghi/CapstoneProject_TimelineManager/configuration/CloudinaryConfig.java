package davidebraghi.CapstoneProject_TimelineManager.configuration;

import com.cloudinary.Cloudinary;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.CloudinaryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary getAvatarImage(
            @Value("${cloudinary.cloudinary_name}") String cloudinaryName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {

        // eseguo controllo sui dati

        try {
            System.out.println("| Cloudinary name: " + cloudinaryName);
            System.out.println("| Api Key: " + apiKey);
            System.out.println("| Api Secret: " + apiSecret);
        } catch (CloudinaryException ex) {
            System.out.println("Warning, some Cloudinary data were not duly uploaded.");
        }

        Map<String, String> config = new HashMap<>();
        config.put("cloudinary_name", cloudinaryName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}