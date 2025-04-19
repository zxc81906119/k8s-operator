package com.redhat.k8soperator.cr.res;

import com.fasterxml.jackson.core.type.TypeReference;
import com.redhat.k8soperator.cr.ExampleCR;
import com.redhat.k8soperator.util.BuilderHelper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import lombok.val;

import java.util.Map;

@KubernetesDependent(resourceDiscriminator = ExampleDeploymentResource.Discriminator.class)
public class ExampleDeploymentResource extends CRUDKubernetesDependentResource<Deployment, ExampleCR> {

    public static final String PROVIDER_ID = "example-deployment";
    private final Deployment baseTemplate;

    public ExampleDeploymentResource() {
        super(Deployment.class);
        baseTemplate = BuilderHelper.loadTemplate(new TypeReference<>() {
        }, "templates/api-server-deployment.yaml");
    }

    @Override
    protected Deployment desired(ExampleCR primary, Context<ExampleCR> context) {
        // 根據主資源的 cr 來創建子資源的結果
        // 如果不需要此資源就回傳 null

        val meta = BuilderHelper.fromPrimary(primary, PROVIDER_ID)
                .build();

        val deploymentBuilder = new DeploymentBuilder(baseTemplate);
        return deploymentBuilder
                .withMetadata(meta)
                .editOrNewSpec()
                .editOrNewSelector()
                .withMatchLabels(meta.getLabels())
                .and()
                .editOrNewTemplate()
                .editOrNewMetadata()
                .withLabels(meta.getLabels())
                .and()
                .and()
                .and()
                .build();
    }

    private DeploymentSpec buildSpec(ExampleCR primary, ObjectMeta primaryMeta) {

        val deploymentSpecBuilder = new DeploymentSpecBuilder();
        return deploymentSpecBuilder
                .withSelector(buildSelector(primaryMeta.getLabels()))
                .withReplicas(1) // Dependenty track does not support multiple pods (yet)
//                .withTemplate(buildPodTemplate(primary, primaryMeta))
                .build();
    }

    private LabelSelector buildSelector(Map<String, String> labels) {
        val labelSelectorBuilder = new LabelSelectorBuilder();
        return labelSelectorBuilder
                .addToMatchLabels(labels)
                .build();
    }

    private PodTemplateSpec buildPodTemplate(ExampleCR primary, ObjectMeta primaryMeta) {

        val podTemplateSpecBuilder = new PodTemplateSpecBuilder();
        return podTemplateSpecBuilder
                .withMetadata(primaryMeta)
                .build();
    }

    private PodSpec buildPodSpec(ExampleCR primary) {

        //@formatter:off
        return new PodSpecBuilder(baseTemplate.getSpec().getTemplate().getSpec())
                .build();
        //@formatter:on
    }

    static class Discriminator extends ResourceIDMatcherDiscriminator<Deployment, ExampleCR> {
        public Discriminator() {
            // arg1 -> event source name
            // arg2 -> 主資源所管理的子資源對應的 DependentResource 唯一標示
            super(PROVIDER_ID, (exampleCR) -> {
                // k8s 限定 apiVersion + kind + name 是唯一
                // cr 就是 apiVersion + kind , 所以 name 一定就是唯一
                val metadata = exampleCR.getMetadata();
                return new ResourceID(metadata.getName() + "-" + PROVIDER_ID, metadata.getNamespace());
            });
        }
    }

}
