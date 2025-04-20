package com.redhat.k8soperator.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.ManagedFieldsEntry;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.CustomResource;
import lombok.val;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public final class BuilderHelper {

    private static final ObjectMapper om;

    static {
        // 寫這是未來需要更改配置
        om = new ObjectMapper(new YAMLFactory());
    }

    public static <T extends CustomResource<?, ?>> ObjectMetaBuilder fromPrimary(T primary, String component) {
        val metadata = primary.getMetadata();
        return new ObjectMetaBuilder()
                .withNamespace(metadata.getNamespace())
                .withName(metadata.getName() + "-" + component);
    }

    public static <T> T loadTemplate(TypeReference<T> reference, String resource) {
        try (val is = new ClassPathResource(resource).getInputStream()) {
            return om.readValue(is, reference);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to load classpath resource '" + resource + "': " + ioe.getMessage());
        }
    }


}
