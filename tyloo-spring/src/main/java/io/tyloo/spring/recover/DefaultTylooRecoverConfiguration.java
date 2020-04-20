package io.tyloo.spring.recover;

import io.tyloo.exception.OptimisticLockException;
import io.tyloo.recover.TylooRecoverConfiguration;

import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

/*
 *
 * Ĭ������ָ�����
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 20:14 2019/10/10
 *
 */

public class DefaultTylooRecoverConfiguration implements TylooRecoverConfiguration {

    public static final TylooRecoverConfiguration INSTANCE = new DefaultTylooRecoverConfiguration();

    /**
     * һ��������ೢ�Իָ������������������Զ��ָ�����Ҫ�˹���Ԥ��Ĭ����30�Σ�
     */
    private int maxRetryCount = 30;

    /**
     * һ��������־������һ��ʱ������û�и��¾ͻᱻ��Ϊ�Ƿ������쳣����Ҫ�ָ���
     * �ָ�Job��ɨ�賬�����ʱ��������û�и��µ�������־��������Щ������лָ���ʱ�䵥λ���룬Ĭ����120��
     */
    private int recoverDuration = 120; //120 seconds

    /**
     * �ָ�Job����������ã�Ĭ����(ÿ����)
     * cron ���ʽ
     * 0/30 * * * * ?��ÿ 30 ��ִ��һ�Ρ�
     */
    private String cronExpression = "0 */1 * * * ?";

    private int asyncTerminateThreadCorePoolSize = 512;

    private int asyncTerminateThreadMaxPoolSize = 1024;

    private int asyncTerminateThreadWorkQueueSize = 512;

    /**
     * �ӳ�ȡ���쳣����
     */
    private Set<Class<? extends Exception>> delayCancelExceptions = new HashSet<Class<? extends Exception>>();

    public DefaultTylooRecoverConfiguration() {

        /**
         * ���� SocketTimeoutException �����������ָ����ʱ��С�� Socket ��ʱʱ�䣬��ʱ����ָ�����Զ�̲�����ȡ���ع�����
         * Զ�̲������´θ�������ʱ������Ϊ�ֹ�������ʧ�ܣ��׳� OptimisticLockException����� TylooInterceptor ��ʱ����ȡ���ع���
         * ���ܻ�Ͷ�ʱ�����ȡ���ع���ͻ�����ͳһ������ʱ������
         *
         */
        delayCancelExceptions.add(OptimisticLockException.class);
        /**
         * try �׶Σ����ز����ߵ���Զ�̲�����( Զ�̷������� Dubbo��Http ����)��Զ�̲����� try �׶εķ����߼�ִ��ʱ��ϳ������� Socket �ȴ�ʱ�������� SocketTimeoutException��
         * �������ִ������ع���Զ�̲����� try �ķ���δִ����ɣ����ܵ��� cancel �ķ���ʵ��δִ��( try �ķ���δִ����ɣ����ݿ����񡾷� TCC ����δ�ύ��
         * cancel �ķ�����ȡ����ʱ����δ��������·���ʵ��δִ�У����� try �ķ���ִ������ύ���ݿ����񡾷� TCC ���񡿣���Ϊ���� )�������������ݲ�һ�¡�
         * ������ָ�ʱ���������������������ȡ���ع��������ʱԶ�̲����ߵ� try �ķ�����δ���������ǿ��ܷ������ݲ�һ�¡�
         *
         */
        delayCancelExceptions.add(SocketTimeoutException.class);
    }

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    @Override
    public int getRecoverDuration() {
        return recoverDuration;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }


    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void setRecoverDuration(int recoverDuration) {
        this.recoverDuration = recoverDuration;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setAsyncTerminateThreadCorePoolSize(int asyncTerminateThreadCorePoolSize) {
        this.asyncTerminateThreadCorePoolSize = asyncTerminateThreadCorePoolSize;
    }

    public void setAsyncTerminateThreadMaxPoolSize(int asyncTerminateThreadMaxPoolSize) {
        this.asyncTerminateThreadMaxPoolSize = asyncTerminateThreadMaxPoolSize;
    }

    public void setAsyncTerminateThreadWorkQueueSize(int asyncTerminateThreadWorkQueueSize) {
        this.asyncTerminateThreadWorkQueueSize = asyncTerminateThreadWorkQueueSize;
    }

    @Override
    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayCancelExceptions) {
        this.delayCancelExceptions.addAll(delayCancelExceptions);
    }

    @Override
    public Set<Class<? extends Exception>> getDelayCancelExceptions() {
        return this.delayCancelExceptions;
    }

    @Override
    public int getAsyncTerminateThreadCorePoolSize() {
        return this.asyncTerminateThreadCorePoolSize;
    }

    @Override
    public int getAsyncTerminateThreadMaxPoolSize() {
        return this.asyncTerminateThreadMaxPoolSize;
    }

    @Override
    public int getAsyncTerminateThreadWorkQueueSize() {
        return this.asyncTerminateThreadWorkQueueSize;
    }

}
