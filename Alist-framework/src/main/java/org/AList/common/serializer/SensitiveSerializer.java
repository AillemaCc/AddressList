package org.AList.common.serializer;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.AList.annotation.Sensitive;
import org.AList.common.enums.SensitiveType;

import java.io.IOException;
import java.util.Objects;

/**
 * 脱敏序列化器
 */
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveType type;

    public SensitiveSerializer() {
    }

    public SensitiveSerializer(SensitiveType type, int prefixLen, int suffixLen, char maskChar) {
        this.type = type;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        String result = switch (type) {
            case PHONE -> DesensitizedUtil.mobilePhone(value);
            case EMAIL -> DesensitizedUtil.email(value);
        };
        gen.writeString(result);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        Sensitive sensitive = property.getAnnotation(Sensitive.class);
        if (sensitive != null && Objects.equals(property.getType().getRawClass(), String.class)) {
            return new SensitiveSerializer(sensitive.type(), sensitive.prefixLen(), 
                                         sensitive.suffixLen(), sensitive.maskChar());
        }
        return this;
    }
}