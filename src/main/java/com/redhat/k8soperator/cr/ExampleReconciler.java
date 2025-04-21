package com.redhat.k8soperator.cr;

import com.redhat.k8soperator.cr.managed.ExampleDeploymentResource;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@ControllerConfiguration(
        dependents = {
                @Dependent(
                        name = ExampleDeploymentResource.PROVIDER_ID,
                        type = ExampleDeploymentResource.class
                )
        }
)
public class ExampleReconciler implements Reconciler<ExampleCR>, Cleaner<ExampleCR> {
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
        // 如子資源讓子資源類自管且父資源清除工作未結束
        // 或父資源管理子資源且父或子資源清除工作未結束
        // 則調用 DeleteControl.noFinalizerRemoval()
        // 如需一些時間再判斷 , 可再調用 .rescheduleAfter(時間區間)

        // 如清除工作結束 , 則調用 DeleteControl.defaultDelete();
        return DeleteControl.defaultDelete();
    }
}
