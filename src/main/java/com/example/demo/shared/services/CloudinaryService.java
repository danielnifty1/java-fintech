package com.example.demo.shared.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.shared.exception.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
@AllArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // ✅ upload file to a specific folder
    public String upload(MultipartFile file, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "fintech/" + folder,
                            "resource_type", "auto"  // handles images, pdfs, docs
                    )
            );
            return (String) result.get("secure_url"); // returns https cloudinary URL
        } catch (IOException e) {
            throw new CustomException("Failed to upload file: " + file.getOriginalFilename());
        }
    }

    // ✅ upload from base64 string
    // accepts: "data:image/jpeg;base64,/9j/4AAQ..." OR raw base64
    public String uploadBase64(String base64String, String folder) {
        try {
            // strip the data URI prefix if present e.g "data:image/jpeg;base64,"
            String base64Data = base64String.contains(",")
                    ? base64String.split(",")[1]
                    : base64String;

            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            Map<?, ?> result = cloudinary.uploader().upload(
                    imageBytes,
                    ObjectUtils.asMap(
                            "folder", "fintech/" + folder,
                            "resource_type", "auto"
                    )
            );
            return (String) result.get("secure_url");
        } catch (IllegalArgumentException e) {
            throw new CustomException("Invalid base64 image data");
        } catch (IOException e) {
            throw new CustomException("Failed to upload image to Cloudinary");
        }
    }

    // ✅ delete by URL
    public void delete(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new CustomException("Failed to delete file");
        }
    }

 

    // ✅ extracts public_id from cloudinary URL for deletion
    private String extractPublicId(String imageUrl) {
        // URL format: https://res.cloudinary.com/cloud/image/upload/v1234567/fintech/folder/filename.jpg
        String[] parts = imageUrl.split("/upload/");
        String afterUpload = parts[1];                                    // v1234567/fintech/folder/filename.jpg
        String withoutVersion = afterUpload.replaceFirst("v\\d+/", "");  // fintech/folder/filename.jpg
        return withoutVersion.substring(0, withoutVersion.lastIndexOf(".")); // fintech/folder/filename (no extension)
    }
}