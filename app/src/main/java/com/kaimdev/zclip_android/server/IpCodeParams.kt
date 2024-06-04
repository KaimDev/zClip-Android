package com.kaimdev.zclip_android.server

data class IpCodeParams(
    val ip: String,
    val code: String
) : Map<String, String> by mapOf(
    "ip" to ip,
    "code" to code
)
