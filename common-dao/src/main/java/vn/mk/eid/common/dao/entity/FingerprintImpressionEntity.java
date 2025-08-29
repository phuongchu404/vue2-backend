package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author mk
 * @date 06-Aug-2025
 */
// Bang luu tru thong tin ve van tay cua nguoi dung
@Getter
@Setter
@Entity
@Table(name = "fingerprint_impression")
public class FingerprintImpressionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "fingerprint_card_id", nullable = false)
    private Long fingerprintCardId; // id cua bang chi ban

    @Column(name = "finger", length = 20)
    private String finger; //'RIGHT_THUMB','RIGHT_INDEX','RIGHT_MIDDLE','RIGHT_RING','RIGHT_LITTLE','LEFT_THUMB','LEFT_INDEX','LEFT_MIDDLE','LEFT_RING','LEFT_LITTLE'  -- bắt buộc khi kind = ROLLED hoặc PLAIN_SINGLE

    @Column(name = "kind", length = 20)
    private String kind; //'ROLLED'-lăn từng ngón (10 ô 1..10), 'PLAIN_SINGLE'-ấn phẳng 1 ngón (nếu tách riêng),'PLAIN_RIGHT_FOUR','PLAIN_LEFT_FOUR','PLAIN_LEFT_THUMBS', 'PLAIN_RIGHT_THUMBS'

    @NotNull
    @Column(name = "bucket", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String bucket; // tên bucket minio nơi lưu trữ ảnh

    @NotNull
    @Column(name = "image_key", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String imageKey; // khóa của ảnh lưu trữ trên minio

    @NotNull
    @Column(name = "object_url", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String objectUrl; // URL của ảnh lưu trữ trên minio

    @Column(name = "quality_score")
    private Short qualityScore; // điểm chất lượng ảnh, từ 0 đến 100, 0 là kém nhất, 100 là tốt nhất

    @Column(name = "captured_at")
    @CreationTimestamp
    private LocalDateTime capturedAt;

}