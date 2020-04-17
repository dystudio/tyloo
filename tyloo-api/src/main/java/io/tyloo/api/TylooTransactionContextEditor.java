package io.tyloo.api;

import java.lang.reflect.Method;

/*
 *
 * ���������ı༭�����������úͻ������������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 11:35 2019/4/7
 *
 */

public interface TylooTransactionContextEditor {
    /**
     * �Ӳ����л������������
     *
     * @param target ����
     * @param method ����
     * @param args   ����
     * @return ����������
     */

    TylooTransactionContext get(Object target, Method method, Object[] args);
    /**
     * �������������ĵ�������
     *
     * @param tylooTransactionContext ����������
     * @param target             ����
     * @param method             ����
     * @param args               ����
     */
    void set(TylooTransactionContext tylooTransactionContext, Object target, Method method, Object[] args);

}
