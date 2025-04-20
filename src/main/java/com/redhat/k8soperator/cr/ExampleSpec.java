package com.redhat.k8soperator.cr;

import lombok.Data;

@Data
public class ExampleSpec {
    private String imageRepo = "jenkins/jenkins";
    private String imageTag = "lts";
    private int replicas = 1;
}
