package com.example.graph.nebula.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "nebula")
public class NebulaProperties {

    private List<String> addresses;
    private String space;
    private String username;
    private String password;
    private PoolConfig pool = new PoolConfig();

    // getter 和 setter 方法
    public List<String> getAddresses() { return addresses; }
    public void setAddresses(List<String> addresses) { this.addresses = addresses; }

    public String getSpace() { return space; }
    public void setSpace(String space) { this.space = space; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public PoolConfig getPool() { return pool; }
    public void setPool(PoolConfig pool) { this.pool = pool; }

    // 内部类，用于承载连接池相关配置
    public static class PoolConfig {
        // 最大 Session 数
        private int maxSessionSize = 10;
        // 空闲清理时间（毫秒），对应 setCleanTime
        private int cleanTime = 0;
        // 健康检查间隔（毫秒），对应 setIntervalTime
        private int intervalTime = 1000;
        // 重试次数
        private int retryTimes = 3;

        // 对应的 getter/setter
        public int getMaxSessionSize() { return maxSessionSize; }
        public void setMaxSessionSize(int maxSessionSize) { this.maxSessionSize = maxSessionSize; }

        public int getCleanTime() { return cleanTime; }
        public void setCleanTime(int cleanTime) { this.cleanTime = cleanTime; }

        public int getIntervalTime() { return intervalTime; }
        public void setIntervalTime(int intervalTime) { this.intervalTime = intervalTime; }

        public int getRetryTimes() { return retryTimes; }
        public void setRetryTimes(int retryTimes) { this.retryTimes = retryTimes; }
    }
}