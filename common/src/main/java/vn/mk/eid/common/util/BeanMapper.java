package vn.mk.eid.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * bean
 *
 * @author mk.com.vn
 * @date 2017/10/17
 */
@Slf4j
public class BeanMapper {
    public static <T> T copy(Object source, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("failed copy {} to {}, caused: {}", source, clazz.getName(), Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
    }

    public static <S, T> List<T> listCopy(List<S> sources, Class<T> clazz) {
        return sources.stream().map(s -> copy(s, clazz)).collect(Collectors.toList());
    }

    public static <S, T> List<T> iterableToListCopy(Iterable<S> sources, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        sources.forEach(s -> {
            list.add(copy(s, clazz));
        });
        return list;
    }

    public static <S, T> T optionalCopy(Optional<S> sources, Class<T> clazz) {
        if (sources.isPresent()) {
            return copy(sources.get(), clazz);
        }
        return null;
    }
}
