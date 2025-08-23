package com.ratefood.app.service;

import com.ratefood.app.enums.ImageType;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
public class ImageService {

    private final MinioClient minioClient;
    private final String bucketName = "foodapp"; // your bucket name

    public ImageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void uploadImage(String imageUrl, String objectKey) throws Exception {
        // Download image from the given URL
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Java client");
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);
        try (InputStream inputStream = connection.getInputStream()) {

            // Optionally, you can copy the input stream to a byte array or temp file
            // Here, we will upload directly using InputStream

            // Upload to MinIO bucket
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(inputStream, -1, 10485760) // -1 to indicate unknown size, max 10MB part size
                            .contentType(connection.getContentType())
                            .build()
            );

            System.out.println("Uploaded image to MinIO with key: " + objectKey);
        } catch (Exception ex) {
            // Log and rethrow or handle error accordingly
            throw new RuntimeException("Failed to download or upload image", ex);
        } finally {
            connection.disconnect();
        }
    }

    public String getPresignedUrl(String objectKey, int expiryDurationMinutes) throws Exception {
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectKey)
                        .expiry(expiryDurationMinutes, TimeUnit.MINUTES)
                        .build()
        );
//        System.out.println("Generated presigned URL: " + url);
        return url;
    }

    public String getPlaceholder(ImageType type ) throws Exception {
        if(type == ImageType.DISH){
            String key = "PLACEHOLDER/" + ImageType.DISH  + "/dish_1.png";
            return getPresignedUrl(key, 100);
        }
        if(type == ImageType.RESTAURANT){
            String key = "PLACEHOLDER/" + ImageType.RESTAURANT  + "/restaurant_1.png";
            return getPresignedUrl(key, 100);
        }
        return "";
    }


}
