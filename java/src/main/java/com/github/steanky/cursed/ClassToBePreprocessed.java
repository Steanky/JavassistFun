package com.github.steanky.cursed;

public class ClassToBePreprocessed {
    @Primitive
    private final Object primitive;

    public ClassToBePreprocessed(@Primitive Object primitive) {
        this.primitive = primitive;
    }
}
