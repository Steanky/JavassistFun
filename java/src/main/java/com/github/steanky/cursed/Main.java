package com.github.steanky.cursed;

import javassist.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Main {
    //never before have i seen so many exceptions listed in the throws clause
    public static void main(String[] args)
    throws NotFoundException, CannotCompileException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        ClassPool pool = ClassPool.getDefault();

        CtClass cls = pool.get("com.github.steanky.cursed.ClassToBePreprocessed");
        cls.setName("com.github.steanky.cursed.PreprocessedClassButItsAnInteger");

        for (CtField field : cls.getDeclaredFields()) {
            if (field.hasAnnotation(Primitive.class)) {
                field.setType(CtClass.intType);
            }
        }

        for (CtConstructor ctor : cls.getConstructors()) {
            Object[][] annotations = ctor.getParameterAnnotations();
            for (Object[] parameterAnnotations : annotations) {
                for (Object annotation : parameterAnnotations) {
                    if (annotation instanceof Primitive) {
                        cls.removeConstructor(ctor);
                        cls.addConstructor(CtNewConstructor.make("""
                            public PreprocessedClassButItsAnInteger(int primitive) {
                                this.primitive = primitive;
                            }
                            """, cls));

                        break;
                    }
                }
            }
        }

        Class<?> generatedClass = cls.toClass(Main.class);

        System.out.println(Arrays.stream(generatedClass.getDeclaredFields()).map(f -> f.getType() + " " + f.getName())
                                 .reduce("", String::concat));

        //instance of PreprocessedClassButItsAnInteger which is not defined at compile time, woo
        Object obj = generatedClass.getDeclaredConstructor(int.class).newInstance(69);

        Field field = generatedClass.getDeclaredField("primitive");
        field.setAccessible(true);

        int value = (int) field.get(obj);

        System.out.println(value);
    }
}