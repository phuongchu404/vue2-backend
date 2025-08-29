package vn.mk.eid.common.dao.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;

// Bang anh chan dung trong DANH BAN
@Getter
@Setter
@Entity
@Table(name = "photo")
public class PhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "identity_record_id", nullable = false)
    private Long identityRecordId; // id danh ban

    @NotNull
    @Column(name = "view", nullable = false, length = 20)
    private String view;  // 'FRONT', 'LEFT_PROFILE', 'RIGHT_PROFILE'

    @NotNull
    @Column(name = "bucket", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String bucket; // Ten bucket luu tru anh

    @NotNull
    @Column(name = "object_key", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String objectKey; // Khoa cua anh luu tru tren minio

    @NotNull
    @Column(name = "object_url", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String objectUrl; // URL cua anh luu tru tren minio

    @Column(name = "mime_type")
    @Type(type = "org.hibernate.type.TextType")
    private String mimeType; // Mime type cua anh, vd: image/jpeg, image/png

    @Column(name = "size_bytes")
    private Long sizeBytes; // Kich thuoc cua anh theo byte

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

}