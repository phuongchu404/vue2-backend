package vn.mk.eid.web.controller.detainee;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.constant.FingerKind;
import vn.mk.eid.web.constant.FingerType;
import vn.mk.eid.web.dto.request.FingerprintCardCreateRequest;
import vn.mk.eid.web.service.FingerprintCardService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/fingerprint")
@RequiredArgsConstructor
public class FingerprintCardController {

    private final FingerprintCardService fingerprintCardService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult createFingerprintCard(
            @Valid @RequestPart(value = "payload") FingerprintCardCreateRequest request,
            @RequestPart(value = "rightThumb", required = false) MultipartFile rightThumb, // ngon cai phai
            @RequestPart(value = "rightIndex", required = false) MultipartFile rightIndex, // ngon tro phai
            @RequestPart(value = "rightMiddle", required = false) MultipartFile rightMiddle, // ngon giua phai
            @RequestPart(value = "rightRing", required = false) MultipartFile rightRing, // ngon ap u phai
            @RequestPart(value = "rightLittle", required = false) MultipartFile rightLittle, // ngon ut phai

            @RequestPart(value = "leftThumb", required = false) MultipartFile leftThumb,
            @RequestPart(value = "leftIndex", required = false) MultipartFile leftIndex,
            @RequestPart(value = "leftMiddle", required = false) MultipartFile leftMiddle,
            @RequestPart(value = "leftRing", required = false) MultipartFile leftRing,
            @RequestPart(value = "leftLittle", required = false) MultipartFile leftLittle,

            // anh 4 ngon tren va ca ban tay
            @RequestPart(value = "plainRightFour", required = false) MultipartFile plainRightFour, // 4 ngon tay phai
            @RequestPart(value = "plainLeftFour", required = false) MultipartFile plainLeftFour, // 4 ngon tay trai
            @RequestPart(value = "plainRightFull", required = false) MultipartFile plainRightFull, // ban tay phai
            @RequestPart(value = "plainLeftFull", required = false) MultipartFile plainLeftFull) { // ban tay trai

        Map<String, MultipartFile> fingerprintImages = new HashMap<>();

        // 10 ngon tay
        addImageToMap(fingerprintImages, FingerType.RIGHT_THUMB.name(), rightThumb);
        addImageToMap(fingerprintImages, FingerType.RIGHT_INDEX.name(), rightIndex);
        addImageToMap(fingerprintImages, FingerType.RIGHT_MIDDLE.name(), rightMiddle);
        addImageToMap(fingerprintImages, FingerType.RIGHT_RING.name(), rightRing);
        addImageToMap(fingerprintImages, FingerType.RIGHT_LITTLE.name(), rightLittle);

        addImageToMap(fingerprintImages, FingerType.LEFT_THUMB.name(), leftThumb);
        addImageToMap(fingerprintImages, FingerType.LEFT_INDEX.name(), leftIndex);
        addImageToMap(fingerprintImages, FingerType.LEFT_MIDDLE.name(), leftMiddle);
        addImageToMap(fingerprintImages, FingerType.LEFT_RING.name(), leftRing);
        addImageToMap(fingerprintImages, FingerType.LEFT_LITTLE.name(), leftLittle);

        // 4 ngon tay va ban tay
        addImageToMap(fingerprintImages, FingerType.RIGHT_FOUR.name(), plainRightFour);
        addImageToMap(fingerprintImages, FingerType.LEFT_FOUR.name(), plainLeftFour);
        addImageToMap(fingerprintImages, FingerType.RIGHT_FULL.name(), plainRightFull);
        addImageToMap(fingerprintImages, FingerType.LEFT_FULL.name(), plainLeftFull);

        request.setFingerprintImages(fingerprintImages);
        return fingerprintCardService.createFingerprintCard(request);
    }

    private void addImageToMap(Map<String, MultipartFile> map, String key, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            // Validate định dạng file
//            String contentType = file.getContentType();
//            if (contentType == null || !contentType.startsWith("image/")) {
//                throw new IllegalArgumentException("File " + key + " phải là định dạng ảnh");
//            }

            // Validate size file
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("File " + key + " cannot be larger than 5MB.");
            }

            map.put(key, file);
        }
    }
}
