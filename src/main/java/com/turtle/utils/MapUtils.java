/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.utils;


import com.turtle.config.SymbolConfig;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 6:04 PM
 */
public class MapUtils {

    private MapUtils() {
    }

    /**
     * <p>判断是否为空</p>
     *
     * @param map Map
     *
     * @return 是否为空
     */
    public static final boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * <p>判断是否非空</p>
     *
     * @param map Map
     *
     * @return 是否非空
     */
    public static final boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * <p>Map转为URL参数</p>
     *
     * @param map Map
     *
     * @return URL参数
     */
    public static final String toUrlQuery(Map<String, String> map) {
        if(MapUtils.isEmpty(map)) {
            return null;
        }
        return map.entrySet().stream()
                .map(entry -> SymbolConfig.Symbol.EQUALS.join(entry.getKey(), UrlUtils.encode(entry.getValue())))
                .collect(Collectors.joining(SymbolConfig.Symbol.AND.toString()));
    }
    /**
     * <p>获取Map</p>
     *
     * @param map 数据
     * @param key 键
     *
     * @return Map
     */
    public static final Map<String, Object> getMap(Map<?, ?> map, String key) {
        if(map == null) {
            return new HashMap<String, Object>();
        }
        final Map<String,Object> result = (Map<String, Object>) map.get(key);
        if(result == null) {
            return new HashMap<String, Object>();
        }
        // 使用LinkedHashMap防止乱序
        return result.entrySet().stream()
                .collect(Collectors.toMap(
                        entry ->entry.getKey()== null ? null : entry.getKey().toString(),
                        Map.Entry::getValue,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }
}
