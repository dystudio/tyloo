package io.tyloo.spring.recover;

import io.tyloo.exception.SystemException;
import io.tyloo.recover.TylooTransactionRecovery;
import io.tyloo.support.TransactionConfigurator;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/*
 *
 * ����ָ���ʱ����
 * ���� Quartz ʵ�ֵ��ȣ�����ִ������ָ�
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 20:15 2019/10/29
 *
 */

public class RecoverScheduledJob {

    /**
     * ����ָ�
     */
    private TylooTransactionRecovery tylooTransactionRecovery;

    /**
     * ע�����TCC����������.
     */
    private TransactionConfigurator transactionConfigurator;

    /**
     * ����ָ����������(����ע�����org.springframework.scheduling.quartz.SchedulerFactoryBeanʵ��)
     */
    private Scheduler scheduler;

    /**
     * ��ʼ��������Spring����ʱִ��.
     */
    public void init() {

        try {

            // MethodInvokingJobDetailFactoryBean �������ɾ��������ֻ��Ҫָ��ĳ�������ĳ���������ڴ���������ʱ��������ָ�������ָ������
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();

            // ָ���������Ӧ�ĵ��ö����������������������ʵ���κνӿ�
            jobDetail.setTargetObject(tylooTransactionRecovery);

            // ָ����targetObject������ĳ���ķ���(�˴�����TransactionRecovery�е�startRecover����)
            jobDetail.setTargetMethod("startRecover");

            // ������������
            jobDetail.setName("transactionRecoveryJob");

            // �Ƿ��������񲢷�ִ�У���Ĭ���ǲ���ִ�еģ���ʱ����������á�concurrent��Ϊfalse���ܿ��ܴ��������������������⣬���Ҽ��ʽ�С�������׸���,
            // ����Ϊfalse��ʾ����һ������ִ������ٿ����µ�����
            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();

            // �������������࣬�ñ�ָ���ĵ�������������ָ������Ĵ���������
            // ���ฺ����spring�����д���һ���������������IDӦ����SchedulerFactoryBean���Ե�List�б����ã�����������������ܱ�֤��ĳ��ָ������������
            CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();

            // ���ô���������
            cronTrigger.setBeanName("transactionRecoveryCronTrigger");
            // ������������ͨ��������������ȡ����ָ���ʱ�������
            cronTrigger.setCronExpression(transactionConfigurator.getTylooRecoverConfiguration().getCronExpression());
            cronTrigger.setJobDetail(jobDetail.getObject());
            cronTrigger.afterPropertiesSet();

            // ���õ�������
            scheduler.scheduleJob(jobDetail.getObject(), cronTrigger.getObject());

            // �������������
            scheduler.start();

        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    public void setTylooTransactionRecovery(TylooTransactionRecovery tylooTransactionRecovery) {
        this.tylooTransactionRecovery = tylooTransactionRecovery;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
