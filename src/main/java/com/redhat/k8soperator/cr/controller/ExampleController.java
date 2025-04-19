package com.redhat.k8soperator.cr.controller;

import com.redhat.k8soperator.cr.ExampleCR;
import com.redhat.k8soperator.cr.res.ExampleDeploymentResource;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@ControllerConfiguration(dependents = {
        @Dependent(name = ExampleDeploymentResource.PROVIDER_ID, type = ExampleDeploymentResource.class)
})
public class ExampleController implements Reconciler<ExampleCR>, Cleaner<ExampleCR> {
    // 傳入新的 cr 物件
    // 可能會有很多 cr 物件 , 名稱都不同, api version kind name 要一樣才可以說是同一個資源
    @Override
    public UpdateControl<ExampleCR> reconcile(ExampleCR resource, Context<ExampleCR> context) throws Exception {
        val metadata = resource.getMetadata();
        val resourceID = new ResourceID(metadata.getName(), metadata.getNamespace());
        val primaryCache = context.getPrimaryCache();
        val exampleCR = primaryCache.get(resourceID);
        // cr 參數驗證
        if (exampleCR.isEmpty()) {
            // 新增
            System.out.println("新增");
        } else {
            // 修改
            System.out.println("修改");
        }
        // 子資源自己管,頂多是改變狀態
        return UpdateControl.noUpdate();
    }

    @Override
    public DeleteControl cleanup(ExampleCR resource, Context<ExampleCR> context) {
        // 如果子資源子資源類自管
        // 且刪除工作沒有完全結束,就調用  DeleteControl.noFinalizerRemoval()
        // 如果知道會需要一些時間,可以再調用 .rescheduleAfter(時間區間)

        // 如果子資源主資源類管,需要等子資源或其他資源都清理乾淨,再進行 defaultDelete
        return DeleteControl.defaultDelete();
    }
}
