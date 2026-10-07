package com.example.graph.nebula.domain;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Data
public class Entity {

    //节点唯一标识
    @NotEmpty(message = "节点id不能为空")
    private String id;

    //节点对应的tag
    @NotEmpty(message = "tag不允许为空")
    private String tag;

    //节点名称，便于前端展示
    @NotEmpty(message = "节点名称不允许为空")
    private String name;

    //节点属性值
    private Map<String,Object> details;

}
