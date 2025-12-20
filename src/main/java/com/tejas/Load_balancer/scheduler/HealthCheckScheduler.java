package com.tejas.Load_balancer.scheduler;

import com.tejas.Load_balancer.model.WorkerNode;
import com.tejas.Load_balancer.service.LoadBalancerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HealthCheckScheduler {
    private final LoadBalancerService loadBalancerService;
    private final RestTemplate restTemplate;

    @Scheduled(fixedRate=10000)
    public void checkHealth(){
        List<WorkerNode> workers = loadBalancerService.getAllWorkers();
        for (WorkerNode worker : workers) {
            try{
                String healthUrl = worker.getUrl()+"/api/health";
                ResponseEntity<String> response= restTemplate.getForEntity(healthUrl, String.class);
                if(response.getStatusCode().is2xxSuccessful()){
                    loadBalancerService.markWorkerHealthy(worker.getUrl());
                }
                else{
                    loadBalancerService.markWorkerUnhealthy(worker.getUrl());
                }
            }
            catch(Exception e){
                loadBalancerService.markWorkerUnhealthy(worker.getUrl());
            }
        }
    }


}
