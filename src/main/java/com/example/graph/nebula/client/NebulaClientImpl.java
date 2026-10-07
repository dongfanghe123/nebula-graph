package com.example.graph.nebula.client;

import com.example.graph.common.Result;
import com.example.graph.nebula.domain.Entity;
import com.example.graph.nebula.domain.NebulaTagFieldMeta;
import com.example.graph.nebula.domain.Tag;
import com.example.graph.nebula.helper.TagHelper;
import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.Session;
import com.vesoft.nebula.client.graph.net.SessionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NebulaClientImpl implements NebulaClient{

    @Autowired
    private SessionPool sessionPool;


    @Override
    public ResultSet execute(String ngql) {
        return execute(ngql,null);
    }

    @Override
    public ResultSet execute( String ngql,String space) {
        return execute(ngql,space, null);
    }

    /**
     * 核心执行方法：支持指定图空间和参数
     */
    public ResultSet execute(String ngql,String space, Map<String, Object> params) {
        // 1. 参数校验
        validateParams( ngql);

        // 2. 处理图空间切换
        String finalNgql = buildFinalNgql(ngql,space);

        // 3. 执行
        log.debug("执行 nGQL [空间: {}, 语句: {}]", space, finalNgql);
        long startTime = System.currentTimeMillis();

        try {
            ResultSet resultSet;
            if (params != null && !params.isEmpty()) {
                resultSet = sessionPool.execute(finalNgql, params);
            } else {
                resultSet = sessionPool.execute(finalNgql);
            }

            // 4. 结果检查
            checkResult(resultSet, space, finalNgql);

            // 5. 日志记录
            logExecution(startTime, resultSet);

            return resultSet;

        } catch (Exception e) {
            log.error("执行 nGQL 失败 [空间: {}, 语句: {}]", space, finalNgql, e);
            throw new RuntimeException("执行 nGQL 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建最终执行的 nGQL 语句（处理 USE 切换）
     */
    private String buildFinalNgql(String ngql,String space) {
        // 如果指定的空间与默认空间相同，直接执行
        if (space == null) {
            return normalizeNgql(ngql);
        }


        // 否则，添加 USE 语句
        String normalizedNgql = normalizeNgql(ngql);
        return String.format("USE %s; %s", space, normalizedNgql);
    }

    /**
     * 规范化 nGQL 语句（确保以分号结尾）
     */
    private String normalizeNgql(String ngql) {
        String trimmed = ngql.trim();
        if (!trimmed.endsWith(";")) {
            trimmed = trimmed + ";";
        }
        return trimmed;
    }

    /**
     * 参数校验
     */
    private void validateParams( String ngql) {
        if (ngql == null || ngql.trim().isEmpty()) {
            throw new IllegalArgumentException("nGQL 语句不能为空");
        }
    }

    /**
     * 检查执行结果
     */
    private void checkResult(ResultSet resultSet, String ngql, String space) {
        if (!resultSet.isSucceeded()) {
            String errorMsg = String.format(
                    "nGQL 执行失败 [空间: %s, 语句: %s, 错误: %s]",
                    space, ngql, resultSet.getErrorMessage()
            );
            throw new RuntimeException(errorMsg);
        }
    }

    /**
     * 记录执行日志
     */
    private void logExecution(long startTime, ResultSet resultSet) {
        long cost = System.currentTimeMillis() - startTime;
        log.info("nGQL 执行成功 [耗时: {}ms, 行数: {}]", cost, resultSet.rowsSize());
    }


    @Override
    public Result saveNode(Entity entity) {
        return null;
    }

    private String buildInsertGql(Entity entity){
        if (entity == null) {
            throw new IllegalArgumentException("Entity对象不能为空");
        }
        if (entity.getTag() == null || entity.getTag().trim().isEmpty()) {
            throw new IllegalArgumentException("节点Tag(clsName)不能为空");
        }
        if (entity.getId() == null || entity.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("节点ID(id)不能为空");
        }

        StringBuilder sb = new StringBuilder("insert verex ");

        return null;

    }

    @Override
    public List<NebulaTagFieldMeta> showTag(String tag)  {

        String nGql = String.format("desc tag %s",tag);
        ResultSet resultSet = this.execute(nGql);

        if (!resultSet.isSucceeded()) {
            throw new RuntimeException("查询tag元信息失败：" + resultSet.getErrorMessage());
        }

        List<NebulaTagFieldMeta> propList = new ArrayList<>();
        List<String> columnNames = resultSet.getColumnNames();
        //结果集中行数
        int rowsSize = resultSet.rowsSize();

        for(int i=0;i<rowsSize;i++){
            ResultSet.Record record = resultSet.rowValues(i);
            NebulaTagFieldMeta nebulaTagFieldMeta = new NebulaTagFieldMeta();
            try{
                nebulaTagFieldMeta.setField(record.get("Field").asString());
                nebulaTagFieldMeta.setType(record.get("Type").asString());
                nebulaTagFieldMeta.setNullable(record.get("Null").equals("\"YES\"")?true:false);

                ValueWrapper aDefault = record.get("Default");
                if (!aDefault.isEmpty()) {
                    nebulaTagFieldMeta.setField(record.get("Field").asString());
                }
                nebulaTagFieldMeta.setComment(record.get("Comment").asString());
                propList.add(nebulaTagFieldMeta);
            }catch (Exception e){
                throw new RuntimeException("解析tag字段错误");
            }

        }

        return propList;
    }

    /**
     * CREATE TAG IF NOT EXISTS person (
     *     name string NOT NULL DEFAULT 'unknown' COMMENT '姓名',
     *     age int64 NULL DEFAULT 18 COMMENT '年龄',
     *     create_time timestamp NULL DEFAULT now() COMMENT '创建时间'
     * )  COMMENT '人概念';
     */
    @Override
    public Boolean createTag(Tag tag) {
        String ngql = TagHelper.buildCreateTag(tag);
        ResultSet resultSet = execute(ngql);
        return resultSet.isSucceeded();
    }
}
