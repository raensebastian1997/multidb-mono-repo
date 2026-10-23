package com.ismarianto.microservice.security;

class RianUpdate {
    private String namalain;

    RianUpdate(String rian) {
        this.namalain = rian;
    }

    @Override
    public String toString() {
        return this.namalain;
    }

    public static String show(String namalain) {
        return namalain;
    }
}

public class ChainClassImpl<T> {
    private String nama;
    private String alamat;


    private ChainClassImpl() {
    }

    private ChainClassImpl setNama(String nama) {
        this.nama = nama;
        return this;
    }

    private ChainClassImpl setAlamat(String alamat) {
        this.alamat = alamat + "---- === ";
        return this;
    }

    public <S extends T> String updateGenerict(String parameter, S subtypeObject) {
        return "berhasil memenerpbau oarmaeter  : " + parameter + "sample outpur parameter : " + subtypeObject.getClass().getSimpleName();

    }

    public static void main(String[] args) {

        ChainClassImpl cn = new ChainClassImpl()
                .setNama("rian")
                .setAlamat("alamasa");

        Integer subTypeValue = 100;
        String rian = cn.updateGenerict("ss", subTypeValue);
        System.out.printf(" %s %s %s", cn.alamat, cn.nama, RianUpdate.show("adsa") + " rian  : " + rian);
    }

}
