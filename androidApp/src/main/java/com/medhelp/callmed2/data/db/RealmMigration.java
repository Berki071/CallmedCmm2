//package com.medhelp.callmed2.data.db;//package com.medhelp.callmed2.data.db;
//
//import androidx.annotation.NonNull;
//
//import io.realm.DynamicRealm;
//import io.realm.RealmSchema;
//
//@SuppressWarnings("all")
//public class RealmMigration implements io.realm.RealmMigration {
//    @Override
//    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
//        RealmSchema schema = realm.getSchema();
//
//        if (oldVersion == 0) {
//            oldVersion++;
//        }
//
//        if(oldVersion == 1){
//            schema.get("InfoAboutKL")
//                            .addField("statusChat", String.class);
//        }
//    }
//
//    @Override
//    public int hashCode() {
//        return 37;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        return (o instanceof RealmMigration);
//    }
//}
