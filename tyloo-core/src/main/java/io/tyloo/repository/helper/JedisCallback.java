package io.tyloo.repository.helper;

import redis.clients.jedis.Jedis;

/*
 * Jedis�ص��ӿ�
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 19:07 2019/5/4
 *
 */

public interface JedisCallback<T> {

    /**
     * ��doInJedis������Ϊ���ṩһ��δ��װ���� jedis ���󣬿���ʹ��ԭ���� jedis �ĸ��ַ���
     * @param jedis
     * @return
     */
    public T doInJedis(Jedis jedis) throws CloneNotSupportedException;
}