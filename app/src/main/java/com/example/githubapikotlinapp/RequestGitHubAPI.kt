package com.example.githubapikotlinapp

import android.os.AsyncTask
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

/**
 *  GitHub API にリクエストを送って取得するAsyncクラス
 *
 * **/


class RequestGitHubAPI() : AsyncTask<String, String, String>() {

    // APIのエンドポイント
    private val API_URL =
        "https://api.github.com/repos/googlesamples/android-architecture-components/contributors"

    private lateinit var listener: OnRequestAPI

    // エラーコード変数
    private var statusCode: Int? = null
    private var statusMsg: String? = null


    interface OnRequestAPI {
        // API リクエストを送る前
        fun onPreRequestAPI()

        // APIを取得し終わったときの処理
        fun onSucceedRequestAPI(jsonArray: JSONArray)

        // API取得に失敗したとき
        fun onFailedRequestAPI(errorCode:Int, errorMsg: String)
    }

    // リスナの通知
    fun setRequestAPIListener(listener: OnRequestAPI) {
        this.listener = listener
    }


    // バックグラウンド処理に入る直前にプログレスなど更新
    override fun onPreExecute() {
        super.onPreExecute()
        listener.onPreRequestAPI()
    }


    // バックグラウンドスレッド処理
    override fun doInBackground(vararg params: String?): String? {

        // URLオブジェクト生成してHTTPコネクション
        val url = URL(API_URL)
        val connection = url.openConnection() as HttpURLConnection
        val stream: InputStream?
        var result: String?

        try {
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                // リクエスト成功
                stream = connection.inputStream
                result = streamToString(stream)
            } else {
                // リクエスト失敗
                stream = null
                result = null
                statusCode = connection.responseCode
                statusMsg = connection.responseMessage
            }

            //各オブジェクト開放
            connection.disconnect()
            stream?.close()
        }
        catch (error: IOException){
            // リクエスト失敗(主に端末のネットワーク関連エラーではconnection.connectがexceptionをthrow)
            result = null
            statusCode = error.hashCode()
            statusMsg = error.message
        }


        return result
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        if (result != null) {
            // APIリクエスト成功時

            // stringからjsonArrayオブジェクト生成
            val jsonArray: JSONArray = JSONArray(result)
            listener.onSucceedRequestAPI(jsonArray)  // リスナで実装

        } else {
            // APIリクエスト失敗時
            listener.onFailedRequestAPI(statusCode!!, statusMsg!!) // リスナで実装
        }
    }


    /**
     *  InputStream を stringに変化する関数
     *
     * @param stream : Stringに変換したいinputStream
     **/
    fun streamToString(stream: InputStream): String {
        val builder = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        var line = reader.readLine()
        while (line != null) {
            builder.append(line)
            line = reader.readLine()
        }

        reader.close()
        return builder.toString()
    }
}