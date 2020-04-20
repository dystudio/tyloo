package io.tyloo.repository.helper;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.transaction.xa.Xid;

/*
 *
 * Redis������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 19:07 2019/5/6
 *
 */

public class RedisHelper {

    public static int SCAN_COUNT = 30;
    public static String SCAN_TEST_PATTERN = "*";
    public static String SCAN_INIT_CURSOR = "0";

    private static Logger logger = Logger.getLogger(RedisHelper.class);

    public static byte[] getRedisKey(String keyPrefix, Xid xid) {
        return new StringBuilder().append(keyPrefix).append(xid.toString()).toString().getBytes();
    }

    public static byte[] getRedisKey(String keyPrefix, String globalTransactionId, String branchQualifier) {

        return new StringBuilder().append(keyPrefix)
                .append(globalTransactionId).append(":")
                .append(branchQualifier).toString().getBytes();
    }

    /**
     * ��JedisPool���ÿ��Jedis����
     *
     * @param jedisPool
     * @param callback
     * @param <T>
     * @return
     */
    public static <T> T execute(JedisPool jedisPool, JedisCallback<T> callback) throws CloneNotSupportedException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return callback.doInJedis(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * SCAN cursor [MATCH pattern] [COUNT count]
     * $redis-cli scan 0 match key99* count 1000
     * 1) "13912"
     * 2)  1) "key997"
     * 2) "key9906"
     * 3) "key9957"
     * 4) "key9902"
     * 5) "key9971"
     * 6) "key9935"
     * 7) "key9958"
     * 8) "key9928"
     * 9) "key9931"
     * 10) "key9961"
     * 11) "key9948"
     * 12) "key9965"
     * 13) "key9937"
     * <p>
     * $redis-cli scan 13912 match key99* count 1000
     * 1) "5292"
     * 2)  1) "key996"
     * 2) "key9960"
     * 3) "key9973"
     * 4) "key9978"
     * 5) "key9927"
     * 6) "key995"
     * 7) "key9992"
     * 8) "key9993"
     * 9) "key9964"
     * 10) "key9934"
     * ���ؽ����Ϊ�������֣���һ���ּ� 1) ������һ�ε����α꣬�ڶ����ּ� 2) ���Ǳ��ε����������
     *
     * @param pattern
     * @param count
     * @return
     */
    public static ScanParams buildDefaultScanParams(String pattern, int count) {
        return new ScanParams().match(pattern).count(count);
    }

    /**
     * �Ƿ�֧��scan���Jedis��
     *
     * @param jedis
     * @return
     */
    public static Boolean isSupportScanCommand(Jedis jedis) {
        try {
            ScanParams scanParams = buildDefaultScanParams(SCAN_TEST_PATTERN, SCAN_COUNT);
            jedis.scan(SCAN_INIT_CURSOR, scanParams);
        } catch (JedisDataException e) {
            logger.error(e.getMessage(), e);
            logger.info("Redis **NOT** support scan command");
            return false;
        }

        logger.info("Redis support scan command");
        return true;
    }
}