package com.example.graph.utils;

import com.example.graph.nebula.domain.Entity;
import com.example.graph.nebula.domain.KeyValue;
import com.vesoft.nebula.client.graph.data.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
public class ConvertUtils {


    /**
     * 将nebula中Node类型转为自定义的Entity类型
     * @param node
     * @return
     */
    public static Entity nodeToEntity(Node node) throws Exception{
        Entity entity = new Entity();
        entity.setId(node.getId().asString());

        List<String> tags = node.tagNames();

        if(tags == null || tags.size() == 0){
            log.error("节点解析错误，tags列表为空！");
            throw new RuntimeException("节点解析错误，tags列表为空！");
        }

        if(tags.size()>1){
            log.error("节点解析错误，目前不支持多tag节点解析");
            throw new RuntimeException("节点解析错误，目前不支持多tag节点解析");
        }

        entity.setTag(tags.get(0));


        HashMap<String, ValueWrapper> properties = node.properties(tags.get(0));

        Map<String,Object> details = new HashMap<>();


        for(Map.Entry<String, ValueWrapper> entry : properties.entrySet()){

            ValueWrapper v = entry.getValue();

            if(v.isBoolean()){
                boolean var = v.asBoolean();
                details.put(entry.getKey(),var);
            }

            if(v.isDouble()){
                double var = v.asDouble();
                details.put(entry.getKey(),var);
            }

            if(v.isString()){
                String var = v.asString();
                details.put(entry.getKey(),var);
            }

            if(v.isDate()){
                DateWrapper var = v.asDate();
                details.put(entry.getKey(),var.toString());
            }

            if(v.isDateTime()){
                DateTimeWrapper var = v.asDateTime();
                details.put(entry.getKey(),var.getLocalDateTime());
            }

            if(v.isDuration()){
                DurationWrapper var = v.asDuration();
                details.put(entry.getKey(),var.toString());
            }

            if(v.isList()){
                ArrayList<ValueWrapper> var = v.asList();
                details.put(entry.getKey(),var);
            }

            if(v.isLong()){
                long var = v.asLong();
                details.put(entry.getKey(),var);
            }

            if(v.isMap()){
                HashMap<String, ValueWrapper> var = v.asMap();
                details.put(entry.getKey(),var);
            }

            if(v.isSet()){
                HashSet<ValueWrapper> var = v.asSet();
                details.put(entry.getKey(),var);
            }

            if(v.isTime()){
                TimeWrapper var = v.asTime();
                details.put(entry.getKey(),var.getLocalTime());
            }


            if(v.isGeography()){
                //TODO
            }
        }

        entity.setDetails(details);

        return entity;
    }


}
