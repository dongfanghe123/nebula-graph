package com.example.graph.nebula.client;

import com.example.graph.common.Result;
import com.example.graph.nebula.domain.Entity;
import com.example.graph.nebula.domain.NebulaTagFieldMeta;
import com.example.graph.nebula.domain.Tag;
import com.vesoft.nebula.client.graph.data.ResultSet;

import java.util.List;

public interface NebulaClient {
    ResultSet execute(String ngql,String space);

    ResultSet execute(String ngql);

    Result saveNode(Entity entity);

    List<NebulaTagFieldMeta> showTag(String tag);

    Boolean createTag(Tag tag);

}
