package main.service;

import lombok.RequiredArgsConstructor;
import main.config.SecurityConfig;
import main.model.User;
import main.repository.UsersRepository;
import main.request.ProfileRequest;
import main.response.api.EditResponse;
import org.imgscalr.AsyncScalr;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UsersRepository usersRepository;

    @Value("${register.min_password_length}")
    private int minPasswordLength;

    @Value("${image.max_size_in_bytes}")
    private long maxFileSize;

    @Value("${image.square_photo_size_in_pixels}")
    private int photoSize;

    public EditResponse editMyProfile(ProfileRequest request, MultipartFile photo) {

        User user = usersRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        String name = request.getName();
        String email = request.getEmail();
        String password = request.getPassword();

        EditResponse response = new EditResponse();
        ConcurrentHashMap<String, String> errors = new ConcurrentHashMap<>();

        if (name != null && !name.replaceAll("[A-Za-zА-Яа-я\\s]", "").isEmpty()) {
            errors.put("name", "Имя указано неверно");
        }
        if (email != null && !user.getEmail().equals(email) && usersRepository.emailCount(email) > 0) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (password != null && password.length() < minPasswordLength) {
            errors.put("password", "Пароль короче 6-ти символов");
        }
        if (photo != null && photo.getSize() > maxFileSize) {
            errors.put("photo", "Фото слишком большое, нужно не более 5 Мб");
        }

        if (errors.isEmpty()) {
            if (name != null) {
                user.setName(name);
            }
            if (email != null) {
                user.setEmail(email);
            }
            if (password != null) {
                user.setPassword(SecurityConfig.encoder().encode(password));
            }
            if (request.getRemovePhoto() == 1) {
                user.setPhoto(null);
            } else if (photo != null) {
                user.setPhoto("/" + resizeImage(photo));
            }

            usersRepository.save(user);
            response.setResult(true);
        }

        response.setErrors(errors);
        return response;
    }

    private String resizeImage(MultipartFile img) {

        String extension = img.getOriginalFilename().replaceAll("(.+?)\\.", ".");
        File photo = new File(ImageService.formDestination() + ImageService.formImageName(img, extension));
        BufferedImage image = null;
        int dim;

        try {
            image = ImageIO.read(img.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image == null) {
            return null;
        } else {
            dim = Math.min(image.getHeight(), image.getWidth());
        }

        try {
            BufferedImage resizedImage = AsyncScalr.resize(Scalr.crop(image, dim, dim), Scalr.Method.QUALITY, photoSize).get();
            ImageIO.write(resizedImage, extension.replaceAll("\\.", ""), photo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return photo.getPath();
    }
}
