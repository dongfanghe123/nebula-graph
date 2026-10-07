package com.example.graph.nebula.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edge {

    private String startNodeId;

    private String endNodeId;

    private String type;

    private Long rank = 0L;

    private Map<String,Object> properties;

}
