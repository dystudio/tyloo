package io.tyloo.api;

/*
 *
 * ÊÂÎñ×´Ì¬Ã¶¾Ù
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 12:05 2019/4/5
 *
 */
public enum TylooTransactionStatus {

    /**
     * try½×¶Î
     */
    TRYING(1),

    /**
     * confirm½×¶Î
     */
    CONFIRMING(2),

    /**
     * cancel½×¶Î
     */
    CANCELLING(3);

    private int id;

     TylooTransactionStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TylooTransactionStatus valueOf(int id) {

        switch (id) {
            case 1:
                return TRYING;
            case 2:
                return CONFIRMING;
            default:
                return CANCELLING;
        }
    }

}
