package com.redhat.k8soperator;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.springboot.starter.test.EnableMockOperator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableMockOperator
@ActiveProfiles("test")
public class ApplicationUnitTest {

    @Autowired
    private KubernetesClient client;

    @Test
    public void whenContextLoaded_thenCrdRegistered() {
        assertThat(
                client.apiextensions()
                        .v1()
                        .customResourceDefinitions()
                        .withName("examplecrs.com.redhat.cleanbase")
                        .get()
        ).isNotNull();
    }
}