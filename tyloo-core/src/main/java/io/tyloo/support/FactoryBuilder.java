package io.tyloo.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/*
 *
 * Bean����������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 19:35 2019/5/27
 *
 */
public final class FactoryBuilder {


    private FactoryBuilder() {

    }

    /**
     * Bean ��������
     */
    private static final List<BeanFactory> beanFactories = new ArrayList<BeanFactory>();

    /**
     * ����Bean���� ��ӳ��
     */
    private static final ConcurrentHashMap<Class, SingeltonFactory> classFactoryMap = new ConcurrentHashMap<Class, SingeltonFactory>();

    /**
     * ���ָ���൥������
     *
     * @param <T>   ����
     * @param clazz ָ����
     * @return ��������
     */
    public static <T> SingeltonFactory<T> factoryOf(Class<T> clazz) {

        if (!classFactoryMap.containsKey(clazz)) {
            for (BeanFactory beanFactory : beanFactories) {
                if (beanFactory.isFactoryOf(clazz)) {
                    classFactoryMap.putIfAbsent(clazz, new SingeltonFactory<T>(clazz, beanFactory.getBean(clazz)));
                }
            }

            if (!classFactoryMap.containsKey(clazz)) {
                classFactoryMap.putIfAbsent(clazz, new SingeltonFactory<T>(clazz));
            }
        }

        return classFactoryMap.get(clazz);
    }

    /**
     * ��Bean����ע�ᵽ��ǰBuilder
     *
     * @param beanFactory Bean����
     */
    public static void registerBeanFactory(BeanFactory beanFactory) {
        beanFactories.add(beanFactory);
    }

    /**
     * ��������
     *
     * @param <T> ����
     */
    public static class SingeltonFactory<T> {

        private volatile T instance = null;

        private String className;

        public SingeltonFactory(Class<T> clazz, T instance) {
            this.className = clazz.getName();
            this.instance = instance;
        }

        public SingeltonFactory(Class<T> clazz) {
            this.className = clazz.getName();
        }

        /**
         * ��õ���
         *
         * @return ����
         */
        public T getInstance() {
            if (instance == null) {
                synchronized (SingeltonFactory.class) {
                    if (instance == null) {
                        try {
                            ClassLoader loader = Thread.currentThread().getContextClassLoader();
                            Class<?> clazz = loader.loadClass(className);
                            instance = (T) clazz.newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to create an instance of " + className, e);
                        }
                    }
                }
            }

            return instance;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }

            SingeltonFactory that = (SingeltonFactory) other;

            if (!className.equals(that.className)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return className.hashCode();
        }
    }
}