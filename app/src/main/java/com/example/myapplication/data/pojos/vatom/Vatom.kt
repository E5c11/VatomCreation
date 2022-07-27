package com.example.myapplication.data.pojos.vatom

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class Vatom (

    @SerializedName("id") val id: String,
    @SerializedName("when_created") val whenCreated: String,
    @SerializedName("when_modified") var whenModified: String,
    @SerializedName("when_added") var whenAdded: String,
    @SerializedName("vAtom::vAtomType") val property: VatomProperty,
    @SerializedName("private") val private: JSONObject?,
    @SerializedName("sync") val sync: Int,
//    @SerializedName("faces") var faces: List<Face>,
//    @SerializedName("actions") var actions: List<Action>
)