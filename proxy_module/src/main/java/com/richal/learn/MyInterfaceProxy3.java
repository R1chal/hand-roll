package com.richal.learn;

public class MyInterfaceProxy3 implements MyInterface{
    private MyInterface myInterface;
    
    @Override
    public void method1() {

 System.out.println(1); System.out.println("method1");    }

    @Override
    public void method2() {

 System.out.println(1); System.out.println("method2");    }

    @Override
    public void method3() {

 System.out.println(1); System.out.println("method3");    }
}
