package vn.mk.eid.web.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@UtilityClass
public class FileUtil {

    private static final Map<String, String> fileExtensionMap;

    static {
        fileExtensionMap = new HashMap<>();
        // MS Office
        fileExtensionMap.put("doc", "application/msword");
        fileExtensionMap.put("dot", "application/msword");
        fileExtensionMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        fileExtensionMap.put("xls", "application/vnd.ms-excel");
        fileExtensionMap.put("xlt", "application/vnd.ms-excel");
        fileExtensionMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileExtensionMap.put("xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12");
        fileExtensionMap.put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        fileExtensionMap.put("pdf", "application/pdf");
        fileExtensionMap.put("ppt", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("zip", "application/zip");
        fileExtensionMap.put("rar", "application/vnd.rar");
        fileExtensionMap.put("exe", "application/vnd.microsoft.portable-executable");

    }

    public static String getContentTypeByFileName(String fileName) {
        // 1. first use java's buildin utils
        FileNameMap mimeTypes = URLConnection.getFileNameMap();
        String contentType = mimeTypes.getContentTypeFor(fileName);
        // 2. nothing found -> lookup our in extension map to find types like ".doc" or ".docx"
        if (!StringUtils.hasText(contentType)) {
            String extension = FilenameUtils.getExtension(fileName);
            contentType = fileExtensionMap.get(extension);
        }
        return contentType;
    }

    @SneakyThrows
    public byte[] isToBytes(InputStream is) {
        byte[] targetArray = new byte[is.available()];
        int size = is.read(targetArray);
        log.debug("Size of input stream: {} bytes", size);
        return targetArray;
    }

    public static InputStream getInputStreamFromResource(String uri) {
        Resource resource = new ClassPathResource(uri);
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static HttpHeaders makeFileHeader(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        return headers;
    }

    public boolean fileNullOrEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    public static String getExtensionOfFile(String filename) {
        return FilenameUtils.getExtension(filename);
    }
}