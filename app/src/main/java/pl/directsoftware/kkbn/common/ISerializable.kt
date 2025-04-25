package pl.directsoftware.kkbn.common

import org.json.JSONObject

interface ISerializable {
    fun serialize(): String
    fun toJSONObject(): JSONObject = JSONObject()
}