package io.tyloo.recover;

import com.alibaba.fastjson.JSON;
import io.tyloo.OptimisticLockException;
import io.tyloo.Transaction;
import io.tyloo.TransactionRepository;
import io.tyloo.api.TylooTransactionStatus;
import io.tyloo.common.TransactionType;
import io.tyloo.support.TransactionConfigurator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 *
 * ����ָ�
 *
 * ������Ϣ���־û����ⲿ�Ĵ洢��������⣩�С�����洢������ָ��Ļ�����ͨ����ȡ�ⲿ�洢���е��쳣���񣬶�ʱ����ᰴ��һ��Ƶ�ʶ�����������ԣ�ֱ��������ɻ򳬹�������Դ�����
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 18:51 2019/5/2
 *
 */
public class TylooTransactionRecovery {

    static final Logger logger = Logger.getLogger(TylooTransactionRecovery.class.getSimpleName());

    /**
     * TCC����������.
     */
    private TransactionConfigurator transactionConfigurator;

    /**
     * ��������ָ�����(��RecoverScheduledJob��ʱ�������).
     */
    public void startRecover() throws CloneNotSupportedException {

        List<Transaction> transactions = loadErrorTransactions();

        recoverErrorTransactions(transactions);
    }
    /**
     * �ҳ�����ִ�д����������Ϣ
     *
     * @return
     */
    private List<Transaction> loadErrorTransactions() {


        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        TylooRecoverConfiguration tylooRecoverConfiguration = transactionConfigurator.getTylooRecoverConfiguration();

        return transactionRepository.findAllUnmodifiedSince(new Date(currentTimeInMillis - tylooRecoverConfiguration.getRecoverDuration() * 1000));
    }

    /**
     * �ָ����������.
     *
     * @param transactions
     */
    private void recoverErrorTransactions(List<Transaction> transactions) throws CloneNotSupportedException {


        for (Transaction transaction : transactions) {

            //�Ƚ����Դ���������������������
            if (transaction.getRetriedCount() > transactionConfigurator.getTylooRecoverConfiguration().getMaxRetryCount()) {

                logger.error(String.format("recover failed with max retry count,will not try again. txid:%s, status:%s,retried count:%d,transaction content:%s", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount(), JSON.toJSONString(transaction)));
                continue;
            }

            //��ǰ�����Ƿ�֧�����ʱ��������������
            if (transaction.getTransactionType().equals(TransactionType.BRANCH)
                    && (transaction.getCreateTime().getTime() +
                    transactionConfigurator.getTylooRecoverConfiguration().getMaxRetryCount() *
                            transactionConfigurator.getTylooRecoverConfiguration().getRecoverDuration() * 1000
                    > System.currentTimeMillis())) {
                continue;
            }
            
            try {
                transaction.addRetriedCount();

                // �����CONFIRMING(2)״̬����������ǰִ��
                if (transaction.getStatus().equals(TylooTransactionStatus.CONFIRMING)) {

                    transaction.changeStatus(TylooTransactionStatus.CONFIRMING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    transaction.commit();
                    transactionConfigurator.getTransactionRepository().delete(transaction);

                } else if (transaction.getStatus().equals(TylooTransactionStatus.CANCELLING)
                        || transaction.getTransactionType().equals(TransactionType.ROOT)) {

                    // ���������������״̬��ΪCANCELLING(3)��Ȼ��ִ�лع�
                    transaction.changeStatus(TylooTransactionStatus.CANCELLING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    transaction.rollback();
                    // ��������£���ʱû�����������־ֱ��ɾ��
                    transactionConfigurator.getTransactionRepository().delete(transaction);
                }

            } catch (Throwable throwable) {

                if (throwable instanceof OptimisticLockException
                        || ExceptionUtils.getRootCause(throwable) instanceof OptimisticLockException) {
                    logger.warn(String.format("optimisticLockException happened while recover. txid:%s, status:%s,retried count:%d,transaction content:%s", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount(), JSON.toJSONString(transaction)), throwable);
                } else {
                    logger.error(String.format("recover failed, txid:%s, status:%s,retried count:%d,transaction content:%s", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount(), JSON.toJSONString(transaction)), throwable);
                }
            }
        }
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
