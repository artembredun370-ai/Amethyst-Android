package com.example.launcherui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF1F1F21)
private val PanelC = Color(0xFF2B2B2E)
private val Line = Color(0xFF111113)
private val Lime = Color(0xFFC6F23A)
private val LimeDark = Color(0xFF7DA11A)
private val Green = Color(0xFF2E9B3E)
private val Mute = Color(0xFFA4A4AA)
private val Pixel = FontFamily.Monospace // замініть на pixel-шрифт з res/font

enum class Tab(val label: String) { Home("Головна"), Mods("Моди"), Versions("Версії"), Settings("Опції"), News("Новини") }

/**
 * onPlay викликається при натисканні «ГРАТИ».
 * Саме сюди підключається запуск гри з PojavLauncher/Amethyst.
 */
@Composable
fun LauncherScreen(
    versions: List<String> = listOf("Forge 1.20.1 (47.1.3)", "Fabric 1.20.1", "Vanilla 1.20.1", "Vanilla 1.21"),
    initialNick: String = "Steve_Gamer24",
    onPlay: (nick: String, version: String) -> Unit,
) {
    var tab by remember { mutableStateOf(Tab.Home) }
    var nick by remember { mutableStateOf(initialNick) }
    var version by remember { mutableStateOf(versions.first()) }

    Scaffold(
        containerColor = Bg,
        topBar = { Header() },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF161618)) {
                Tab.values().forEach {
                    NavigationBarItem(
                        selected = tab == it, onClick = { tab = it },
                        icon = {}, label = { Text(it.label, fontFamily = Pixel, fontSize = 11.sp) },
                        alwaysShowLabel = true,
                    )
                }
            }
        },
    ) { pad ->
        Column(Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(bottom = 12.dp)) {
            when (tab) {
                Tab.Home -> HomeTab(nick, { nick = it }, versions, version, { version = it }) { onPlay(nick, version) }
                Tab.Mods -> ListTab("Каталог модів", listOf("Create", "Farmer’s Delight", "Alex’s Mobs", "JEI", "JourneyMap"))
                Tab.Versions -> ListTab("Версії гри", versions)
                Tab.Settings -> ListTab("Налаштування", listOf("Оперативна пам’ять: 3 ГБ", "Повний екран", "Екранні кнопки"))
                Tab.News -> ListTab("Новини", listOf("Нове оновлення Java Edition", "Збірки модів тижня"))
            }
        }
    }
}

@Composable private fun Header() = Row(
    Modifier.fillMaxWidth().background(Color(0xFF161618)).statusBarsPadding().padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    Box(Modifier.size(34.dp).background(Brush.verticalGradient(0.35f to Color(0xFF5FB04A), 0.35f to Color(0xFF7A5230))))
    Spacer(Modifier.width(10.dp))
    Column {
        Text("MINECRAFT", fontFamily = Pixel, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
        Text("LAUNCHER", fontFamily = Pixel, fontWeight = FontWeight.Bold, color = Lime, fontSize = 12.sp)
    }
}

@Composable private fun Panel(title: String, content: @Composable ColumnScope.() -> Unit) = Column(
    Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp).fillMaxWidth()
        .background(PanelC).border(3.dp, Line).padding(12.dp),
) {
    Text(title, color = Mute, fontFamily = Pixel, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    Spacer(Modifier.height(8.dp))
    content()
}

@Composable private fun HomeTab(
    nick: String, onNick: (String) -> Unit,
    versions: List<String>, version: String, onVersion: (String) -> Unit,
    onPlay: () -> Unit,
) {
    // Банер (замініть на Image з res/drawable)
    Box(Modifier.fillMaxWidth().height(180.dp).background(Brush.verticalGradient(listOf(Color(0xFF5AA7E8), Color(0xFFBFE0F8)))))

    Panel("Нікнейм") {
        BasicTextField(
            value = nick, onValueChange = { onNick(it.take(16)) }, singleLine = true,
            textStyle = TextStyle(color = Color.White, fontFamily = Pixel, fontSize = 15.sp),
            cursorBrush = SolidColor(Lime),
            modifier = Modifier.fillMaxWidth().background(Color(0xFF171719)).border(3.dp, Line).padding(12.dp),
        )
    }

    var open by remember { mutableStateOf(false) }
    Panel("Версія") {
        Box {
            Text(version, color = Color.White, fontFamily = Pixel, fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().background(Color(0xFF171719)).border(3.dp, Line)
                    .clickable { open = true }.padding(12.dp))
            DropdownMenu(open, { open = false }) {
                versions.forEach { v -> DropdownMenuItem(text = { Text(v) }, onClick = { onVersion(v); open = false }) }
            }
        }
    }

    Spacer(Modifier.height(12.dp))
    Box(
        Modifier.padding(horizontal = 12.dp).fillMaxWidth().background(Lime)
            .border(BorderStroke(3.dp, Line)).clickable(onClick = onPlay).padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("▶ ГРАТИ", color = Color(0xFF1A2300), fontFamily = Pixel, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("Запустити Minecraft", color = Color(0xFF1A2300), fontFamily = Pixel, fontSize = 11.sp)
        }
    }

    Panel("Нещодавно грали") { Row2("World_Alpha", "Create Modpack") }
    Panel("Популярні моди") {
        Row2("Create", "Механізми й автоматизація"); Row2("Alex’s Mobs", "Нові тварини"); Row2("JEI", "Рецепти в грі")
    }
}

@Composable private fun ListTab(title: String, items: List<String>) = Panel(title) { items.forEach { Row2(it, "") } }

@Composable private fun Row2(name: String, sub: String) = Row(
    Modifier.fillMaxWidth().padding(bottom = 8.dp).background(Color(0xFF232326)).border(3.dp, Line).padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    Box(Modifier.size(30.dp).background(Green))
    Spacer(Modifier.width(10.dp))
    Column {
        Text(name, color = Color.White, fontFamily = Pixel, fontSize = 13.sp)
        if (sub.isNotEmpty()) Text(sub, color = Mute, fontFamily = Pixel, fontSize = 11.sp)
    }
}
