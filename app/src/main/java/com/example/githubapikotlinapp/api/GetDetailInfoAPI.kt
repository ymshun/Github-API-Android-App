package com.example.githubapikotlinapp.api

import android.util.Log
import com.example.githubapikotlinapp.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetDetailInfoAPI {

    companion object {
        private const val JSON_ARRAY: Int = 0
        private const val JSON_OBJECT: Int = 1
    }

    // github token
    private val MY_TOKEN = "e1c59981c9ae28215d25208681bdbbc60d5892a6"

    lateinit var listener: OnGetAPIListener

    interface OnGetAPIListener {
        fun onGetAPIListener(
            contributorJSONObject: JSONObject?,
            organizationJSONObject: JSONObject?,
            followersJSONArray: JSONArray?
        )
    }

    fun setOnListener(listener: OnGetAPIListener) {
        this.listener = listener
    }


    /**
     *  コルーチンでリクエストを送る
     *
     * @param contributorInfoURL : contributorListを取得するAPIにて、key: urlで取得したAPI
     * @param organizationInfoURL : contributorListを取得するAPIにて、key: organizations_urlで取得したAPI
     * @param followersInfoURL : contributorListを取得するAPIにて、key: followers_urlで取得したAPI
     **/
    fun getDetailInfo(
        contributorInfoURL: String,
        organizationInfoURL: String,
        followersInfoURL: String
    ) = GlobalScope.launch {
        // コルーチンで非同期にAPI取得し、リスナを送る

        val contributorJSONObject =
            withContext(Dispatchers.IO) {
                requestAPI(
                    contributorInfoURL,
                    JSON_OBJECT
                )
            } as JSONObject?

        val organizationJSONArray =
            withContext(Dispatchers.IO) {
                requestAPI(
                    organizationInfoURL,
                    JSON_ARRAY
                )
            } as JSONArray?
        // サイズが0の時はnullにして、リスナで送るときにindexOutOfRange回避
        val organizationJSONObject = if (organizationJSONArray?.length() == 0) {
            null
        } else if(organizationJSONArray?.get(0) == null) {
            null
        }else{
            JSONObject(organizationJSONArray.get(0).toString())
        }

        val followersJSONArray =
            withContext(Dispatchers.IO) {
                requestAPI(
                    followersInfoURL,
                    JSON_ARRAY
                )
            } as JSONArray?

        // リスナで送ってビューに反映
        listener.onGetAPIListener(
            contributorJSONObject,
            organizationJSONObject,
            followersJSONArray
        )

    }


    /**
     *  APIリクエストを送ってその結果をJSONArrayで返す関数
     *  デバッグ時にはエラーコードとエラーメッセージを出力
     *
     * @param API_URL : リクエストを送るAPIのエンドポイント
     * @param type : APIの取得結果がJSONArray 型か JSONObject型のどちらか(JSONArray ->0, JSONObject ->1)
     *
     **/
    private fun requestAPI(API_URL: String, type: Int): Any? {
        // エラーコード変数
        var statusCode: Int? = null
        var statusMsg: String? = null

        var result: String?

        try {
            // URLオブジェクト生成してHTTPコネクション
            val url = URL(API_URL)
            val connection = url.openConnection() as HttpURLConnection
            // auth認証(無しでも行けるが回数制限がきつい)
            connection.setRequestProperty("Authorization",MY_TOKEN)
            val stream: InputStream?

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
        } catch (error: IOException) {
            // リクエスト失敗(主に端末のネットワーク関連エラーではconnection.connectがexceptionをthrow)
            result = null
            statusCode = error.hashCode()
            statusMsg = error.message
        }

        if (BuildConfig.DEBUG && statusMsg != null) {
            // エラーコードデバッグ
            Log.e(
                "REQUEST_ERROR",
                "status_code: $statusCode; message: $statusMsg; at request $API_URL"
            )
        }
        Log.d("test01", result.toString())
        // stringからjsonArray, jsonObjectオブジェクト生成
        return if (result != null && type == JSON_ARRAY) {
            // JSON Array
            JSONArray(result)
        } else if (result != null && type == JSON_OBJECT) {
            //JSON Object
            JSONObject(result)
        } else {
            null
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