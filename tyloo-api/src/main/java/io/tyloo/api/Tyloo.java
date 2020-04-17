package io.tyloo.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/*
 *
 * ����ע��
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 11:32 2019/4/1
 *
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Tyloo {

    /**
     * ��������
     */
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * ȷ��ִ��ҵ�񷽷�
     */
    String confirmMethod() default "";

    /**
     * ȡ��ִ��ҵ�񷽷�
     */
    String cancelMethod() default "";

    /**
     * ���������ı༭
     */
    public Class<? extends TylooTransactionContextEditor> transactionContextEditor() default DefaultTylooTransactionContextEditor.class;

    /**
     * ��ʱ�쳣
     * delayCancelExceptions()��ʾϵͳ���������õ��쳣ʱ������������rollbac�������ɻָ�job��ִ������ָ���k
     * ͨ����Ҫ����ʱ�쳣����ΪdelayCancelExceptions���������Ա�����Ϊ�������ʱ�����˳�ʱ�쳣���������������rollback, ���Ǵ�����ûִ���꣬�Ӷ����������rollbackʧ��
     *
     * @return
     */
    public Class<? extends Exception>[] delayCancelExceptions() default {};

    public boolean asyncConfirm() default false;

    public boolean asyncCancel() default false;

    /**
     * Ĭ�����������ı༭��ʵ��
     */
    class DefaultTylooTransactionContextEditor implements TylooTransactionContextEditor {

        @Override
        public TylooTransactionContext get(Object target, Method method, Object[] args) {
            int position = getTransactionContextParamPosition(method.getParameterTypes());

            if (position >= 0) {
                return (TylooTransactionContext) args[position];
            }

            return null;
        }

        @Override
        public void set(TylooTransactionContext tylooTransactionContext, Object target, Method method, Object[] args) {

            int position = getTransactionContextParamPosition(method.getParameterTypes());
            if (position >= 0) {
                args[position] = tylooTransactionContext;
            }
        }

        /**
         * ��������������ڷ����������λ��
         *
         * @param parameterTypes �������ͼ���
         * @return λ��
         */
        static int getTransactionContextParamPosition(Class<?>[] parameterTypes) {

            int position = -1;

            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i].equals(TylooTransactionContext.class)) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        /**
         * @param args �����б�
         * @return ��ȡTransactionContext����
         */
        public static TylooTransactionContext getTransactionContextFromArgs(Object[] args) {

            TylooTransactionContext tylooTransactionContext = null;

            for (Object arg : args) {
                if (arg != null && TylooTransactionContext.class.isAssignableFrom(arg.getClass())) {

                    tylooTransactionContext = (TylooTransactionContext) arg;
                }
            }

            return tylooTransactionContext;
        }
    }
}