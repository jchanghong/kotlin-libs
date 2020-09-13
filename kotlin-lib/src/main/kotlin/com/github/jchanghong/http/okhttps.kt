package com.github.jchanghong.http

import cn.hutool.crypto.SecureUtil
import cn.hutool.json.JSONUtil
import com.github.jchanghong.log.kInfo
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*

object OkHttps {
    /** https://1.1.1.1:443*/
    var pviaURL: String = "https://1.1.1.1:443"
    val httpClient: OkHttpClient by lazy {
        kInfo("初始化Okhttp..............")
        val build = OkHttpClient.Builder()
                .followRedirects(true).followSslRedirects(true)
                .addInterceptor { chain ->
                    val request = chain.request()
                    logger.info(request.toString())
                    chain.proceed(request)
                }
                .cookieJar(object : CookieJar {
                    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                        for (cookie in cookies) {
                            val hashMap = cookieStore.getOrPut(cookie.domain) { hashMapOf() }
                            hashMap[cookie.name] = cookie
                        }
                        logger.info("saveFromResponse ${url.host}" + cookies.joinToString { it.name + it.value })
                    }

                    override fun loadForRequest(url: HttpUrl): List<Cookie> {
                        val hashMap = cookieStore.getOrPut(url.host) { hashMapOf() }
                        logger.info("loadForRequest ${url.host}")
                        return hashMap.values.toList()
                    }
                })
                .sslSocketFactory(createSSLSocketFactory(), TrustAllCerts())
                .hostnameVerifier(TrustAllHostnameVerifier())
                .build()
        kInfo("初始化完成okhttp...........")
        build
    }
    private val logger = LoggerFactory.getLogger(OkHttps::class.java)

    private class TrustAllCerts : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String?) {
//            logger.info(authType+chain.firstOrNull().toString())
        }

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String?) {
//            logger.info(authType+chain.firstOrNull().toString())
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }

    }

    private class TrustAllHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String?, session: SSLSession?): Boolean {
            logger.info(hostname + session.toString())
            return true
        }
    }

    private fun createSSLSocketFactory(): SSLSocketFactory {
        val sc: SSLContext = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf<TrustManager>(TrustAllCerts()), SecureRandom())
        return sc.socketFactory
    }

    val cookieStore = HashMap<String, HashMap<String, Cookie>>()

    /** 保存最新请求的url。需要登陆的时候登陆后，重新请求*/
    var lasthttpRequest: Request? = null

    fun requestCSRFRequestHead(
            url: String,
            nameRegex: String = "_csrf_header",
            valueRegex: String = "_csrf",
            loginaction: (() -> Unit)? = null
    ): Pair<String, String> {
        val execute4 = httpClient.get(url, null, loginaction)
//        <meta name="_csrf_header" content="X-CSRF-TOKEN" />
//
//	<meta name="_csrf" content="6499598e-5027-44b3-a052-0f2f77dfe543" />
        val toRegex1 = """name="${valueRegex}"\s+content="(\S+)"""".toRegex()
        val toRegex2 = """name="${nameRegex}"\s+content="(\S+)"""".toRegex()
        val v = toRegex1.find(execute4)!!.groupValues[1]
        val k = toRegex2.find(execute4)!!.groupValues[1]
        logger.info(" csrf :$k  -> $v")
        return k to v
    }

    fun loginForPvia(user: String, password: String) {
        val execute2 =
                httpClient.postForm("${OkHttps.pviaURL}/portal/login/ajax/postLoginData.do", mapOf("userName" to user))
        val parseObj1 = JSONUtil.parseObj(execute2)
        val parseObj = parseObj1.getJSONObject("data")
        val vcodestr = parseObj.getStr("vCode")
        val salt = parseObj.getStr("salt")
        val passtmp = SecureUtil.sha256(SecureUtil.sha256(password + salt) + vcodestr)

        val formBodyLogin = hashMapOf<String, String>()
        formBodyLogin.set("userName", user)
        formBodyLogin.set("password", passtmp)
        formBodyLogin.set("serviceUrl", """${OkHttps.pviaURL}/portal/cas/loginPage?service=${OkHttps.pviaURL}/portal""")
        formBodyLogin.set("imageCode", "")
        formBodyLogin.set("codeId", parseObj.getStr("codeId"))
        formBodyLogin.set("userType", "0")
        formBodyLogin.set("lang", "zh_CN")
        val execute3 = httpClient.postForm("${OkHttps.pviaURL}/portal/login/ajax/submit.do", formBodyLogin)
        println(execute3)
        val url2 = JSONUtil.parseObj(execute3).getStr("data").toString()
        if (!url2.isNullOrBlank()) {
            logger.info("登陆成功")
        } else {
            logger.error("登陆失败")
        }
        val execute5 = httpClient.newCall(Request.Builder().url(url2).build()).execute()
        execute5.close()
    }
}


val JSON_MEDIATYPE = "application/json;charset=utf-8".toMediaTypeOrNull()

fun main() {

    OkHttps.pviaURL = ""
//
    println(JSON_MEDIATYPE.toString())
    val loginaction = {
        OkHttps.loginForPvia("admin", "12345")
    }
    val needLogin: (t: String) -> Boolean = { it.contains("登录相关") }
    val carCSRFRequestHead = OkHttps.requestCSRFRequestHead(
            "${OkHttps.pviaURL}/ivehicle-web/view/index.do", """name="_csrf"\s+content="(\S+)"""",
            """name="_csrf_header"\s+content="(\S+)"""", loginaction
    )
    val message = OkHttps.httpClient.postJson(
            "${OkHttps.pviaURL}/ivehicle-web/web/vehicle/ds/tabulate-vehicle-passing-data", """
             {
              "data": {
                "nonEmptyRows": false,
                "groupBy": "cross",
                "beginTime": "2020-08-21T00:00:00.000+08:00",
                "endTime": "2020-08-21T23:59:59.999+08:00",
                "interval": "2h",
                "headerType": "hour",
                "crosses": "",
                "originations": "99f165044b764133a37bbddaa1c2f549,76cb871cc60b44449708fb53dde17559"
              },
              "metadata": {
                "pageNo": "",
                "pageSize": ""
              }
            }
       """.trimIndent(), mapOf(carCSRFRequestHead), loginaction
    )
    println(message)
    for (entry in OkHttps.cookieStore) {
        println(entry.key)
        println(entry.value)
    }

}

@JvmOverloads
fun OkHttpClient.postJson(
        url: String,
        json: String,
        headers: Map<String, String>? = null,
        loginaction: (() -> Unit)? = null
): String {
    val toRequestBody = json.trimIndent().trim().toRequestBody(JSON_MEDIATYPE)
    val builder = Request.Builder()
    for ((k, v) in (headers ?: emptyMap())) {
        builder.header(k, v)
    }
    val execute7 = this.newCall(
            builder
                    .url(url)
                    .post(toRequestBody)
                    .build()
    ).executeFroUtf8()

    if (loginaction != null && execute7 == null) {
//        需要登陆
        loginaction.invoke()
        return this.newCall(
                builder
                        .url(url)
                        .post(toRequestBody)
                        .build()
        ).executeFroUtf8().toString()
    }
    return execute7.toString()
}

@JvmOverloads
fun OkHttpClient.postForm(
        url: String,
        form: Map<String, String>? = null,
        headers: Map<String, String>? = null,
        loginaction: (() -> Unit)? = null
): String {
    val builder = Request.Builder()
    for ((k, v) in (headers ?: emptyMap())) {
        builder.header(k, v)
    }
    val body = FormBody.Builder()
    form?.forEach { k, v ->
        body.add(k, v)
    }
    val formBodyLogin = body.build()
    val toString = this.newCall(
            Request.Builder().url(url)
                    .post(formBodyLogin).build()
    ).executeFroUtf8()
    if (loginaction != null && toString == null) {
//        需要登陆
        loginaction.invoke()
        return this.newCall(
                Request.Builder().url(url)
                        .post(formBodyLogin).build()
        ).executeFroUtf8().toString()
    }
    return toString.toString()
}

@JvmOverloads
fun OkHttpClient.get(url: String, headers: Map<String, String>? = null, loginaction: (() -> Unit)? = null): String {
    val builder = Request.Builder()
    for ((k, v) in (headers ?: emptyMap())) {
        builder.header(k, v)
    }
    val execute7 = this.newCall(
            builder
                    .url(url)
                    .build()
    ).executeFroUtf8()
    if (loginaction != null && execute7 == null) {
//        需要登陆
        loginaction.invoke()
        return this.newCall(
                builder
                        .url(url)
                        .build()
        ).executeFroUtf8().toString()
    }
    return execute7.toString()
}

/** 返回null，就需要登陆*/
fun Call.executeFroUtf8(): String? {
    val execute = this.execute()
    if (execute.request.url.toString().contains("portal/cas/login")) return null
    if (execute.code == 403) return null
    val body = execute.body?.string().toString()
    return body
}