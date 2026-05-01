package com.backend.Services.Impl;

import com.backend.Services.FileService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final Cloudinary cloudinary;

    @Override
    public FileResponse uploadFileToCloudinary(MultipartFile file, String folder) {
        try {
            log.info("Uploading file '{}' to Cloudinary folder '{}'", file.getOriginalFilename(), folder);

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto"
                    )
            );

            String secureUrl = result.get("secure_url").toString();
            String publicId  = result.get("public_id").toString();

            log.info("Upload successful — publicId: {}, url: {}", publicId, secureUrl);
            return new FileResponse(secureUrl, publicId);

        } catch (IOException e) {
            log.error("Failed to upload file '{}' to Cloudinary", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    @Override
    public void deleteImageFromCloudinary(String publicId) {
        try {
            log.info("Deleting file from Cloudinary — publicId: '{}'", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Successfully deleted file — publicId: '{}'", publicId);

        } catch (IOException e) {
            log.error("Failed to delete file from Cloudinary — publicId: '{}'", publicId, e);
            throw new RuntimeException("Failed to delete file from Cloudinary: " + publicId, e);
        }
    }
}