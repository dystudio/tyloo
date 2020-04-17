package io.tyloo.context;

import io.tyloo.api.TylooTransactionContext;
import io.tyloo.api.TylooTransactionContextEditor;
import io.tyloo.utils.TylooMethodUtils;

import java.lang.reflect.Method;

/*
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 16:42 2019/4/16
 *
 */

@Deprecated
public class MethodTylooTransactionContextEditor implements TylooTransactionContextEditor {

    @Override
    public TylooTransactionContext get(Object target, Method method, Object[] args) {
        int position = TylooMethodUtils.getTransactionContextParamPosition(method.getParameterTypes());

        if (position >= 0) {
            return (TylooTransactionContext) args[position];
        }
        
        return null;
    }

    @Override
    public void set(TylooTransactionContext tylooTransactionContext, Object target, Method method, Object[] args) {

        int position = TylooMethodUtils.getTransactionContextParamPosition(method.getParameterTypes());
        if (position >= 0) {
            args[position] = tylooTransactionContext;
        }
    }
}
