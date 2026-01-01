package com.tejas.Load_balancer.controller;

import com.tejas.Load_balancer.model.WorkerNode;
import com.tejas.Load_balancer.model.WorkerNodeRequest;
import com.tejas.Load_balancer.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class LoadBalancerController {
    private final LoadBalancerService loadBalancerService;
    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger(LoadBalancerController.class.getName());
    @RequestMapping(value = "/balance/**")
    public ResponseEntity<?> handleRequest(HttpServletRequest request,
                                           @RequestBody(required = false) String body) throws RestClientException {
        WorkerNode worker = loadBalancerService.getNextWorkerNode();
        if (worker == null) {  // Add this check!
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("No healthy workers available");
        }

        String originalPath = request.getRequestURI().replaceFirst("/balance", "");
        String targetUrl = worker.getUrl() + (originalPath.isEmpty() ? "" : originalPath);

        HttpMethod requestMethod =  HttpMethod.valueOf(request.getMethod());
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.add(headerName,request.getHeader(headerName));
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(body,headers);
        ResponseEntity<String> response;
        response = restTemplate.exchange(
                targetUrl,
                requestMethod,
                requestEntity,
                String.class
        );
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerServer(@RequestBody WorkerNodeRequest request) {
        loadBalancerService.addWorkerNode(request.url());
        return  ResponseEntity.status(HttpStatus.CREATED).body(request.url());
    }

    @GetMapping("/workers")
    public ResponseEntity<List<String>> getAllWorkers() {
        List<WorkerNode> workers =  loadBalancerService.getAllWorkers();
        List<String> urls = new ArrayList<>();
        for (WorkerNode worker : workers) {
            urls.add(worker.getUrl());
        }
        return ResponseEntity.ok(urls);
    }
}
