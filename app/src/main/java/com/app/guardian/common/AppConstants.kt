package com.app.guardian.common

object AppConstants {


    val BROADCAST_REC_INTENT = "PUSH"
    val BROADCAST_ADD_VIDEO = "com.guardian.uploadVideos"
    var latitude = 0.0
    var longitude = 0.0
    const val Poppins_Light = 1
    const val Poppins_Regular = 2
    const val Poppins_Medium = 3
    const val Poppins_SemiBold = 4
    const val Poppins_Bold = 5

    const val LOG_DEBUG = true
    val USER = "User"
    val MEDIATOR = "Mediator"
    val LAWYEER = "Lawyer"
    val APP_NAME = "Guardian"
    val APP_ROLE_USER = "user"
    val APP_ROLE_LAWYER = "lawyer"
    val APP_ROLE_MEDIATOR = "mediator"
    val EXTRA_TODAY = "Today"
    val EXTRA_YESTERDAY = "Yesterday"
    const val ONLINE = "ONLINE"
    const val OFFLINE = "OFFLINE"
    const val SKIP_INTRO = "SKIP_INTRO"
    const val LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID"
    val API_KEY_VALUE = "API_KEY_VALUE"
    const val ACCESS_TOKEN = "ACCESS_TOKEN"
    const val BEREAR_TOKEN = "BEREAR_TOKEN"
    const val EXTRA_YOU_CAN_ORDER_ONE = "You can order from one restaurant at a time"
    const val USER_ROLE = "USER_ROLE"
    const val IS_SUBSCRIBE = "IS_SUBSCRIBE"
    const val IS_LOGIN = "IS_LOGIN"
    const val IS_CHANGE_PASS = "IS_CHANGE_PASS"
    const val IS_EDIT = "IS_EDIT"
    const val IS_WALKTHROUGH_VIEWED = "IS_WALKTHROUGH_VIEWED"
    const val USER_DETAIL_LOGIN = "USER_DETAIL_LOGIN"
    const val EXTRA_PARAM_PAGE_POSITION = "EXTRA_PARAM_PAGE_POSITION"
    const val STATIC_API_KEY = "335a559c51b88b4752b8325980abb3c14e1c4e61"
    const val EXTRA_OTP = "OTP"
    const val EXTRA_EMAIL_PHONE = "EMAIL_PHONE"
    const val EXTRA_CCP = "CCP"
    const val EXTRA_USER_ID = "USER_ID"
    const val CMS_DETAIL = "CMS_DETAILS"
    const val EXTRA_IS_EMAIL = "IS_EMAIL"
    const val EXTRA_PHONE = "phone"
    const val EXTRA_EMAIL = "email"
    const val EXTRA_PATH = "PATH"
    const val IS_NOTIFICATION = "IS_NOTIFICATION"
    const val EXTRA_NOTIFICATION_DATA_TYPE = "NOTFICAITON_DATA"
    const val EXTRA_NOTIFICATION_DATA_ID = "NOTFICAITON_DATA_ID"
    const val IS_LOGIN_ONCE = "IS_LOGIN_ONCE"
    const val IS_OFFLINE_VIDEO_UPLOAD = "IS_ONLINE_VIDEO_UPLOAD"

    const val MONTHLY_PLAN = "Monthly Plan"
    const val ANNUAL_PLAN = "Annual Plan"

    const val REQ_GET = "request_get"
    const val REQ_SEND = "request_send"

    const val MULTIPLE_CALLS = "MULTIPLE_CALLS"
    const val IS_NOTIFICATION_SHARED_TYPE = "IS_NOTIFICATION_SHARED_TYPE"
    const val IS_NOTIFICATION_SHARED_ID = "IS_NOTIFICATION_SHARED_ID"

    const val EXTRA_SH_USER_HOME = "USERHOME"
    const val EXTRA_SH_MEDIATOR_HOME = "MEDIATORHOME"
    const val EXTRA_SH_LAWYER_HOME = "LAWYERHOME"
    const val EXTRA_SH_RECORD_POLICE_INTERACTION = "RECORD_POLIC_EINTERRACTION"
    const val EXTRA_SH_RECORD_POLICE_INTERACTION_2 = "RECORD_POLIC_EINTERRACTION_2"
    const val EXTRA_SH_LIVE_VIRTUAL_WITNESS = "LIVE_VIRTUAL_WITNESS"
    const val EXTRA_SH_SCHEDUAL_VIRTUAL_WITNESS = "SCHEDUAL_VIRTUAL_WITNESS"
    const val EXTRA_SH_CONTACT_SUPPORT = "CONTACT_SUPPORT"
    const val EXTRA_SH_SUPPORT_GROUP_LIST = "SUPPORT_GROUP_LIST"

    const val EXTRA_VIDEOCALL_RESP = "VIDEO_CALL_RESP"
    const val EXTRA_CALLING_HISTORY_ID = "CALLING_HISTORY_ID"
    const val EXTRA_TO_ID = "TO_ID"
    const val EXTRA_TO_ROLE = "TO_ROLE"
    const val EXTRA_SCHEDUAL_DATE_TIME = "SCHEDUALE_DATE_TIME"
    const val EXTRA_URL = "URL"
    const val EXTRA_ROOM_ID = "ROOM_ID"
    const val EXTRA_NAME = "NAME"
    const val IS_JOIN = "IS_JOIN"

    const val EXTRA_VIRTUAL_WITNESS_PAYLOAD = "virtual_witness_request"
    const val EXTRA_VIDEOCALLREQ_PAYLOAD = "video_call_request"
    const val EXTRA_MEDIATOR_PAYLOAD = "mediator_request"
    const val EXTRA_CHAT_MESSAGE_PAYLOAD = "chat_message"

    const val VIDEO = "video"
    const val TXT = "text"

    const val NOTIFICATION_BAGE = "NOTIFICATION_BAGE"

    const val CITY_STATE = "CITY_STATE"
    const val CITY = "CITY"
    const val STATE = "STATE"
    const val EXTRA_IS_USER = "IS_USER"
    const val EXTRA_IS_LAWYER = "IS_LAWYER"
    const val EXTRA_IS_MEDIATOR = "IS_MEDIAOR"
    const val IS_FROM_SETTINGS: Boolean = false
    const val EXTRA_PHOTO_RADAR = "photo_radar"
    const val EXTRA_ROAD_BLOCK = "road_block"
    const val EXTRA_ORDER_PENDING = "Pending"
    const val EXTRA_ORDER_CANCELED = "Cancelled"
    const val EXTRA_ORDER_PREPARING = "Preparing"
    const val EXTRA_ORDER_PREPARED = "Prepared"
    const val EXTRA_ORDER_PICKED_UP = "Picked-up"
    const val EXTRA_ORDER_COMPLETED = "Completed"
    const val EXTRA_MEALPLAN = "Meal Plan"
    const val EXTRA_REGULAR = "Regular Item"
    const val EXTRA_RESTAURANT = "Restaurant"
    const val EXTRA_GROCERIES = "Groceries"
    const val EXTRA_GROCERY = "Grocery"
    const val EXTRA_DRINKS = "Drinks"
    const val EXTRA_ACCOMODATION = "Accommodation"
    const val EXTRA_JOBS = "Jobs"
    const val EXTRA_JOBS_INTERNSHIPS = "Jobs / Internships"
    const val EXTRA_ME = "Me"
    const val EXTRA_OTHER = "Other"
    const val EXTRA_OTHERS = "Others"
    const val EXTRA_ITEM_ADDED = "Item added in cart successfully"
    const val EXTRA_ITEM_REMOVED = "Item removed successfully"
    const val EXTRA_FINE_PERMISSION = 100
    const val EXTRA_COURSE_PERMISSION = 101
    const val EXTRA_STORAGE_PERMISSION = 102
    const val EXTRA_CALL_PERMISSION = 103
    const val EXTRA_CAMERA_PERMISSION = 104
    const val EXTRA_READ_PERMISSION = 105
    const val EXTRA_WRITE_PERMISSION = 106
    const val EXTRA_CORSE_LOC_PERMISSION = 107
    const val EXTRA_FINE_LOC_PERMISSION = 108
    const val EXTRA_READ_STORAGE_PERMISSION = 109
    const val EXTRA_WRITE_STORAGE_PERMISSION = 110
    const val EXTRA_ACCESS_LOCATION_PERMISSION = 111
    const val EXTRA_MANAGE_LOCATION_PERMISSION = 111
    const val GET_CAT = "GET_CATEGORY"
    const val CARD_DETAILS = "CARD_DETAILS"
    const val EXTRA_SELECT = "Select"
    const val EXTRA_MARTIAL_STATUS = "Marital Status"
    const val EXTRA_LAT = "Latitude"
    const val EXTRA_LONG = "Longitude"
    const val EXTRA_RESTAURANTS = "Restaurants"
    const val EXTRA_BEER_AND_ALCOHOL = "Beer & Alcohol"
    const val EXTRA_ACCOMOIDATIONS = "Accommodations"
    const val EXTRA_ORDER_PLACE_SUCCESSFULLY = "Order place successfully"
    const val FILTER_ITEM = "FILTER_ITEM"
    const val EXTRA_YOUR_PROPERTY_SUCCESSFULLY_ADDED = "Your Property \n Successfully Added "
    const val EXTRA_YOUR_PROPERTY_SUCCESSFULLY_UPDATED = "Your Property \n Successfully Updated "
    const val EXTRA_PROPERTY_BOOKED_SUCCESSFULLY = "Your Property \n" +
            "Successfully booked"
    const val EXTRA_PAID = "Paid"
    const val EXTRA_ACCEPTED = "Accepted"
    const val EXTRA_REJECTED = "Rejected"
    const val ORDER_TYPE = "ORDER_TYPE"
    const val ACCOMODATION_TYPE = "ACCOMODATION_TYPE"
    const val ORDER_CONFIRM = "Order Confirmed"
    const val ORDER_PICKED_UP = "Order Picked-up"
    const val ORDER_PREPARED = "Order Prepared"
    const val ORDER_COMPLETED = "Order Completed"
    const val ORDER_CANCELLED = "Order Cancelled"
    const val NEW_PROPERTY_BOOKED = "New Property Booked"
    const val ACCOMODATION_ACCEPTED = "Renter request Accepted"
    const val ACCOMODATION_REJECTED = "Renter request Rejected"
    const val HOME_NOTIFICATION = "Home Notification"


}