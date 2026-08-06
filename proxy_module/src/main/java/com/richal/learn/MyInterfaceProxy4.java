package com.richal.learn;

public class MyInterfaceProxy4 implements MyInterface{
    private MyInterface myInterface;
    
    @Override
    public void method1() {

 System.out.println("before");
 this.myInterface.method1();
 System.out.println("after");    }

    @Override
    public void method2() {

 System.out.println("before");
 this.myInterface.method2();
 System.out.println("after");    }

    @Override
    public void method3() {

 System.out.println("before");
 this.myInterface.method3();
 System.out.println("after");    }
}
