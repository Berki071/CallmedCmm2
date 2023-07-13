package com.medhelp.callmed2.data.model;

import com.google.gson.annotations.SerializedName;

public class UserForRecordItem implements Comparable {
    @SerializedName("idkl")
    String id;
    @SerializedName("idfilial")
    String idFilial;
    @SerializedName("fam")
    String surnameEncoded;
    @SerializedName("kf")
    String keySurname;
    @SerializedName("name")
    String name;
    @SerializedName("otch")
    String patronymic;
    @SerializedName("tel")
    String phoneEncoded;
    @SerializedName("kt")
    String keyPhone;
    @SerializedName("dr")
    String dr;

    String surname;
    String phone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(String idFilial) {
        this.idFilial = idFilial;
    }

    public String getSurnameEncoded() {
        return surnameEncoded;
    }

    public void setSurnameEncoded(String surnameEncoded) {
        this.surnameEncoded = surnameEncoded;
    }

    public String getKeySurname() {
        return keySurname;
    }

    public void setKeySurname(String keySurname) {
        this.keySurname = keySurname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhoneEncoded() {
        return phoneEncoded;
    }

    public void setPhoneEncoded(String phoneEncoded) {
        this.phoneEncoded = phoneEncoded;
    }

    public String getKeyPhone() {
        return keyPhone;
    }

    public void setKeyPhone(String keyPhone) {
        this.keyPhone = keyPhone;
    }

    public String getDr() {
        return dr;
    }

    public void setDr(String dr) {
        this.dr = dr;
    }

    public String getSurname() {
        if(surname==null  && surnameEncoded!=null  && keySurname!=null){
            surname="";
            try {
                for (int i = 0; i < surnameEncoded.length(); i++)
                    surname += (char) (surnameEncoded.charAt(i) ^ keySurname.charAt(i));
            }catch (Exception e){}
        }

        return surname;
    }

    public String getPhone() {
        if(phone==null  && phoneEncoded!=null  && keyPhone!=null){
            phone="";
            try {
                for (int i = 0; i < phoneEncoded.length(); i++)
                    phone += (char) (phoneEncoded.charAt(i) ^ keyPhone.charAt(i));
            }catch (Exception e){}
        }

        return phone;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int compareTo(Object o) {
        UserForRecordItem tmp=(UserForRecordItem)o;
        String str1=getSurname()+getName()+getPatronymic();
        String str2=tmp.getSurname()+tmp.getName()+tmp.getPatronymic();

        return str1.compareTo(str2);
    }
}
