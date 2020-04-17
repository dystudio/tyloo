package io.tyloo;

import io.tyloo.api.TylooTransactionXid;

import java.util.Date;
import java.util.List;

/*
 *
 * �����(����tcc���ݿ��dao)
 * ��ʵ�����ڱ�����Ŀdubbo-order�е�config.spring.local��appcontext-service-dao.xml�����ã�
 * ����ʵ������SpringJdbcTransactionRepository���̳�JdbcTransactionRepository����Ϊorder��capital��redpacket��3�������ķ���
 * ����ÿһ��ģ���ж���appcontext-service-dao.xml���������ӳ�
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 19:00 2019/6/12
 *
 */

public interface TransactionRepository {

    int create(Transaction transaction) throws CloneNotSupportedException;

    int update(Transaction transaction) throws CloneNotSupportedException;

    int delete(Transaction transaction) throws CloneNotSupportedException;

    Transaction findByXid(TylooTransactionXid xid) throws CloneNotSupportedException;

    List<Transaction> findAllUnmodifiedSince(Date date) throws CloneNotSupportedException;
}
