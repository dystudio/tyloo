package io.tyloo.support;

import io.tyloo.common.TransactionManager;
import io.tyloo.repository.TransactionRepository;
import io.tyloo.recover.TylooRecoverConfiguration;

/*
 *
 * ����������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 19:34 2019/5/28
 *
 */

public interface TransactionConfigurator {

    /**
     * ��ȡ���������.
     *
     * @return
     */
    TransactionManager getTransactionManager();

    /**
     * ��ȡ�����.
     *
     * @return
     */
    TransactionRepository getTransactionRepository();

    /**
     * ��ȡ����ָ�����.
     *
     * @return
     */
    TylooRecoverConfiguration getTylooRecoverConfiguration();
}
