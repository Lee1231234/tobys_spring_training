package com.example.spring.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyTest {
    public DynamicProxyTest() {
    }

    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget();
        Assert.assertThat(hello.sayHello("Toby"), CoreMatchers.is("Hello Toby"));
        Assert.assertThat(hello.sayHi("Toby"), CoreMatchers.is("Hi Toby"));
        Assert.assertThat(hello.sayThankYou("Toby"), CoreMatchers.is("Thank You Toby"));
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Hello.class}, new UppercaseHandler(new HelloTarget()));
        Assert.assertThat(proxiedHello.sayHello("Toby"), CoreMatchers.is("HELLO TOBY"));
        Assert.assertThat(proxiedHello.sayHi("Toby"), CoreMatchers.is("HI TOBY"));
        Assert.assertThat(proxiedHello.sayThankYou("Toby"), CoreMatchers.is("THANK YOU TOBY"));

    }

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());
        Hello proxiedHello = (Hello)pfBean.getObject();
        Assert.assertThat(proxiedHello.sayHello("Toby"), CoreMatchers.is("HELLO TOBY"));
        Assert.assertThat(proxiedHello.sayHi("Toby"), CoreMatchers.is("HI TOBY"));
        Assert.assertThat(proxiedHello.sayThankYou("Toby"), CoreMatchers.is("THANK YOU TOBY"));
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello)pfBean.getObject();
        Assert.assertThat(proxiedHello.sayHello("Toby"), CoreMatchers.is("HELLO TOBY"));
        Assert.assertThat(proxiedHello.sayHi("Toby"), CoreMatchers.is("HI TOBY"));
        Assert.assertThat(proxiedHello.sayThankYou("Toby"), CoreMatchers.is("Thank You Toby"));
    }

    interface Hello {
        String sayHello(String var1);

        String sayHi(String var1);

        String sayThankYou(String var1);
    }

    static class HelloTarget implements Hello {
        HelloTarget() {
        }

        public String sayHello(String name) {
            return "Hello " + name;
        }

        public String sayHi(String name) {
            return "Hi " + name;
        }

        public String sayThankYou(String name) {
            return "Thank You " + name;
        }
    }

    static class HelloUppercase implements Hello {
        Hello hello;

        public HelloUppercase(Hello hello) {
            this.hello = hello;
        }

        public String sayHello(String name) {
            return this.hello.sayHello(name).toUpperCase();
        }

        public String sayHi(String name) {
            return this.hello.sayHi(name).toUpperCase();
        }

        public String sayThankYou(String name) {
            return this.hello.sayThankYou(name).toUpperCase();
        }
    }

    static class UppercaseAdvice implements MethodInterceptor {
        UppercaseAdvice() {
        }

        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String)invocation.proceed();
            return ret.toUpperCase();
        }
    }

    static class UppercaseHandler implements InvocationHandler {
        Object target;

        private UppercaseHandler(Object target) {
            this.target = target;
        }


        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object ret = method.invoke(this.target, args);
            return ret instanceof String && method.getName().startsWith("say") ? ((String)ret).toUpperCase() : ret;
        }
    }
}
