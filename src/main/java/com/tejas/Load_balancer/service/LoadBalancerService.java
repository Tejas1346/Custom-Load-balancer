package com.tejas.Load_balancer.service;

import com.tejas.Load_balancer.model.WorkerNode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadBalancerService {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final HashMap<String, WorkerNode> workers = new HashMap<String, WorkerNode>();
    public void addWorkerNode(String url) {

        WorkerNode worker = new WorkerNode(url, true);
        workers.put(url, worker);
    }



    public WorkerNode getNextWorkerNode() {
        if (workers.isEmpty()) {
            return null;
        }
        List<WorkerNode> healthyWorkers = workers.values().stream()
                .filter(WorkerNode::isHealthy)
                .toList();
        if (healthyWorkers.isEmpty()) {
            return null;
        }
        int index = counter.getAndIncrement() % healthyWorkers.size();
        return healthyWorkers.get(index);
    }

    public void markWorkerHealthy(String url) {
        WorkerNode workerNode = workers.get(url);
        if (workerNode != null) {
            workerNode.setHealthy(true);
        }
    }

    public void markWorkerUnhealthy(String url){
        WorkerNode workerNode = workers.get(url);
        if(workerNode !=null){
            workerNode.setHealthy(false);
        }
    }

    public List<WorkerNode> getAllWorkers() {
        return workers.values().stream().toList();
    }
}
