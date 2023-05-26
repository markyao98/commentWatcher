package com.markyao.utils;

import java.util.*;

public class TreeMapExample {
    public static void main(String[] args) {
        TreeMap<String, Integer> map = new TreeMap<>();

        // 添加示例数据
        map.put("A", 10);
        map.put("B", 5);
        map.put("C", 8);
        map.put("D", 15);

        // 通过比较器按照值进行排序
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(map.entrySet());
        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        // 获取value最大的entry
        Map.Entry<String, Integer> maxEntry = sortedEntries.get(0);

        System.out.println("Key: " + maxEntry.getKey() + ", Value: " + maxEntry.getValue());
    }
}
