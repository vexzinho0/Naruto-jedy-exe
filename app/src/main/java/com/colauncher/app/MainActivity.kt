package com.colauncher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Forest = Color(0xFF101311)
private val Surface = Color(0xFF1B201C)
private val Lime = Color(0xFFA8E65B)
private val Soft = Color(0xFFDCE7D8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CoLauncherTheme { LauncherApp() } }
    }
}

@Composable
private fun CoLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(primary = Lime, background = Forest, surface = Surface), content = content)
}

@Composable
private fun LauncherApp() {
    var selected by rememberSaveable { mutableStateOf("Sobrevivência") }
    var launching by rememberSaveable { mutableStateOf(false) }
    var showProfiles by remember { mutableStateOf(false) }
    var showSetup by remember { mutableStateOf(false) }
    val profiles = listOf(
        Profile("Sobrevivência", "1.21.4 • Vanilla", "Pronto para iniciar"),
        Profile("Aventura", "1.20.1 • Fabric", "4 mods ativados"),
        Profile("Criativo", "1.21.4 • Vanilla", "Pronto para iniciar")
    )
    Scaffold(containerColor = Forest, bottomBar = { BottomNavigation() }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item { Header() }
            item { HeroCard(selected, launching) { launching = true } }
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("PERFIS", color = Soft, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { showProfiles = true }) { Text("Gerenciar") }
                }
            }
            items(profiles.size) { index ->
                val profile = profiles[index]
                ProfileRow(profile, selected == profile.name) { selected = profile.name }
            }
            item { SetupHint() }
        }
    }
    LaunchedEffect(launching) {
        if (launching) {
            delay(700)
            launching = false
            showSetup = true
        }
    }
    if (showProfiles) ProfileDialog { showProfiles = false }
    if (showSetup) SetupDialog { showSetup = false }
}

@Composable private fun Header() = Row(verticalAlignment = Alignment.CenterVertically) {
    Surface(shape = RoundedCornerShape(14.dp), color = Lime) {
        Icon(Icons.Default.CatchingPokemon, null, modifier = Modifier.padding(10.dp), tint = Forest)
    }
    Spacer(Modifier.width(12.dp))
    Column { Text("CO LAUNCHER", color = Soft, fontWeight = FontWeight.Black, fontSize = 20.sp); Text("Minecraft Java no seu ritmo", color = Color(0xFF9DA99A), fontSize = 12.sp) }
    Spacer(Modifier.weight(1f)); Icon(Icons.Default.Settings, "Configurações", tint = Soft)
}

@Composable private fun HeroCard(profile: String, launching: Boolean, launch: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF263126)), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(22.dp)) {
            Text("JOGAR AGORA", color = Lime, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(10.dp)); Text(profile, color = Soft, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Minecraft Java Edition", color = Color(0xFFB8C3B4))
            Spacer(Modifier.height(22.dp))
            Button(onClick = launch, enabled = !launching, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Forest)) {
                if (launching) { CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Forest, strokeWidth = 2.dp); Spacer(Modifier.width(12.dp)); Text("PREPARANDO...", fontWeight = FontWeight.Bold) }
                else { Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(8.dp)); Text("INICIAR JOGO", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

private data class Profile(val name: String, val version: String, val detail: String)
@Composable private fun ProfileRow(profile: Profile, active: Boolean, choose: () -> Unit) {
    Surface(color = if (active) Color(0xFF263126) else Surface, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = choose)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (active) Icons.Default.CheckCircle else Icons.Default.Inventory2, null, tint = if (active) Lime else Color(0xFF9DA99A))
            Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(profile.name, color = Soft, fontWeight = FontWeight.SemiBold); Text(profile.version + "  ·  " + profile.detail, color = Color(0xFF9DA99A), fontSize = 12.sp) }
            if (active) Text("ATIVO", color = Lime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun SetupHint() = Surface(color = Color(0xFF20281F), shape = RoundedCornerShape(18.dp)) {
    Row(Modifier.padding(16.dp)) { Icon(Icons.Default.Info, null, tint = Lime); Spacer(Modifier.width(12.dp)); Text("Configure um runtime Java compatível e faça login com uma conta Microsoft que possua o jogo antes de iniciar.", color = Soft, fontSize = 13.sp, lineHeight = 19.sp) }
}

@Composable private fun BottomNavigation() = Surface(color = Color(0xFF171B18)) { Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
    NavItem(Icons.Default.Home, "Início", true); NavItem(Icons.Default.Folder, "Arquivos", false); NavItem(Icons.Default.Download, "Downloads", false)
} }
@Composable private fun NavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, active: Boolean) = Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon, label, tint = if (active) Lime else Color(0xFF8B9688)); Text(label, color = if (active) Lime else Color(0xFF8B9688), fontSize = 11.sp) }
@Composable private fun ProfileDialog(close: () -> Unit) = AlertDialog(onDismissRequest = close, confirmButton = { TextButton(close) { Text("CONCLUÍDO") } }, title = { Text("Gerenciar perfis") }, text = { Text("A edição detalhada de versões, memória e argumentos Java será adicionada aqui. Seus jogos e contas permanecem sob seu controle.") })
@Composable private fun SetupDialog(close: () -> Unit) = AlertDialog(onDismissRequest = close, confirmButton = { TextButton(close) { Text("ENTENDI") } }, title = { Text("Configuração necessária") }, text = { Text("Antes de jogar, conecte sua conta Microsoft e selecione um runtime Java compatível nas configurações. O Co Launcher somente usa arquivos de jogo obtidos legalmente.") })
