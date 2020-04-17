package io.tyloo.dubbo.proxy.jdk;

import io.tyloo.api.Tyloo;
import io.tyloo.api.Propagation;
import io.tyloo.dubbo.context.DubboTylooTransactionContextEditor;
import io.tyloo.interceptor.TylooCoordinatorAspect;
import io.tyloo.support.FactoryBuilder;
import io.tyloo.utils.ReflectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.proxy.InvokerInvocationHandler;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/*
 *
 * TCC ���ô�����
 * �ڵ��� Dubbo Service ����ʱ��ʹ�� TylooCoordinatorAspect ���ش���
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 10:55 2019/9/23
 *
 */

public class TccInvokerInvocationHandler extends InvokerInvocationHandler {

    private Object target;

    public TccInvokerInvocationHandler(Invoker<?> handler) {
        super(handler);
    }

    public <T> TccInvokerInvocationHandler(T target, Invoker<T> invoker) {
        super(invoker);
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Tyloo tyloo = method.getAnnotation(Tyloo.class);

        if (tyloo != null) {

            if (StringUtils.isEmpty(tyloo.confirmMethod())) {
                ReflectionUtils.changeAnnotationValue(tyloo, "confirmMethod", method.getName());
                ReflectionUtils.changeAnnotationValue(tyloo, "cancelMethod", method.getName());
                ReflectionUtils.changeAnnotationValue(tyloo, "transactionContextEditor", DubboTylooTransactionContextEditor.class);
                ReflectionUtils.changeAnnotationValue(tyloo, "propagation", Propagation.SUPPORTS);
            }

            /**
             * ���ɷ�������
             * ���� TylooCoordinatorAspect#interceptTransactionContextMethod �������Է����������ش���
             * Ϊʲô������� TylooTransactionAspect ���棿
             * ��Ϊ��������Ϊ Propagation.SUPPORTS�����ᷢ������
             */
            ProceedingJoinPoint pjp = new MethodProceedingJoinPoint(proxy, target, method, args);
            return FactoryBuilder.factoryOf(TylooCoordinatorAspect.class).getInstance().interceptTransactionContextMethod(pjp);
        } else {
            return super.invoke(target, method, args);
        }
    }


}
