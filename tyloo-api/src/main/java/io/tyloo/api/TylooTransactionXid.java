package io.tyloo.api;


import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import cn.hutool.core.lang.UUID;

/*
 * xid�������ţ�����Ψһ��ʶһ������ʹ�� UUID �㷨���ɣ���֤Ψһ�ԡ�
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 14:57 2019/4/10
 *
 */
public class TylooTransactionXid implements Xid, Serializable {

    private static final long serialVersionUID = -6817267250789142043L;

    /**
     * XID �ĸ�ʽ��ʶ��
     * ��һ�����֣����ڱ�ʶ��globalTransactionId��branchQualifierֵʹ�õĸ�ʽ��Ĭ��ֵ��1��
     */
    private final int formatId = 1;

    /**
     * ȫ������ID.
     * ��ͬ�ķֲ�ʽ����Ӧ��ʹ����ͬ��globalTransactionId������������ȷ֪��XA���������ĸ��ֲ�ʽ����
     */
    private byte[] globalTransactionId;

    /**
     * ��֧�޶���.
     * Ĭ��ֵ�ǿմ�������һ���ֲ�ʽ�����е�ÿ����֧����bqual��ֵ����Ψһ
     */
    private byte[] branchQualifier;

    private static final byte[] CUSTOMIZED_TRANSACTION_ID = "UniqueIdentity".getBytes();

    public TylooTransactionXid() {
        globalTransactionId = uuidToByteArray(UUID.randomUUID());
        branchQualifier = uuidToByteArray(UUID.randomUUID());
    }

    public void setGlobalTransactionId(byte[] globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
    }

    public void setBranchQualifier(byte[] branchQualifier) {
        this.branchQualifier = branchQualifier;
    }

    public TylooTransactionXid(Object uniqueIdentity) {

        if (uniqueIdentity == null) {
            globalTransactionId = uuidToByteArray(UUID.randomUUID());
            branchQualifier = uuidToByteArray(UUID.randomUUID());

        } else {
            this.globalTransactionId = CUSTOMIZED_TRANSACTION_ID;
            this.branchQualifier = uniqueIdentity.toString().getBytes();
        }
    }

    public TylooTransactionXid(byte[] globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = uuidToByteArray(UUID.randomUUID());
    }

    public TylooTransactionXid(byte[] globalTransactionId, byte[] branchQualifier) {
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }

    /**
     * ��ȡ XID �ĸ�ʽ��ʶ�����֡�
     */
    @Override
    public int getFormatId() {
        return formatId;
    }

    /**
     * ��ȡ XID ��ȫ�������ʶ��������Ϊ�ֽ����顣
     */
    @Override
    public byte[] getGlobalTransactionId() {
        return globalTransactionId;
    }

    /**
     * ��ȡ XID �������֧��ʶ��������Ϊ�ֽ����顣
     */
    @Override
    public byte[] getBranchQualifier() {
        return branchQualifier;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        if (Arrays.equals(CUSTOMIZED_TRANSACTION_ID, globalTransactionId)) {

            stringBuilder.append(new String(globalTransactionId));
            stringBuilder.append(":").append(new String(branchQualifier));

        } else {

            stringBuilder.append(UUID.nameUUIDFromBytes(globalTransactionId).toString());
            stringBuilder.append(":").append(UUID.nameUUIDFromBytes(branchQualifier).toString());
        }

        return stringBuilder.toString();
    }

    @Override
    public TylooTransactionXid clone() throws CloneNotSupportedException {

        TylooTransactionXid clone = (TylooTransactionXid) super.clone();

        byte[] cloneGlobalTransactionId = null;
        byte[] cloneBranchQualifier = null;

        if (globalTransactionId != null) {
            cloneGlobalTransactionId = new byte[globalTransactionId.length];
            System.arraycopy(globalTransactionId, 0, cloneGlobalTransactionId, 0, globalTransactionId.length);
        }

        if (branchQualifier != null) {
            cloneBranchQualifier = new byte[branchQualifier.length];
            System.arraycopy(branchQualifier, 0, cloneBranchQualifier, 0, branchQualifier.length);
        }

        return new TylooTransactionXid(cloneGlobalTransactionId, cloneBranchQualifier);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.getFormatId();
        result = prime * result + Arrays.hashCode(branchQualifier);
        result = prime * result + Arrays.hashCode(globalTransactionId);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;

    }

    private static byte[] uuidToByteArray(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

}


