package com.example.calendertestdb


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.calendertestdb.databinding.ActivityMainBinding
import java.time.Clock
import java.time.LocalDate.*
import java.time.ZoneId
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    // データベースヘルパーオブジェクト
    private val _helper = DatabaseHelper(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var date: String? = null


        // 〜今日までを選択範囲にする
        var calendar = Calendar.getInstance()
        binding.calendarView.maxDate = calendar.timeInMillis

        // カレンダー選択→データ表示
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth, ->
            date = "$year-${month + 1}-$dayOfMonth"

            // 曜日を表示するための計算
            var _dayOfWeek = dayOfMonth % 7
            var week = when (_dayOfWeek) {
                0 -> "（日）"
                1 -> "（月）"
                2 -> "（火）"
                3 -> "（水）"
                4 -> "（木）"
                5 -> "（金）"
                6 -> "（土）"
                else -> ""
            }
            // 日付の表示
            binding.viewDate.text = date + week

            // ここからDB処理
            // データベースヘルパーオブジェクトから、データベース接続オブジェクトを取得する
            val db = _helper.writableDatabase

            // 日付によるデータ検索 SQLiteの日付は文字列なので、''でエスケープする必要がある。
            // アホほどつまったので注意
            // https://www.dbonline.jp/sqlite/type/index4.html
            val sql = "SELECT * FROM thermoList WHERE datetime = '$date'"


            // SQLの実行
            val cursor = db.rawQuery(sql, null)
            // データベースから取得した値を格納する変数の用意。
            // データがなかった時のために、初期値も用意する。
            var note = ""

            // SQL実行の戻り値である cursorオブジェクトをループさせて、
            // データベース内のデータを取得する。
            while (cursor.moveToNext()) {
                // カラムのインデックス値を取得
                val indexNote = cursor.getColumnIndex("thermo")
                // カラムのインデックス値を元に、実際のデータを取得
                note = cursor.getString(indexNote)
                Log.d("test", note)
            }
            binding.editThermo.setText(note)
        }


        // 更新処理
        binding.setThermo.setOnClickListener {
            val note = binding.editThermo.text.toString()

            // その日の体調用の変数　後でスピナーかなんかにいれる
            var condition = ""
            // データベースヘルパーオブジェクトから、
            // データベース接続オブジェクトを取得
            val db = _helper.writableDatabase

            // リストで選択されたカクテルのメモデータを削除。その後インサート
            // 削除用SQL文字列を用意

            Log.d("test", date.toString())
            val sqlDelete = "DELETE FROM thermoList WHERE datetime = '$date'"

            // SQL文字列を元にプリペアドステートメントを取得
            // プリペアドステートメント：SQL文で値がいつでも変更できるように、変更する箇所だけ変数のようにした命令文を作る仕組みのこと
            // https://qiita.com/wakahara3/items/d7a3674eecd3b021a21e
            var stmt = db.compileStatement(sqlDelete)

            // 削除SQLの実行
            stmt.executeUpdateDelete()

            // インサート用SQL文字列の用意
            val sqlInsert = "INSERT INTO thermoList(datetime, thermo, condition) VALUES(?,?,?)"
            // SQL文字列を元にプリペアドステートメントを取得
            stmt = db.compileStatement(sqlInsert)
            // 変数のバイド
            stmt.bindString(1, date)
            stmt.bindString(2, note)
            stmt.bindString(3, condition)
            // インサートSQLの実行
            stmt.executeInsert()

            binding.editThermo.setText("")

        }
    }


    override fun onDestroy() {
        // ヘルパーオブジェクトの開放
        _helper.close()
        super.onDestroy()
    }


}