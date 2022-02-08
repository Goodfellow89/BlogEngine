package main.service;

import main.response.api.EditResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImageService {

    @Value("${image.max_size_in_bytes}")
    private long maxFileSize;

    public EditResponse addImage(MultipartFile img) {

        EditResponse response = new EditResponse();
        ConcurrentHashMap<String, String> errors = new ConcurrentHashMap<>();

        String extension = img.getOriginalFilename().replaceAll("(.+?)\\.", ".");
        String filePath = formDestination() + formImageName(img, extension);

        response.setResult(false);

        if (img.getSize() > maxFileSize) {
            errors.put("image", "Размер файла превышает допустимый размер");
        }
        if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png")) {
            errors.put("image", "Недопустимый формат файла");
        }

        if (errors.isEmpty()) {
            try {
                img.transferTo(new File(filePath).getAbsoluteFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            errors.put("path", "/" + filePath);
            response.setResult(true);
        }

        response.setErrors(errors);
        return response;
    }

    protected static String formDestination() {
        StringBuilder dst = new StringBuilder();
        dst.append("upload/");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                char letter = (char) Math.round(('a' + Math.random() * ('z' - 'a')));
                dst.append(letter);
            }
            dst.append("/");
        }
        try {
            Files.createDirectories(Path.of(dst.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dst.toString();
    }

    protected static String formImageName(MultipartFile image, String extension) {
        StringBuilder img = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int num = (int) (Math.random() * 9);
            img.append(num);
        }
        img.append(extension);
        return img.toString();
    }
}
