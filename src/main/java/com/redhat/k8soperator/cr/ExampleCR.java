package com.redhat.k8soperator.cr;

import com.redhat.k8soperator.cr.spec.ExampleSpec;
import com.redhat.k8soperator.cr.status.ExampleStatus;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;

@Group(ExampleCR.CRD_GROUP)
@Version("v1")
@Kind(ExampleCR.CR_KIND)
public class ExampleCR extends CustomResource<ExampleSpec, ExampleStatus> implements Namespaced {
    public static final String CRD_GROUP = "com.redhat.cleanbase";
    public static final String CR_KIND = "ExampleCR";
}
