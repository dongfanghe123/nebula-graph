package com.example.graph.nebula.helper;

import com.example.graph.nebula.domain.NebulaTagFieldMeta;
import com.example.graph.nebula.domain.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TagHelper {
    /** 生成 CREATE TAG 语句 */
    public static String buildCreateTag(Tag tag) {
        validate(tag);

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TAG IF NOT EXISTS ")
                .append(tag.getTagName())
                .append(" (");

        List<String> fieldDefs = new ArrayList<>();
        for (NebulaTagFieldMeta f : tag.getFields()) {
            fieldDefs.add(buildFieldDef(f));
        }
        sb.append(String.join(", ", fieldDefs));
        sb.append(")");

        if (isNotBlank(tag.getTagComment())) {
            sb.append(" COMMENT = ").append(quoteString(tag.getTagComment()));
        }

        sb.append(";");
        return sb.toString();
    }

    /** 单个字段定义 */
    private static String buildFieldDef(NebulaTagFieldMeta f) {
        StringBuilder sb = new StringBuilder();
        sb.append(f.getField())
                .append(" ")
                .append(f.getType().trim());

        // nullable: true -> NULL, false/null -> NOT NULL
        if (Boolean.TRUE.equals(f.getNullable())) {
            sb.append(" NULL");
        } else {
            sb.append(" NOT NULL");
        }

        // 默认值
        if (isNotBlank(f.getDefaultValue())) {
            sb.append(" DEFAULT ").append(formatDefaultValue(f));
        }

        // 字段注释
        if (isNotBlank(f.getComment())) {
            sb.append(" COMMENT ").append("\'"+f.getComment()+"\'");
        }

        return sb.toString();
    }

    /**
     * 默认值格式化：
     *  - 数字 / 布尔：裸值
     *  - 已是表达式（如 now()、uuid()）：裸值
     *  - 其他：按字符串加引号
     */
    private static String formatDefaultValue(NebulaTagFieldMeta f) {
        String v = f.getDefaultValue().trim();
        String type = f.getType().toUpperCase();

        // 表达式：以 () 结尾，常见于 now() / uuid() / timestamp()
        if (v.endsWith("()")) {
            return v;
        }

        // 数字类型
        if (type.contains("INT") || type.equals("DOUBLE") || type.equals("FLOAT")) {
            return v; // 交给 Nebula 校验，非法会在执行时报错
        }

        // 布尔
        if (type.equals("BOOL") || type.equals("BOOLEAN")) {
            return v.toLowerCase();
        }

        // 其余按字符串处理
        return quoteString(v);
    }


    /** 单引号字符串，内部单引号和反斜杠转义 */
    private static String quoteString(String s) {
        String escaped = s.replace("\\", "\\\\").replace("'", "\\'");
        return "'" + escaped + "'";
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    // ---------- 校验 ----------

    private static void validate(Tag tag) {
        Objects.requireNonNull(tag, "tag 不能为空");
        if (!isNotBlank(tag.getTagName())) {
            throw new IllegalArgumentException("tagName 不能为空");
        }
        if (!isValidIdentifier(tag.getTagName())) {
            throw new IllegalArgumentException("非法 tagName: " + tag.getTagName());
        }
        if (tag.getFields() == null) {
            tag.setFields(new ArrayList<>()); // 或抛异常，看你的策略
        }
        List<String> seen = new ArrayList<>();
        for (NebulaTagFieldMeta f : tag.getFields()) {
            if (!isNotBlank(f.getField())) {
                throw new IllegalArgumentException("字段名不能为空");
            }
            if (!isValidIdentifier(f.getField())) {
                throw new IllegalArgumentException("非法字段名: " + f.getField());
            }
            if (seen.contains(f.getField())) {
                throw new IllegalArgumentException("字段名重复: " + f.getField());
            }
            seen.add(f.getField());

            if (!isNotBlank(f.getType())) {
                throw new IllegalArgumentException("字段 " + f.getField() + " 缺少 type");
            }
        }
    }

    /** Nebula 标识符：字母/下划线开头，字母数字下划线组成（简化版） */
    private static boolean isValidIdentifier(String s) {
        return s.matches("^[A-Za-z_][A-Za-z0-9_]*$");
    }
}
