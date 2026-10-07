package com.example.graph.nebula.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EdgeType {
    /** 边类型名称（底层NGQL使用，不可修改） */
    private String edgeName;
    /** 边类型整体注释 */
    private String edgeComment;
    /** 属性列表 */
    private List<NebulaEdgeFieldMeta> fields;
}
