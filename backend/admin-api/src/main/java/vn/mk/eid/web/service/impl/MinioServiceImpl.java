package vn.mk.eid.web.service.impl;

import com.github.f4b6a3.ulid.UlidCreator;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.web.constant.WebConstants;
import vn.mk.eid.web.exception.RestException;
import vn.mk.eid.web.service.MinioService;
import vn.mk.eid.web.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private static final String UPLOAD_SUCCESS = "upload thành công";

    private static final String UPLOAD_FAIL = "lỗi upload minio";

    private final MinioClient minioClient;

    @Value("${minio.bucket-private}")
    private String bucketPrivate;

    @Value("${minio.bucket-public}")
    private String bucketPublic;

    public List<Bucket> getAllBucket() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            log.error("lỗi get bucket");
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @SneakyThrows
    public String uploadFile(MultipartFile file, String dir) {
        return uploadFile(file, dir, UploadOption.builder().isPublic(false).build());
    }

    @SneakyThrows
    public String uploadFile(MultipartFile file, String dir, UploadOption option) {
        return uploadFile(file, null, dir, option).getLeft();
    }

    @SneakyThrows
    @Override
    public Pair<String, String> uploadFile(MultipartFile file, String name, String dir) {
        return uploadFile(file, name, dir, UploadOption.builder().isPublic(false).build());
    }

    @Override
    @SneakyThrows
    public Pair<String, String> uploadFile(MultipartFile file, String name, String dir, UploadOption option) {
        if (name != null) name = renameFile(file.getOriginalFilename(), name);
        return uploadFile(file.getInputStream(), name, dir, option);
    }

    @SneakyThrows
    @Override
    public String uploadFileJpg(MultipartFile file, String name, String dir, UploadOption option) {
        return uploadFile(file.getInputStream(), name, dir, option).getLeft();
    }

    @Override
    public String uploadFile(InputStream content, String name, String dir, UploadOption option, boolean rename) {
        if (rename) name = renameFile(name);
        return uploadFile(content, name, dir, option).getLeft();
    }

    public Pair<String, String> uploadFile(InputStream content, String fileName, String dir, UploadOption option) {
        try {
            String filePath = StringUtil.isBlank(dir) ? fileName : dir.concat(WebConstants.CommonSymbol.FORWARD_SLASH).concat(fileName);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(option.isPublic() ? bucketPublic : bucketPrivate)
                    .object(filePath)
                    .stream(content, content.available(), 0L)
                    .build());
            log.info(UPLOAD_SUCCESS);
            return Pair.of(fileName, filePath);
        } catch (Exception e) {
            log.info(UPLOAD_FAIL);
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public String uploadFile(InputStream content, String name, String dir) {
        name = renameFile(name);
        return uploadFile(content, name, dir, UploadOption.builder().isPublic(true).build()).getLeft();
    }

    public String uploadFileNew(InputStream content, String fileName, String id, String dir) {
        fileName = renameFileNew(fileName, id);
        return uploadFile(content, fileName, dir, UploadOption.builder().isPublic(true).build()).getLeft();
    }

    public String uploadFileTemplate(InputStream content, String name) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketPublic)
                    .object("template/" + name)
                    .stream(content, content.available(), 0L)
                    .build());
            log.info(UPLOAD_SUCCESS);
            return name;
        } catch (Exception e) {
            log.info(UPLOAD_FAIL);
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String renameFile(String name) {
        return FilenameUtils.removeExtension(name) + "_" + UlidCreator.getMonotonicUlid(System.currentTimeMillis()) + "." + FilenameUtils.getExtension(name);
    }

    private String renameFile(String oldName, String newName) {
        return newName + "." + FilenameUtils.getExtension(oldName);
    }

    private String renameFileNew(String name, String id) {
        return FilenameUtils.removeExtension(name) + "_" + id + "." + FilenameUtils.getExtension(name);
    }

    public byte[] downloadFile(String fileName, String directory) {
        return this.downloadFile(fileName, directory, DownloadOption.builder().isPublic(true).build());
    }

    @Override
    public byte[] downloadFile(String filePath) {
        return this.downloadFile(filePath, "");
    }

    @Override
    public byte[] downloadFile(String fileName, String dir, DownloadOption option) {
        try {
            String filePath = StringUtil.isBlank(dir) ? fileName : dir.concat(WebConstants.CommonSymbol.FORWARD_SLASH).concat(fileName);
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .object(filePath)
                    .bucket(option.isPublic() ? bucketPublic : bucketPrivate)
                    .build());
            byte[] content = IOUtils.toByteArray(inputStream);
            inputStream.close();
            log.info("get file {} thành công", fileName);
            return content;
        } catch (Exception e) {
            log.info("Lỗi get file minio");
            log.error(e.getMessage(), e);
            throw new RestException(HttpStatus.BAD_REQUEST,"Không tìm thấy file: " + fileName);
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        return getFileUrl(filePath, "", DownloadOption.builder().isPublic(true).build());
    }

    @Override
    public String getFileUrl(String fileName, String directory) {
        return getFileUrl(fileName, directory, DownloadOption.builder().isPublic(true).build());
    }

    @Override
    public String getFileUrl(String fileName, String directory, DownloadOption option) {
        try {
            String filePath = StringUtil.isBlank(directory) ? fileName : directory.concat("/").concat(fileName);

            GetPresignedObjectUrlArgs.Builder getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(option.isPublic() ? bucketPublic : bucketPrivate)
                    .object(filePath);
            if (option.getExpirationDuration() > 0) {
                getPresignedObjectUrlArgs.expiry(option.getExpirationDuration(), option.getExpirationUnit());
            }
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs.build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            log.info("Lỗi get image path");
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String getImageUrl(String dir, String bucket, String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET).bucket(bucket)
                    .object(dir + WebConstants.CommonSymbol.FORWARD_SLASH + fileName)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            log.info("Lỗi get image path");
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean removeFile(String fileName, String dir, DownloadOption option) {
        try {
            String filePath = StringUtil.isBlank(dir) ? fileName : dir.concat(WebConstants.CommonSymbol.FORWARD_SLASH).concat(fileName);
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(option.isPublic() ? bucketPublic : bucketPrivate)
                    .object(filePath)
                    .build());
            return true;
        } catch (Exception e) {
            log.info("xóa file thất bại");
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public void moveFiles(String oldDir, String newDir, DownloadOption option) {
        try {
            String bucket = option.isPublic() ? bucketPublic : bucketPrivate;
            Iterable<Result<Item>> objectNames = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucket)
                    .prefix(oldDir)
                    .delimiter(WebConstants.CommonSymbol.COMMA)
                    .build());
            objectNames.forEach(objectName -> {
                try {
                    String objName = objectName.get().objectName();
                    String destObjName = objName.replace(oldDir, newDir);
                    ObjectWriteResponse objectWriteResponse = minioClient.copyObject(CopyObjectArgs.builder()
                            .bucket(bucket)
                            .object(destObjName)
                            .source(CopySource.builder()
                                    .bucket(bucket)
                                    .object(objName)
                                    .build())
                            .build());
                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objName)
                            .build());
                    log.info("Object {} moved to {}", objName, objectWriteResponse.object());
                } catch (Exception ex) {
                    log.error("Object copy failed: {}", objectName, ex);
                }
            });
        } catch (Exception e) {
            log.info("Lỗi move file minio");
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @SneakyThrows
    public List<ComposeSource> buildComposeSources(List<String> fileNames, String dir, UploadOption option){
        List<ComposeSource> composeSources = new ArrayList<>();
        try {
            fileNames.forEach(fileName -> {
                String filePath = StringUtil.isBlank(dir) ? fileName : dir.concat(WebConstants.CommonSymbol.FORWARD_SLASH).concat(fileName);
                composeSources.add(ComposeSource.builder()
                        .bucket(option.isPublic() ? bucketPublic : bucketPrivate)
                        .object(filePath)
                        .build());
            });
        } catch (Exception e) {
            log.info("build compose sources error");
            log.error(e.getMessage(), e);

        }
        return composeSources;
    }

    @Override
    @SneakyThrows
    public void composeFiles(List<ComposeSource> composeSources, String fileName, String dir, UploadOption option){
        try{
            String filePath = StringUtil.isBlank(dir) ? fileName : dir.concat(WebConstants.CommonSymbol.FORWARD_SLASH).concat(fileName);
            minioClient.composeObject(ComposeObjectArgs.builder()
                    .bucket(option.isPublic() ? bucketPublic : bucketPrivate)
                    .object(filePath)
                    .sources(composeSources)
                    .build());
        } catch (Exception e) {
            log.info("ghép file thất bại");
            log.error(e.getMessage(), e);
        }
    }
}
