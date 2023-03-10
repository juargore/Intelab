package com.intelab.joblab.presentation.services

import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.intelab.joblab.presentation.base.utils._notificationAction
import com.intelab.joblab.presentation.base.utils._notificationMessage
import com.intelab.joblab.presentation.base.utils._notificationTitle

class PushHuaweiListenerService : HmsMessageService() {
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        if (message != null) {
            val map = message.dataOfMap
            val title = map[_notificationTitle]
            val msg = map[_notificationMessage]
            val action = map[_notificationAction]

            NotificationBuilder(applicationContext).setUpNotification(title, msg, action)
        }
    }
}
