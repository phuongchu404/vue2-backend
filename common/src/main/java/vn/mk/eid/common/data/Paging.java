package vn.mk.eid.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author mk.com.vn
 * @date 2018/7/17 10:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paging<T> implements Serializable {

    private Long totalElements;

    private List<T> content;

    public static <T> Paging<T> empty() {
        List<T> emptyData = Collections.emptyList();
        return new Paging<>(0L, emptyData);
    }
}