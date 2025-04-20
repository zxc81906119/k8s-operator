package com.redhat.k8soperator.cr.managed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.redhat.k8soperator.cr.ExampleCR;
import com.redhat.k8soperator.util.BuilderHelper;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceIDMatcherDiscriminator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import lombok.val;

@KubernetesDependent(resourceDiscriminator = ExampleDeploymentResource.Discriminator.class)
public class ExampleDeploymentResource extends CRUDKubernetesDependentResource<Deployment, ExampleCR> {

    public static final String PROVIDER_ID = "example-deployment";
    public static final String META_LABEL_APP = "app";

    private final Deployment baseTemplate;

    public ExampleDeploymentResource() {
        super(Deployment.class);
        baseTemplate = BuilderHelper.loadTemplate(
                new TypeReference<>() {
                },
                "templates/jenkins-deployment.yaml"
        );
    }

    @Override
    protected Deployment desired(ExampleCR primary, Context<ExampleCR> context) {
        // 根據主資源的 cr 來創建子資源的結果
        // 如果不需要此資源就回傳 null
        // label + name + namespace 已設置
        val meta = BuilderHelper.fromPrimary(primary, PROVIDER_ID)
                .addToLabels(META_LABEL_APP, PROVIDER_ID)
                .build();
        // match labels 設置
        // template meta label 也要設置
        val deploymentBuilder = new DeploymentBuilder(baseTemplate);
        val spec = primary.getSpec();

        return deploymentBuilder
                .withMetadata(meta)
                .editOrNewSpec()
                .withReplicas(spec.getReplicas())
                .editOrNewSelector()
                .withMatchLabels(meta.getLabels())
                .and()
                // pod
                .editOrNewTemplate()
                .editOrNewMetadata()
                .withLabels(meta.getLabels())
                .and()
                .editOrNewSpec()
                .editContainer(0)
                .withImage("%s:%s".formatted(spec.getImageRepo(), spec.getImageTag()))
                .and()
                .and()
                .and()
                .and()
                .build();
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
