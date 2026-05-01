package com.backend.Services;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {


    record FileResponse(String secureUrl, String publicId) {
    }


    FileResponse uploadFileToCloudinary(MultipartFile file, String folder);

    void deleteImageFromCloudinary(String publicId);


}