package com.fresh.xy.rmq.tx;

import com.fresh.common.utils.AssertUtils;
import com.fresh.common.utils.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Deprecated
public class FlRmqTxExecution<T extends RmqTxModel> {
    private Object obj;
    private Method method;
    private T model;

    public FlRmqTxExecution(Object obj, T model, String methodName, Class<?>... paramTypes) {
        AssertUtils.notNull(obj, () -> "参数obj不能为空", null);
        AssertUtils.ifTrue(methodName==null || methodName.trim().length()==0, () -> "参数methodName不能为空", null);
        AssertUtils.notNull(model, () -> "参数model不能为空", null);
        this.obj = obj;
        this.model = model;
        Method methodExists = ReflectUtils.getMethod(obj.getClass(), methodName, paramTypes);
        AssertUtils.notNull(methodExists, () -> "methodName {" + methodName + "} not exists", null);
        this.method = methodExists;
    }

    public void execute(String txId) {
        try {
            model.setTxId(txId);
            method.invoke(obj, model);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
