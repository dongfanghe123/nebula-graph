package com.example.graph.nebula.domain;

import lombok.Data;

import java.util.List;

@Data
public class Tag {
    private String tagName;

    private List<NebulaTagFieldMeta> fields;

    //tag本身的注释
    private String tagComment;

}
