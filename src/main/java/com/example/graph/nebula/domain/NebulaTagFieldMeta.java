package com.example.graph.nebula.domain;

import lombok.Data;

@Data
public class NebulaTagFieldMeta {
    /** 字段名称 */
    private String field;
    /** 字段nebula类型 */
    private String type;
    /** 是否允许null值 YES / NO */
    private Boolean nullable;
    /** 默认值 */
    private String defaultValue;
    /** 注释 */
    private String comment;
}
