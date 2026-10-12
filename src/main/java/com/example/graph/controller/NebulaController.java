package com.example.graph.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.graph.common.Result;
import com.example.graph.nebula.client.NebulaClient;
import com.example.graph.nebula.domain.Entity;
import com.example.graph.nebula.domain.KeyValue;
import com.example.graph.nebula.domain.NebulaTagFieldMeta;
import com.example.graph.nebula.domain.Tag;
import com.example.graph.utils.ConvertUtils;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.data.*;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/nebula")
@Slf4j
public class NebulaController {


    @Autowired
    private SessionPool sessionPool;

    @Autowired
    private NebulaClient nebulaClient;


    @RequestMapping("/saveNode")
    public Result saveNode(@Valid @RequestBody Entity entity) {
        return null;
    }


    @RequestMapping("/showTag")
    public Result showTag(@RequestParam("tag") String tag) {
        List<NebulaTagFieldMeta> nebulaTagFieldMetas = nebulaClient.getTagInfo(tag);
        return Result.success(nebulaTagFieldMetas);
    }

    @RequestMapping("/createTag")
    public Result showTag(@RequestBody Tag tag) {
        Boolean isSuccess = nebulaClient.createTag(tag);
        if (isSuccess) {
            return Result.success();
        } else {
            return Result.fail("创建tag失败");
        }
    }

    @RequestMapping("/common")
    public Result common(@RequestBody String string) throws Exception {

        JSONObject jsonObject = JSON.parseObject(string);

        String ngql = jsonObject.getString("ngql");
        ResultSet resultSet = sessionPool.execute(ngql);

        if (!resultSet.isSucceeded()) {
            log.error("execute ngql:[{}] error,error msg:[{}]", ngql, resultSet.getErrorMessage());
            return Result.fail(500, resultSet.getErrorMessage());
        }

        int rowsSize = resultSet.rowsSize();
        List<String> columnNames = resultSet.getColumnNames();

        Map<String,String> type = new HashMap<>();

        List<List<Object>> result = new ArrayList<>();

        for (int i = 0; i < rowsSize; i++) {
            ResultSet.Record record = resultSet.rowValues(i);

            List<Object> row = new ArrayList<>();

            List<ValueWrapper> values = record.values();

            for (int j = 0; j < values.size(); j++) {
                ValueWrapper v = values.get(j);

                if(v.isBoolean()){
                    boolean var = v.asBoolean();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Boolean");
                    }
                }

                if(v.isDouble()){
                    double var = v.asDouble();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Double");
                    }
                }

                if(v.isString()){
                    String var = v.asString();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"String");
                    }
                }

                if(v.isDate()){
                    DateWrapper var = v.asDate();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Date");
                    }
                }

                if(v.isDateTime()){
                    DateTimeWrapper var = v.asDateTime();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"DateTime");
                    }
                }

                if(v.isDuration()){
                    DurationWrapper var = v.asDuration();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Duration");
                    }
                }

                if(v.isList()){
                    ArrayList<ValueWrapper> var = v.asList();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"List");
                    }
                }

                if(v.isLong()){
                    long var = v.asLong();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Long");
                    }
                }

                if(v.isMap()){
                    HashMap<String, ValueWrapper> var = v.asMap();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Map");
                    }
                }

                if(v.isSet()){
                    HashSet<ValueWrapper> var = v.asSet();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Set");
                    }
                }

                if(v.isTime()){
                    TimeWrapper var = v.asTime();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Time");
                    }
                }

                if(v.isVertex()){
                    Node var = v.asNode();
                    Entity entity = ConvertUtils.nodeToEntity(var);
                    row.add(entity);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Vertex");
                    }
                }

                if(v.isEdge()){
                    Relationship var = v.asRelationship();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Edge");
                    }
                }

                if(v.isPath()){
                    PathWrapper var = v.asPath();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Path");
                    }
                }

                if(v.isNull()){
                    ValueWrapper.NullType var = v.asNull();
                    row.add(var);

                }

                if(v.isEmpty()){

                }

                if(v.isGeography()){
                    GeographyWrapper var = v.asGeography();
                    row.add(var);
                    if(!type.containsKey(columnNames.get(j))){
                        type.put(columnNames.get(j),"Geo");
                    }
                }
            }

            result.add(row);
        }

        return Result.success(result);
    }



    @RequestMapping("/execute")
    public Result execute(@RequestBody String string){
        JSONObject jsonObject = JSONObject.parseObject(string);
        String ngql = jsonObject.getString("ngql");
        ResultSet resultSet = nebulaClient.execute(ngql);
        return Result.success(resultSet);
    }

}
