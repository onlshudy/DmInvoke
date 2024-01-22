package com.tjut.enums;

public enum KeyBorad {

    W(87),
    A(65),
    S(83),
    D(68);


    private int vk_code;

    KeyBorad(int i) {
        this.vk_code = i;
    }

    public int getVk_code() {
        return vk_code;
    }
}
