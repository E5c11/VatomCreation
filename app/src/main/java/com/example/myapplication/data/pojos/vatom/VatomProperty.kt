package com.example.myapplication.data.pojos.vatom

import com.example.myapplication.data.pojos.GeoPos
import com.google.gson.annotations.SerializedName

data class VatomProperty(

    @SerializedName("parent_id"          ) var parentId          : String?              = null,
    @SerializedName("publisher_fqdn"     ) var publisherFqdn     : String?              = null,
    @SerializedName("root_type"          ) var rootType          : String?              = null,
    @SerializedName("owner"              ) var owner             : String?              = null,
    @SerializedName("author"             ) var author            : String?              = null,
    @SerializedName("template"           ) var template          : String?              = null,
    @SerializedName("template_variation" ) var templateVariation : String?              = null,
    @SerializedName("notify_msg"         ) var notifyMsg         : String?              = null,
    @SerializedName("title"              ) var title             : String?              = null,
    @SerializedName("description"        ) var description       : String?              = null,
    @SerializedName("disabled"           ) var disabled          : Boolean?             = null,
    @SerializedName("category"           ) var category          : String?              = null,
    @SerializedName("tags"               ) var tags              : ArrayList<String>    = arrayListOf(),
    @SerializedName("transferable"       ) var transferable      : Boolean?             = null,
    @SerializedName("acquirable"         ) var acquirable        : Boolean?             = null,
    @SerializedName("tradeable"          ) var tradeable         : Boolean?             = null,
    @SerializedName("transferred_by"     ) var transferredBy     : String?              = null,
    @SerializedName("cloned_from"        ) var clonedFrom        : String?              = null,
    @SerializedName("cloning_score"      ) var cloningScore      : Int?                 = null,
    @SerializedName("in_contract"        ) var inContract        : Boolean?             = null,
    @SerializedName("redeemable"         ) var redeemable        : Boolean?             = null,
    @SerializedName("in_contract_with"   ) var inContractWith    : String?              = null,
//    @SerializedName("commerce"           ) var commerce          : Commerce?            = Commerce(),
//    @SerializedName("states"             ) var states            : ArrayList<States>    = arrayListOf(),
    @SerializedName("resources"          ) var resources         : ArrayList<Resources> = arrayListOf(),
//    @SerializedName("visibility"         ) var visibility        : Visibility?          = Visibility(),
    @SerializedName("num_direct_clones"  ) var numDirectClones   : Int?                 = null,
    @SerializedName("geo_pos"            ) var geoPos            : GeoPos?              = GeoPos(),
    @SerializedName("dropped"            ) var dropped           : Boolean?             = null,
    @SerializedName("age"                ) var age               : Int?                 = null

)
