package vn.mk.eid.web.controller.detainee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.constant.FingerKind;
import vn.mk.eid.web.constant.FingerType;
import vn.mk.eid.web.dto.request.FingerprintCardCreateRequest;
import vn.mk.eid.web.dto.request.QueryFingerPrintRequest;
import vn.mk.eid.web.service.FingerprintCardService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/fingerprint")
@RequiredArgsConstructor
public class FingerprintCardController {

    private final FingerprintCardService fingerprintCardService;

    @GetMapping
    public ServiceResult getWithPaging(
            QueryFingerPrintRequest request,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "1") int pageNo,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return fingerprintCardService.getWithPaging(pageable, request);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult createFingerprintCard(
            @Valid @RequestPart(value = "payload") FingerprintCardCreateRequest request,
            @RequestPart(value = "RIGHT_THUMB", required = false) MultipartFile rightThumb, // ngon cai phai
            @RequestPart(value = "RIGHT_INDEX", required = false) MultipartFile rightIndex, // ngon tro phai
            @RequestPart(value = "RIGHT_MIDDLE", required = false) MultipartFile rightMiddle, // ngon giua phai
            @RequestPart(value = "RIGHT_RING", required = false) MultipartFile rightRing, // ngon ap u phai
            @RequestPart(value = "RIGHT_LITTLE", required = false) MultipartFile rightLittle, // ngon ut phai

            @RequestPart(value = "LEFT_THUMB", required = false) MultipartFile leftThumb,
            @RequestPart(value = "LEFT_INDEX", required = false) MultipartFile leftIndex,
            @RequestPart(value = "LEFT_MIDDLE", required = false) MultipartFile leftMiddle,
            @RequestPart(value = "LEFT_RING", required = false) MultipartFile leftRing,
            @RequestPart(value = "LEFT_LITTLE", required = false) MultipartFile leftLittle,

            // anh 4 ngon tren va ca ban tay

            @RequestPart(value = "RIGHT_FOUR", required = false) MultipartFile plainRightFour, // 4 ngon tay phai
            @RequestPart(value = "LEFT_FOUR", required = false) MultipartFile plainLeftFour, // 4 ngon tay trai
            @RequestPart(value = "RIGHT_FULL", required = false) MultipartFile plainRightFull, // ban tay phai
            @RequestPart(value = "LEFT_FULL", required = false) MultipartFile plainLeftFull, // ban tay trai
            @RequestPart(value = "BOTH_THUMBS", required = false) MultipartFile bothThumbs) {
        Map<String, MultipartFile> fingerprintImages = createFingerPrintMap(rightThumb, rightIndex, rightMiddle, rightRing, rightLittle, leftThumb, leftIndex,
                leftMiddle, leftRing, leftLittle, plainRightFour, plainLeftFour, plainRightFull, plainLeftFull, bothThumbs);

        request.setFingerprintImages(fingerprintImages);
        return fingerprintCardService.createFingerprintCard(request);
    }

    @NotNull
    private Map<String, MultipartFile> createFingerPrintMap(MultipartFile rightThumb, MultipartFile rightIndex, MultipartFile rightMiddle, MultipartFile rightRing,
                                                            MultipartFile rightLittle, MultipartFile leftThumb, MultipartFile leftIndex, MultipartFile leftMiddle,
                                                            MultipartFile leftRing, MultipartFile leftLittle, MultipartFile plainRightFour, MultipartFile plainLeftFour,
                                                            MultipartFile plainRightFull, MultipartFile plainLeftFull, MultipartFile bothThumbs) {
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

        //2 ngon cai chum
        addImageToMap(fingerprintImages, FingerType.BOTH_THUMBS.name(), bothThumbs);
        return fingerprintImages;
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

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult updateFingerprintCard(
            @Valid @RequestPart(value = "payload") FingerprintCardCreateRequest request,
            @PathVariable Long id,

            @RequestPart(value = "RIGHT_THUMB", required = false) MultipartFile rightThumb, // ngon cai phai
            @RequestPart(value = "RIGHT_INDEX", required = false) MultipartFile rightIndex, // ngon tro phai
            @RequestPart(value = "RIGHT_MIDDLE", required = false) MultipartFile rightMiddle, // ngon giua phai
            @RequestPart(value = "RIGHT_RING", required = false) MultipartFile rightRing, // ngon ap u phai
            @RequestPart(value = "RIGHT_LITTLE", required = false) MultipartFile rightLittle, // ngon ut phai

            @RequestPart(value = "LEFT_THUMB", required = false) MultipartFile leftThumb,
            @RequestPart(value = "LEFT_INDEX", required = false) MultipartFile leftIndex,
            @RequestPart(value = "LEFT_MIDDLE", required = false) MultipartFile leftMiddle,
            @RequestPart(value = "LEFT_RING", required = false) MultipartFile leftRing,
            @RequestPart(value = "LEFT_LITTLE", required = false) MultipartFile leftLittle,

            // anh 4 ngon tren va ca ban tay

            @RequestPart(value = "RIGHT_FOUR", required = false) MultipartFile plainRightFour, // 4 ngon tay phai
            @RequestPart(value = "LEFT_FOUR", required = false) MultipartFile plainLeftFour, // 4 ngon tay trai
            @RequestPart(value = "RIGHT_FULL", required = false) MultipartFile plainRightFull, // ban tay phai
            @RequestPart(value = "LEFT_FULL", required = false) MultipartFile plainLeftFull,// ban tay trai
            @RequestPart(value = "BOTH_THUMBS", required = false) MultipartFile bothThumbs) { // 2 ngon tay cai chum

        Map<String, MultipartFile> fingerprintImages = createFingerPrintMap(rightThumb, rightIndex, rightMiddle, rightRing, rightLittle, leftThumb, leftIndex,
                leftMiddle, leftRing, leftLittle, plainRightFour, plainLeftFour, plainRightFull, plainLeftFull, bothThumbs);

        request.setFingerprintImages(fingerprintImages);
        return fingerprintCardService.updateFingerprintCard(request, id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Finger Print Card by ID", description = "Retrieve finger print card information by ID")
    public ServiceResult getFingerprintCardById(
            @Parameter(description = "Finger Print Card ID") @PathVariable Long id) {
        return fingerprintCardService.getFingerprintCardById(id);
    }

    @GetMapping("/detainee/{detaineeId}")
    public ServiceResult getFingerprintCardByDetaineeId(@PathVariable("detaineeId") Long detaineeId) {
        return fingerprintCardService.getFingerprintCardByDetaineeId(detaineeId);
    }

//    @GetMapping("/impression/{cardId}")
//    public ServiceResult getFingerprintImpressions(@PathVariable("cardId") Long cardId) {
//        return fingerprintCardService.getFingerprintImpressions(cardId);
//    }

    @DeleteMapping("/{id}")
    public ServiceResult deleteFingerprintCard(@PathVariable Long id) {
        return fingerprintCardService.deleteFingerPrint(id);
    }
}
