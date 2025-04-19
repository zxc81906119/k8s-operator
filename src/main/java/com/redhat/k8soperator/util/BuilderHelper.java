package com.redhat.k8soperator.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.ManagedFieldsEntry;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.CustomResource;
import lombok.val;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public final class BuilderHelper {

    private static final ObjectMapper om;

    static {
        om = new ObjectMapper(new YAMLFactory());
    }

    public static <T extends CustomResource<?, ?>> ObjectMetaBuilder fromPrimary(T primary, String component) {
        return new ObjectMetaBuilder()
                // 跟主資源相同 namespace
                .withNamespace(primary.getMetadata().getNamespace())
                .withManagedFields((List<ManagedFieldsEntry>) null)
                .addToLabels("component", component)
                .addToLabels("name", primary.getMetadata().getName())
                .withName(primary.getMetadata().getName() + "-" + component)
                .addToLabels("ManagedBy", primary.getFullResourceName());
    }

    public static <T> T loadTemplate(TypeReference<T> reference, String resource) {

        val cl = getClassLoader();
        try (val is = cl.getResourceAsStream(resource)) {
            return om.readValue(is, reference);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to load classpath resource '" + resource + "': " + ioe.getMessage());
        }

    }

    private static ClassLoader getClassLoader() {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader())
                .orElseGet(BuilderHelper.class::getClassLoader);
    }

}
