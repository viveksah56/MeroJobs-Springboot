package com.backend.Converter;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    @NonNull
    public <T extends Enum> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return value -> {
            if (value.isBlank()) return null;
            try {
                return (T) Enum.valueOf(targetType, value.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid value '" + value + "' for enum " + targetType.getSimpleName());
            }
        };
    }
}