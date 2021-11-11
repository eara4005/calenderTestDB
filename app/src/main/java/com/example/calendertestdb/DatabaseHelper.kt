package com.example.calendertestdb

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // クラス内のprivate定数を宣言
    companion object {
        // データベース名の定義
        private const val DATABASE_NAME = "thermoDB.db"

        // バージョン情報の定数
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // テーブル作成用SQL文字列の作成
        val sb = StringBuilder()
        sb.append("CREATE TABLE thermoList (") // テーブル名
        sb.append("datetime TIMESTAMP DEFAULT (date(CURRENT_TIMESTAMP,'localtime')) PRIMARY KEY,") // 時間
        sb.append("thermo TEXT,") // 体温
        sb.append("condition TEXT") // 症状　コンディション
        sb.append(");")

        val sql = sb.toString()

        // SQLの実行
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
}