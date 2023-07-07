package MicroRpc.framework.redis.Registry.utils;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;
import java.util.List;

@Slf4j(topic = "m.RedisUtils")
public class RedisUtils {
    /**
     * 通过scan模糊删除
     * @param pattern
     * @return
     */
    public static Long delScan(Jedis jedis,String pattern) {
        long count=0;
        pattern+="*";
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams scanParams = new ScanParams();
        scanParams.match(pattern);
        ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
        List<String> result = scanResult.getResult();
        if (null!=result&&result.size()!=0){
            for (String key : result) {
                count+=jedis.del(key);
                log.info(count+": 删除key: {} ",key);
            }
        }
        return count;
    }

}
