package com.gopikishan.mobileshop.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageStorageService {

    private final Cloudinary cloudinary;

    public ImageStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile image) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                image.getBytes(),
                ObjectUtils.asMap(
                        "folder", "mobile-shop/products",
                        "resource_type", "image"
                )
        );

        Object secureUrl = uploadResult.get("secure_url");

        if (secureUrl == null){
            throw new IOException("Image is not loaded from cloud storage");
        }

        return secureUrl.toString();
    }
}
