package io.tyloo.interceptor;

import io.tyloo.context.MethodContext;
import io.tyloo.common.Subordinate;
import io.tyloo.common.Transaction;
import io.tyloo.common.TransactionManager;
import io.tyloo.api.Tyloo;
import io.tyloo.api.TylooTransactionContext;
import io.tyloo.api.TylooTransactionStatus;
import io.tyloo.api.TylooTransactionXid;
import io.tyloo.support.FactoryBuilder;
import io.tyloo.utils.ReflectionUtils;
import io.tyloo.utils.TylooMethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/*
 * 资源协调拦截器
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 15:35 2019/4/23
 *
 */


public class TylooCoordinatorInterceptor {

    private TransactionManager transactionManager;


    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {

        Transaction transaction = transactionManager.getCurrentTransaction();

        if (transaction != null) {

            switch (transaction.getStatus()) {
                case TRYING:
                    addSubordinate(pjp);
                    break;
                case CONFIRMING:
                    break;
                case CANCELLING:
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + transaction.getStatus());
            }
        }

        return pjp.proceed(pjp.getArgs());
    }

    /**
     * 添加事务参与者，在事务处于 Try 阶段被调用
     *
     * @param pjp
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void addSubordinate(ProceedingJoinPoint pjp) throws IllegalAccessException, InstantiationException, CloneNotSupportedException {

        Method method = TylooMethodUtils.getTylooMethod(pjp);
        if (method == null) {
            throw new RuntimeException(String.format("join point not found method, point is : %s", pjp.getSignature().getName()));
        }
        Tyloo tyloo = method.getAnnotation(Tyloo.class);

        String confirmMethodName = tyloo.confirmMethod();
        String cancelMethodName = tyloo.cancelMethod();

        Transaction transaction = transactionManager.getCurrentTransaction();
        TylooTransactionXid xid = new TylooTransactionXid(transaction.getXid().getGlobalTransactionId());

        if (FactoryBuilder.factoryOf(tyloo.transactionContextEditor()).getInstance().get(pjp.getTarget(), method, pjp.getArgs()) == null) {
            FactoryBuilder.factoryOf(tyloo.transactionContextEditor()).getInstance().set(new TylooTransactionContext(xid, TylooTransactionStatus.TRYING.getId()), pjp.getTarget(), ((MethodSignature) pjp.getSignature()).getMethod(), pjp.getArgs());
        }

        Class targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(), method.getName(), method.getParameterTypes());

        MethodContext confirmInvocation = new MethodContext(targetClass,
                confirmMethodName,
                method.getParameterTypes(), pjp.getArgs());

        MethodContext cancelInvocation = new MethodContext(targetClass,
                cancelMethodName,
                method.getParameterTypes(), pjp.getArgs());

        Subordinate Subordinate =
                new Subordinate(
                        xid,
                        confirmInvocation,
                        cancelInvocation,
                        tyloo.transactionContextEditor());

        transactionManager.addSubordinate(Subordinate);

    }


}
