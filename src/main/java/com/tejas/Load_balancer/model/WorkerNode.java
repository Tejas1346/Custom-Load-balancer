package com.tejas.Load_balancer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkerNode {
    private String url;
    private boolean healthy;

}
