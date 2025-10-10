package ru.astondevs.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class BeanUtilsHelper {
    public static void copyNonNullProperties(Object source, Object target, String... ignoreFields) {
        BeanUtils.copyProperties(source, target, getNullAndIgnoredPropertyNames(source, ignoreFields));
    }

    private static String[] getNullAndIgnoredPropertyNames(Object source, String... ignoreFields) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> ignoreNames = new HashSet<>();

        if (ignoreFields != null) {
            Collections.addAll(ignoreNames, ignoreFields);
        }

        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                ignoreNames.add(pd.getName());
            }
        }

        return ignoreNames.toArray(new String[0]);
    }
}
