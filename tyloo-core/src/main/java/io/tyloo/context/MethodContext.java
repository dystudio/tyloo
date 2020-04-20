package io.tyloo.context;

import java.io.Serializable;

/*
 *
 *  ִ�з�������������
 *  ��¼�ࡢ�������������������顢�������顣
 *  ͨ����Щ���ԣ�����ִ���ύ / �ع�����
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 15:48 2019/6/3
 *
 */
public class MethodContext implements Serializable {

    private static final long serialVersionUID = -7969140711432461165L;

    /**
     * ��
     */
    private Class targetClass;

    /**
     * ������
     */
    private String methodName;

    /**
     * ������������
     */
    private Class[] parameterTypes;

    /**
     * ��������
     */
    private Object[] args;

    public MethodContext() {

    }

    public MethodContext(Class targetClass, String methodName, Class[] parameterTypes, Object... args) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.targetClass = targetClass;
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }
}
