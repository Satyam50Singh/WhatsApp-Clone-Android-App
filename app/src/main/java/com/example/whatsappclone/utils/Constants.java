package com.example.whatsappclone.utils;

public interface Constants {
    String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    int RC_SIGN_IN = 65;
    String DB_PATH = "https://whatsapp-clone-2511-default-rtdb.europe-west1.firebasedatabase.app";
    String USER_COLLECTION_NAME = "Users";
    String CHAT_COLLECTION_NAME = "Chats";
    String GROUP_CHAT_COLLECTION_NAME = "Group Chats";
    String STARRED_MESSAGES_COLLECTION_NAME = "Starred Messages";
    String PRESENCE_COLLECTION_NAME = "Presence";
    String USER_STATUS_COLLECTION_NAME = "User Status";
    String STATUSES_COLLECTION_NAME = "statuses";
    String FILE_TYPE = "image/*";
}
