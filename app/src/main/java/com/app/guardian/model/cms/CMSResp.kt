package com.app.guardian.model.cms

data class CMSResp(
    val about_us: String,
    val contact: List<Contact>,
    val terms_conditions: String
)
data class Contact(
    val label: String,
    val title: String,
    val value: String
)