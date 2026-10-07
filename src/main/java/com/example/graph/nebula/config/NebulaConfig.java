package com.example.graph.nebula.config;

import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class NebulaConfig {

    @Autowired
    private NebulaProperties nebulaProperties;

    @Bean
    public SessionPool sessionPool() {
        // 1. 将配置的字符串地址转换为 HostAddress 列表
        List<HostAddress> addresses = nebulaProperties.getAddresses().stream()
                .map(addr -> {
                    String[] parts = addr.split(":");
                    try {
                        return new HostAddress(parts[0], Integer.parseInt(parts[1]));
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid Nebula address: " + addr, e);
                    }
                })
                .collect(Collectors.toList());

        // 2. 构建 SessionPoolConfig
        SessionPoolConfig poolConfig = new SessionPoolConfig(
                addresses,
                nebulaProperties.getSpace(),
                nebulaProperties.getUsername(),
                nebulaProperties.getPassword()
        );

        // 3. 设置可选的连接池参数
        NebulaProperties.PoolConfig pool = nebulaProperties.getPool();
        poolConfig.setMaxSessionSize(pool.getMaxSessionSize());
        poolConfig.setCleanTime(pool.getCleanTime());          // 修正方法名
        poolConfig.setIntervalTime(pool.getIntervalTime());    // 方法名正确，但参数类型需一致
        poolConfig.setRetryTimes(pool.getRetryTimes());

        // 4. 初始化 SessionPool
        SessionPool sessionPool = new SessionPool(poolConfig);
        if (!sessionPool.init()) {
            throw new RuntimeException("Nebula SessionPool 初始化失败！");
        }
        return sessionPool;
    }
}
