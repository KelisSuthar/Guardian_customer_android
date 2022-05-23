package com.app.guardian.model.ListFilter

data class FilterResp(
    val age: Age,
    val specialization: List<Specialization>
)
data class Age(
    val `0-1`: String,
    val `1-5`: String,
    val `10-15`: String,
    val `15-100`: String,
    val `5-7`: String,
    val `7-10`: String
)
data class Specialization(
    val id: Int,
    val title: String
)