package io.getstream.chat.sample.application

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import com.getstream.sdk.chat.Chat
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.sample.BuildConfig
import io.getstream.chat.sample.R
import io.getstream.chat.sample.data.dataModule
import loginModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    private val appConfig: AppConfig by inject()

    override fun onCreate() {
        super.onCreate()
        DebugMetricsHelper().init()
        initKoin()
        Chat.Builder(appConfig.apiKey, this).apply {
            offlineEnabled = true
            val notificationConfig =
                NotificationConfig(
                    firebaseMessageIdKey = "message_id",
                    firebaseChannelIdKey = "channel_id",
                    firebaseChannelTypeKey = "channel_type",
                    smallIcon = R.drawable.ic_chat_bubble
                )
            notificationHandler = SampleNotificationHandler(this@App, notificationConfig)
        }.build()

        configFlipper()
    }

    private fun Application.configFlipper() {
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            AndroidFlipperClient.getInstance(this).apply {
                addPlugin(InspectorFlipperPlugin(this@App, DescriptorMapping.withDefaults()))
                addPlugin(DatabasesFlipperPlugin(this@App))
                addPlugin(NetworkFlipperPlugin())
            }.start()
        }
    }

    private fun initKoin() {
        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }
            androidContext(this@App)

            // see crash/bug here: https://github.com/InsertKoinIO/koin/issues/871
            koin.loadModules(
                listOf(
                    appModule,
                    dataModule,
                    loginModule
                )
            )
            koin.createRootScope()
        }
    }
}
