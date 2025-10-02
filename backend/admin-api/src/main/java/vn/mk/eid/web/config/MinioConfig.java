package vn.mk.eid.web.config;


import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.access.name}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String secretKey;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.bucket-private}")
    private String bucketPrivate;

    @Value("${minio.bucket-public}")
    private String bucketPublic;

    @Value("${minio.bucket-public-policy}")
    private String bucketPublicPolicy;

    @SneakyThrows
    @Bean
    public MinioClient generateMinioClient() {
        log.info("Initializing Minio client with access key: {}", accessKey);

        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .httpClient(getUnsafeOkHttpClient())
                .build();

        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketPrivate).build());
        if (!found) {
            log.info("Creating private bucket: {}", bucketPrivate);
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketPrivate).build());
        } else {
            log.info("Private bucket already exists: {}", bucketPrivate);
        }

        found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketPublic).build());
        if (!found) {
            log.info("Creating public bucket: {}", bucketPublic);
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketPublic).build());
            // Set public policy for the public bucket
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketPublic).config(bucketPublicPolicy).build());
            log.info("Public bucket created with policy: {}", bucketPublicPolicy);
        } else {
            log.info("Public bucket already exists: {}", bucketPublic);
        }

        return minioClient;
    }
    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
