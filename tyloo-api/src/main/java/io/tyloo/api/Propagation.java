package io.tyloo.api;

/*
 *
 * ��������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 11:49 2019/4/4
 *
 */

public enum Propagation {
    /**
     * ֧�ֵ�ǰ���������ǰû�����񣬾��½�һ������
     */
    REQUIRED(0),
    /**
     * ֧�ֵ�ǰ���������ǰû�����񣬾��Է�����ʽִ��
     */
    SUPPORTS(1),
    /**
     * ֧�ֵ�ǰ���������ǰû�����񣬾��׳��쳣
     */
    MANDATORY(2),
    /**
     * �½����������ǰ�������񣬰ѵ�ǰ�������
     */
    REQUIRES_NEW(3);

    private final int value;

    Propagation(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}