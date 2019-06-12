package modbat.test;

import java.io.IOException;

public class MainTest23 {

    public final static void main(String[] args) throws Exception {

        modbat.examples.ControlCounter controlCounter0 = new modbat.examples.ControlCounter();
        org.objenesis.ObjenesisStd objenesisStd1 = new org.objenesis.ObjenesisStd();
        java.lang.Class<?> wildcardClass3 = java.lang.Class.forName("modbat.examples.ControlCounter");
        java.lang.Object obj4 = modbat.genran.RandoopUtils.newInstance(objenesisStd1, wildcardClass3);
        java.lang.reflect.Field[] fieldArray5 = randoop.org.apache.commons.lang3.reflect.FieldUtils.getAllFields(wildcardClass3);
        java.lang.reflect.Field field7 = modbat.genran.RandoopUtils.getField(fieldArray5, 0);
        field7.setAccessible(true);
        field7.set(obj4, (java.lang.Object)2);
        java.lang.reflect.Field field13 = modbat.genran.RandoopUtils.getField(fieldArray5, 1);
        field13.setAccessible(true);
        field13.set(obj4, (java.lang.Object)2);
        java.lang.reflect.Field field19 = modbat.genran.RandoopUtils.getField(fieldArray5, 2);
        field19.setAccessible(true);
        field19.set(obj4, (java.lang.Object)true);
        @SuppressWarnings("unchecked")
        modbat.examples.ControlCounter controlCounter24 = (modbat.examples.ControlCounter)obj4;
        boolean boolean26 = controlCounter24.equals((java.lang.Object)0.0f);
        boolean boolean27 = controlCounter0.equals((java.lang.Object)controlCounter24);
        int int28 = controlCounter24.value();
        java.lang.Class<?> wildcardClass29 = controlCounter24.getClass();



        controlCounter24.toggleSwitch();
        controlCounter24.inc();

    }


}
