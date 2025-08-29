package vn.mk.eid.web.service;

import io.minio.ComposeSource;
import lombok.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface MinioService {

    @SneakyThrows
    List<ComposeSource> buildComposeSources(List<String> fileNames, String dir, UploadOption option);

    @SneakyThrows
    void composeFiles(List<ComposeSource> composeSources, String fileName, String dir, UploadOption option);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class UploadOption {

        private boolean isPublic;

        private long retentionCount;

        private TemporalUnit retentionUnit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class DownloadOption {

        private boolean isPublic;

        private int expirationDuration = 0;

        private TimeUnit expirationUnit = TimeUnit.HOURS;
    }

    byte[] downloadFile(String fileName, String directory, DownloadOption option);

    byte[] downloadFile(String fileName, String directory);

    byte[] downloadFile(String filePath);

    Pair<String, String> uploadFile(MultipartFile file, String name, String dir);

    Pair<String, String> uploadFile(MultipartFile file, String name, String dir, UploadOption option);

    String uploadFileJpg(MultipartFile file, String name, String dir, UploadOption option);

    String uploadFile(InputStream content, String name, String dir, UploadOption option, boolean rename);

    String uploadFile(InputStream content, String name, String dir);

    String uploadFileNew(InputStream content, String fileName, String id, String dir);

    String getFileUrl(String fileName, String directory, DownloadOption option);

    String getFileUrl(String fileName, String directory);

    String getFileUrl(String filePath);

    boolean removeFile(String fileName, String directory, DownloadOption option);
}
