package com.redhat.k8soperator.cr;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExampleStatus extends ObservedGenerationAwareStatus {
}
