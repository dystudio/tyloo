package io.tyloo.interceptor;

import io.tyloo.api.Tyloo;
import io.tyloo.api.Propagation;
import io.tyloo.api.TylooTransactionContext;
import io.tyloo.api.UniqueIdentity;
import io.tyloo.common.MethodRole;
import io.tyloo.support.FactoryBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/*
 * ע�ⷽ��������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 15:33 2019/4/24
 *
 */
public class TylooMethodContext {

    /**
     * �����
     */
    private ProceedingJoinPoint pjp = null;

    /**
     * ע�ⷽ��
     */
    private Method method = null;

    /**
     * ע��
     */
    private Tyloo tyloo = null;

    /**
     * ��������
     */
    private Propagation propagation = null;

    /**
     * ����������
     */
    private TylooTransactionContext tylooTransactionContext = null;

    TylooMethodContext(ProceedingJoinPoint pjp) {
        this.pjp = pjp;
        this.method = getTylooMethod();
        assert method != null;
        this.tyloo = method.getAnnotation(Tyloo.class);
        this.propagation = tyloo.propagation();
        this.tylooTransactionContext = FactoryBuilder.factoryOf(tyloo.transactionContextEditor()).getInstance().get(pjp.getTarget(), method, pjp.getArgs());

    }

    public Tyloo getAnnotation() {
        return tyloo;
    }

    public Propagation getPropagation() {
        return propagation;
    }

    public TylooTransactionContext getTylooTransactionContext() {
        return tylooTransactionContext;
    }

    public Method getMethod() {
        return method;
    }

    /**
     * ��ȡΨһ��ʶ
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
     * ��ȡע�ⷽ��
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
     * ͨ���÷������񴫲������ȡ��������
     *
     * @param isTransactionActive
     * @return
     */
    public MethodRole getMethodRole(boolean isTransactionActive) {
        if ((propagation.equals(Propagation.REQUIRED) && !isTransactionActive && tylooTransactionContext == null) ||
                propagation.equals(Propagation.REQUIRES_NEW)) {
            return MethodRole.ROOT;
        } else if ((propagation.equals(Propagation.REQUIRED) || propagation.equals(Propagation.MANDATORY)) && !isTransactionActive && tylooTransactionContext != null) {
            return MethodRole.PROVIDER;
        } else {
            return MethodRole.NORMAL;
        }
    }


    public Object proceed() throws Throwable {
        return this.pjp.proceed();
    }
}