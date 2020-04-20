package io.tyloo.common;

import io.tyloo.api.TylooTransactionContext;
import io.tyloo.api.TylooTransactionContextEditor;
import io.tyloo.context.MethodContext;
import io.tyloo.exception.SystemException;
import io.tyloo.support.FactoryBuilder;
import io.tyloo.utils.StringUtils;

import java.lang.reflect.Method;

/*
 *
 * �������ִ����
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 20:01 2019/6/6
 *
 */

public final class InvokeHandler {

    public InvokeHandler() {

    }

    /**
     * ���ݵ��������ģ���ȡĿ�귽����ִ�з�������.
     *
     * @param methodContext
     */
    public static void invoke(TylooTransactionContext tylooTransactionContext, MethodContext methodContext, Class<? extends TylooTransactionContextEditor> transactionContextEditorClass) {


        if (StringUtils.isNotEmpty(methodContext.getMethodName())) {

            try {
                Object target = FactoryBuilder.factoryOf(methodContext.getTargetClass()).getInstance();
                Method method = null;
                //ע������������
                method = target.getClass().getMethod(methodContext.getMethodName(), methodContext.getParameterTypes());
                FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(tylooTransactionContext, target, method, methodContext.getArgs());
                // ���÷��񷽷������ٴα�TylooAspect��TylooCoordinatorAspect���أ�����Ϊ����״̬�Ѿ�������TRYING�ˣ�����ֱ��ִ��Զ�̷���
                method.invoke(target, methodContext.getArgs());
            } catch (Exception e) {
                throw new SystemException(e);
            }
        }
    }
}
