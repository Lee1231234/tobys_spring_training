package com.example.spring.user.Service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransactionHandler implements InvocationHandler {
    Object target;
    PlatformTransactionManager transactionManager;
    String pattern;

    public TransactionHandler() {
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.getName().startsWith(this.pattern) ? this.invokeInTransaction(method, args) : method.invoke(this.target, args);
    }

    private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object ret = method.invoke(this.target, args);
            this.transactionManager.commit(status);
            return ret;
        } catch (InvocationTargetException var5) {
            this.transactionManager.rollback(status);
            throw var5.getTargetException();
        }
    }
}
