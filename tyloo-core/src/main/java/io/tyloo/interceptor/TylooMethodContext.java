package io.tyloo.interceptor;

import io.tyloo.api.Tyloo;
import io.tyloo.api.Propagation;
import io.tyloo.api.TransactionContext;
import io.tyloo.api.UniqueIdentity;
import io.tyloo.common.MethodRole;
import io.tyloo.support.FactoryBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/*
 * 注解方法上下文
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 15:33 2019/4/24
 *
 */
public class TylooMethodContext {

    /**
     * 切入点
     */
    private ProceedingJoinPoint pjp = null;

    /**
     * 注解方法
     */
    private Method method = null;

    /**
     * 注解
     */
    private Tyloo tyloo = null;

    /**
     * 传播级别
     */
    private Propagation propagation = null;

    /**
     * 事务上下文
     */
    private TransactionContext transactionContext = null;

    TylooMethodContext(ProceedingJoinPoint pjp) {
        this.pjp = pjp;
        this.method = getTylooMethod();
        assert method != null;
        this.tyloo = method.getAnnotation(Tyloo.class);
        this.propagation = tyloo.propagation();
        this.transactionContext = FactoryBuilder.factoryOf(tyloo.transactionContextEditor()).getInstance().get(pjp.getTarget(), method, pjp.getArgs());

    }

    public Tyloo getAnnotation() {
        return tyloo;
    }

    public Propagation getPropagation() {
        return propagation;
    }

    public TransactionContext getTransactionContext() {
        return transactionContext;
    }

    public Method getMethod() {
        return method;
    }

    /**
     * 获取唯一标识
     *
     * @return
     */
    public Object getUniqueIdentity() {
        Annotation[][] annotations = this.getMethod().getParameterAnnotations();

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation.annotationType().equals(UniqueIdentity.class)) {

                    Object[] params = pjp.getArgs();

                    return params[i];
                }
            }
        }

        return null;
    }

    /**
     * 获取注解方法
     *
     * @return
     */
    private Method getTylooMethod() {
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

        if (method.getAnnotation(Tyloo.class) == null) {
            try {
                method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return method;
    }

     /**
     * 通过该方法事务传播级别获取方法类型
     *
     * @param isTransactionActive
     * @return
     */
    public MethodRole getMethodRole(boolean isTransactionActive) {
        if ((propagation.equals(Propagation.REQUIRED) && !isTransactionActive && transactionContext == null) ||
                propagation.equals(Propagation.REQUIRES_NEW)) {
            return MethodRole.ROOT;
        } else if ((propagation.equals(Propagation.REQUIRED) || propagation.equals(Propagation.MANDATORY)) && !isTransactionActive && transactionContext != null) {
            return MethodRole.PROVIDER;
        } else {
            return MethodRole.NORMAL;
        }
    }


    public Object proceed() throws Throwable {
        return this.pjp.proceed();
    }
}