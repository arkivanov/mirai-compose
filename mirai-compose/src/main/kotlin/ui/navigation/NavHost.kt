package com.youngerhousea.miraicompose.ui.navigation

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent
import com.arkivanov.decompose.push
import com.arkivanov.decompose.router
import com.arkivanov.decompose.statekeeper.Parcelable
import com.youngerhousea.miraicompose.MiraiCompose
import com.youngerhousea.miraicompose.console.MiraiComposeLogger
import com.youngerhousea.miraicompose.model.ComposeBot
import com.youngerhousea.miraicompose.theme.ComposeSetting
import com.youngerhousea.miraicompose.theme.ResourceImage
import com.youngerhousea.miraicompose.ui.common.SelectEdgeText
import com.youngerhousea.miraicompose.ui.common.Spacer
import com.youngerhousea.miraicompose.ui.feature.about.About
import com.youngerhousea.miraicompose.ui.feature.bot.BotV
import com.youngerhousea.miraicompose.ui.feature.log.Log
import com.youngerhousea.miraicompose.ui.feature.plugin.PluginV
import com.youngerhousea.miraicompose.ui.feature.setting.Setting
import com.youngerhousea.miraicompose.utils.Component
import kotlinx.coroutines.cancel
import net.mamoe.mirai.console.plugin.PluginManager


fun MiraiComposeView() =
    Window(
        title = "",
        size = IntSize(1280, 768),
        icon = ResourceImage.icon,
        onDismissRequest = {
            MiraiCompose.cancel()
        }
    ) {
        rememberRootComponent { componentContext ->
            NavHost(componentContext)
        }.render()
    }


class NavHost(
    component: ComponentContext,
) : Component, ComponentContext by component {

    sealed class Config : Parcelable {
        object Bot : Config()
        object Setting : Config()
        object About : Config()
        object Log : Config()
        object Plugin : Config()
    }

    private val router = router<Config, Component>(
        initialConfiguration = Config.Bot,
        handleBackButton = true,
        componentFactory = { config, componentContext ->
            when (config) {
                is Config.Bot ->
                    BotV(
                        componentContext,
                        model = ComposeBot.instances
                    )
                is Config.Setting ->
                    Setting(
                        componentContext,
                    )
                is Config.About ->
                    About(
                        componentContext,
                    )
                is Config.Log ->
                    Log(
                        componentContext,
                        loggerStorage = MiraiComposeLogger.loggerStorage,
                        logger = MiraiComposeLogger.out
                    )
                is Config.Plugin -> {
                    PluginV(
                        componentContext,
                        plugins = PluginManager.plugins
                    )
                }
            }
        },
    )

    @Composable
    override fun render() {
        DesktopMaterialTheme(
            colors = ComposeSetting.AppTheme.themeColors.materialLight
        ) {
            Children(
                router.state,
            ) { child, config ->
                Row {
                    Edge(config)
                    child.render()
                }
            }
        }
    }


    @Composable
    private fun Edge(config: Config) {
        Column(
            Modifier
                .requiredWidth(160.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colors.primary),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer()
            SelectEdgeText(
                "机器人",
                isWishWindow = config is Config.Bot
            ) {
                router.push(Config.Bot)
            }
            SelectEdgeText(
                "插件",
                isWishWindow = config is Config.Plugin
            ) {
                router.push(Config.Plugin)
            }
            SelectEdgeText(
                "设置",
                isWishWindow = config is Config.Setting
            ) {
                router.push(Config.Setting)
            }

            SelectEdgeText(
                "日志",
                isWishWindow = config is Config.Log
            ) {
                router.push(Config.Log)
            }
            SelectEdgeText(
                "关于",
                isWishWindow = config is Config.About
            ) {
                router.push(Config.About)
            }
        }
    }
}
