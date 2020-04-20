

package io.tyloo.enums;

/*
 * ��������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 15:28 2019/4/17
 *
 */

public enum TransactionType {

    /**
     * ������:1.
     */
    ROOT(1),

    /**
     * ��֧����:2.
     */
    BRANCH(2);

    int id;

    TransactionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TransactionType valueOf(int id) {
        switch (id) {
            case 1:
                return ROOT;
            case 2:
                return BRANCH;
            default:
                return null;
        }
    }

}
