package org.thoughtcrime.securesms.loki

import android.content.ContentValues
import android.content.Context
import org.thoughtcrime.securesms.database.Database
import org.thoughtcrime.securesms.database.helpers.SQLCipherOpenHelper

/**
 * A database for associating friend request data to Threads
 */
class LokiThreadFriendRequestDatabase(context: Context, helper: SQLCipherOpenHelper) : Database(context, helper) {

    companion object {
        private val tableName = "loki_thread_friend_request_database"
        private val threadId = "_id"
        private val friendRequestStatus = "friend_request_status"

        @JvmStatic
        val createTableCommand = "CREATE TABLE $tableName ($threadId INTEGER PRIMARY KEY, $friendRequestStatus INTEGER DEFAULT 0);"
    }

    fun getFriendRequestStatus(threadId: Long): Int {
        val db = databaseHelper.readableDatabase
        return db.get(tableName, ID_WHERE, arrayOf(threadId.toString())) { cursor ->
            cursor.getInt(friendRequestStatus)
        } ?: LokiFriendRequestStatus.NONE
    }

    fun setFriendRequestStatus(threadId: Long, status: Int) {
        val database = databaseHelper.writableDatabase
        val contentValues = ContentValues(1)
        contentValues.put(Companion.threadId, threadId)
        contentValues.put(friendRequestStatus, status)

        database.insertOrUpdate(tableName, contentValues, ID_WHERE, arrayOf(threadId.toString()))
        notifyConversationListListeners()
    }
}