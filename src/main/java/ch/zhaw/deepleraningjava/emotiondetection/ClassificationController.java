package ch.zhaw.deepleraningjava.emotiondetection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StreamUtils;
import java.io.FileInputStream;
import java.io.File;


@RestController
public class ClassificationController {


    private Inference inference = new Inference();

    @GetMapping("/ping")
    public String ping() {
        return "Classification app is up and running!";
    }

    @PostMapping(path = "/analyze")
    public String predict(@RequestParam("image") MultipartFile image) throws Exception {
        try {
            byte[] imageData = image.getBytes();
            return inference.predict(imageData).toJson();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Failed to process image.", e);
        }
    }
}