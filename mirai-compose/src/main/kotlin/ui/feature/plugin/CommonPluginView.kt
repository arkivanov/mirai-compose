package com.youngerhousea.miraicompose.ui.feature.plugin

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.Router
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.router
import com.arkivanov.decompose.statekeeper.Parcelable
import com.youngerhousea.miraicompose.utils.Component
import com.youngerhousea.miraicompose.utils.asComponent
import net.mamoe.mirai.console.plugin.Plugin

class CommonPlugin(
    componentContext: ComponentContext,
    val plugin: Plugin,
) : ComponentContext by componentContext {

    sealed class Setting : Parcelable {
        object Description : Setting()
    }

    private val router: Router<Setting, Component> = router(
        initialConfiguration = Setting.Description,
        handleBackButton = true,
        key = "PluginDetailedRouter",
        childFactory = { configuration, componentContext ->
            when (configuration) {
                is Setting.Description ->
                    DetailedDescription(componentContext, plugin).asComponent { DetailedDescriptionUi(it) }
            }
        }
    )

    val state get() = router.state

}

@Composable
fun CommonPluginUi(commonPlugin: CommonPlugin) {
    Children(commonPlugin.state) { child ->
        child.instance()
    }
}
