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
 * 反射调用执行器
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 20:01 2019/6/6
 *
 */

public final class InvokeHandler {

    public InvokeHandler() {

    }

    /**
     * 根据调用上下文，获取目标方法并执行方法调用.
     *
     * @param methodContext
     */
    public static void invoke(TylooTransactionContext tylooTransactionContext, MethodContext methodContext, Class<? extends TylooTransactionContextEditor> transactionContextEditorClass) {


        if (StringUtils.isNotEmpty(methodContext.getMethodName())) {

            try {
                Object target = FactoryBuilder.factoryOf(methodContext.getTargetClass()).getInstance();
                Method method = null;
                //注入事务上下文
                method = target.getClass().getMethod(methodContext.getMethodName(), methodContext.getParameterTypes());
                FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(tylooTransactionContext, target, method, methodContext.getArgs());
                // 调用服务方法，被再次被TylooAspect和TylooCoordinatorAspect拦截，但因为事务状态已经不再是TRYING了，所以直接执行远程服务
                method.invoke(target, methodContext.getArgs());
            } catch (Exception e) {
                throw new SystemException(e);
            }
        }
    }
}
