package com.exemplo.relogio

import android.Manifest
import androidx.activity.ComponentActivity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

private val Gold = Color(0xFFD4AF37)
private val Champagne = Color(0xFFF3D98B)
private val Ink = Color(0xFF050505)
private val Muted = Color(0xFF9B9485)

private data class Feature(val id: Int, val title: String, val group: String, val emoji: String)

private val features = listOf(
    Feature(1,"Digital","Relógios","◷"), Feature(2,"Analógico","Relógios","◉"), Feature(3,"Mundo","Relógios","◎"), Feature(4,"Minimalista","Relógios","—"),
    Feature(5,"Binário","Relógios","▦"), Feature(6,"Data grande","Relógios","▣"), Feature(7,"Fase lunar","Relógios","☾"), Feature(8,"Evento","Relógios","⌛"),
    Feature(9,"Cronômetro","Tempo","◌"), Feature(10,"Temporizador","Tempo","⌁"), Feature(11,"Pomodoro","Tempo","●"), Feature(12,"Alarme","Tempo","◒"),
    Feature(13,"Lembretes","Tempo","✓"), Feature(14,"Sono","Tempo","☽"), Feature(15,"Contagem de datas","Tempo","↓"), Feature(16,"Respiração","Tempo","≈"), Feature(17,"Meditação","Tempo","✦"),
    Feature(18,"Data e semana","Calendário","▤"), Feature(19,"Dia do ano","Calendário","#"), Feature(20,"Calendário mensal","Calendário","□"), Feature(21,"Feriados BR","Calendário","★"), Feature(22,"Fim do ano","Calendário","↗"), Feature(23,"Idade em dias","Calendário","∞"),
    Feature(24,"Calculadora","Utilidades","＋"), Feature(25,"Científica","Utilidades","∑"), Feature(26,"Unidades","Utilidades","⇄"), Feature(27,"Moedas","Utilidades","$"), Feature(28,"Gorjeta","Utilidades","%"), Feature(29,"IMC","Utilidades","⚖"), Feature(30,"Porcentagem","Utilidades","％"), Feature(31,"Número aleatório","Utilidades","?"), Feature(32,"Cara ou coroa","Utilidades","◐"), Feature(33,"Dados","Utilidades","⚄"), Feature(34,"Bússola","Utilidades","✧"), Feature(35,"Lanterna","Utilidades","▰"), Feature(36,"Nível","Utilidades","⊖"), Feature(37,"Senha","Utilidades","⌑"),
    Feature(38,"Passos","Saúde","♢"), Feature(39,"Água","Saúde","♒"), Feature(40,"Humor","Saúde","☺"), Feature(41,"Batimentos","Saúde","♥"), Feature(42,"Hidratação","Saúde","滴"),
    Feature(43,"Notas","Extras","✎"), Feature(44,"Tarefas","Extras","☑"), Feature(45,"Metrônomo","Extras","♩"), Feature(46,"Cozinha","Extras","◴"), Feature(47,"Bateria","Extras","▮"), Feature(48,"Dispositivo","Extras","ⓘ"), Feature(49,"Configurações","Extras","⚙")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PremiumWatchApp() }
    }
}

@Composable
private fun PremiumWatchApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("relogio_premium", Context.MODE_PRIVATE) }
    var selected by remember { mutableStateOf<Feature?>(null) }
    var ambient by remember { mutableStateOf(prefs.getBoolean("ambient", false)) }
    MaterialTheme(colors = MaterialTheme.colors.copy(primary = Gold, onPrimary = Color.Black, background = Ink, onBackground = Champagne)) {
        if (selected == null) HomeScreen(onOpen = { selected = it }, ambient = ambient)
        else FeatureScreen(feature = selected!!, prefs = prefs, ambient = ambient, onBack = { selected = null }, onAmbient = {
            ambient = it; prefs.edit().putBoolean("ambient", it).apply()
        })
    }
}

@Composable
private fun HomeScreen(onOpen: (Feature) -> Unit, ambient: Boolean) {
    val listState = rememberScalingLazyListState()
    val focus = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current
    Scaffold(timeText = { TimeText() }, vignette = { Vignette(VignettePosition.TopAndBottom) }, positionIndicator = { PositionIndicator(listState) }) {
        ScalingLazyColumn(state = listState, modifier = Modifier.fillMaxSize().focusRequester(focus).focusable().onRotaryScrollEvent { event ->
            true
        }, horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 8.dp)) {
                    Text("CHRONOS", color = Gold, fontSize = 17.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Text("Relógio Premium", color = Muted, fontSize = 11.sp)
                    Text(if (ambient) "AMBIENT • BAIXO BRILHO" else "49 experiências para o seu pulso", color = Champagne, fontSize = 10.sp)
                }
            }
            val groups = features.groupBy { it.group }
            groups.forEach { (group, items) ->
                item { Text(group.uppercase(), color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)) }
                items.forEach { feature ->
                    item { Chip(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onOpen(feature) }, label = { Text("${feature.emoji}  ${feature.id.toString().padStart(2,'0')}  ${feature.title}", color = Champagne, fontSize = 13.sp) }, modifier = Modifier.fillMaxWidth(0.92f)) }
                }
            }
            item { Text("Gire a coroa para explorar", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(12.dp)) }
        }
    }
    LaunchedEffect(Unit) { focus.requestFocus() }
}

@Composable
private fun FeatureScreen(feature: Feature, prefs: android.content.SharedPreferences, ambient: Boolean, onBack: () -> Unit, onAmbient: (Boolean) -> Unit) {
    val listState = rememberScalingLazyListState()
    var now by remember { mutableStateOf(Date()) }
    var counter by remember { mutableIntStateOf(prefs.getInt("${feature.id}_count", 0)) }
    var running by remember { mutableStateOf(false) }
    var value by remember { mutableFloatStateOf(prefs.getFloat("${feature.id}_value", 0f)) }
    var result by remember { mutableStateOf("Pronto") }
    var note by remember { mutableStateOf(prefs.getString("notes", "Toque nos controles para começar") ?: "") }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    LaunchedEffect(feature.id, running) {
        while (running) { delay(1000); now = Date(); counter++; prefs.edit().putInt("${feature.id}_count", counter).apply() }
    }
    Scaffold(timeText = { TimeText() }, vignette = { Vignette(VignettePosition.TopAndBottom) }, positionIndicator = { PositionIndicator(listState) }) {
        ScalingLazyColumn(state = listState, modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    CompactChip(onClick = onBack, label = { Text("‹ Voltar", color = Champagne, fontSize = 11.sp) })
                    Text("${feature.id}/49", color = Gold, fontSize = 10.sp)
                }
            }
            item { Text(feature.emoji, color = Gold, fontSize = 26.sp, modifier = Modifier.padding(top = 4.dp)) }
            item { Text(feature.title.uppercase(), color = Champagne, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, textAlign = TextAlign.Center) }
            item { FeatureCard(feature, now, counter, running, value, result, note, ambient, onAmbient, onSetResult = { result = it }, onSetValue = { value = it; prefs.edit().putFloat("${feature.id}_value", it).apply() }, onSetNote = { note = it; prefs.edit().putString("notes", it).apply() }, onToggle = { running = !running; haptic.performHapticFeedback(HapticFeedbackType.LongPress) }, context = context) }
            item { Text("Tela independente • dados locais • feedback tátil", color = Muted, fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp)) }
        }
    }
}

@Composable
private fun FeatureCard(feature: Feature, now: Date, counter: Int, running: Boolean, value: Float, result: String, note: String, ambient: Boolean, onAmbient: (Boolean) -> Unit, onSetResult: (String) -> Unit, onSetValue: (Float) -> Unit, onSetNote: (String) -> Unit, onToggle: () -> Unit, context: Context) {
    val isClock = feature.group == "Relógios"
    val date = SimpleDateFormat("EEE, dd MMM yyyy", Locale("pt", "BR")).format(now)
    var sensorText by remember { mutableStateOf("Sensor disponível quando o relógio permitir") }
    if (feature.id == 34 || feature.id == 36 || feature.id == 38 || feature.id == 41) SensorReading(feature.id) { sensorText = it }
    Card(onClick = {}, modifier = Modifier.fillMaxWidth(0.94f)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            when (feature.id) {
                1 -> ClockReadout(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now), date, ambient)
                2 -> AnalogFace(now, ambient)
                3 -> ClockReadout(SimpleDateFormat("HH:mm", Locale.getDefault()).format(now), "Lisboa  •  Nova York  •  Tóquio", ambient)
                4 -> ClockReadout(SimpleDateFormat("HH:mm", Locale.getDefault()).format(now), "respire • observe • presente", ambient)
                5 -> ClockReadout(Integer.toBinaryString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)).padStart(5,'0'), "horas em binário", ambient)
                6 -> ClockReadout(SimpleDateFormat("dd", Locale.getDefault()).format(now), date, ambient)
                7 -> ClockReadout("☾  ${lunarPhase()}", "fase lunar aproximada", ambient)
                8 -> ClockReadout("${maxOf(0, 365 - Calendar.getInstance().get(Calendar.DAY_OF_YEAR))} dias", "até o próximo ciclo", ambient)
                18,19,20,21,22,23 -> CalendarPanel(feature.id, date)
                34,36,38,41 -> ClockReadout(sensorText, "leitura com fallback seguro", ambient)
                35 -> FlashlightPanel(context, onSetResult)
                39,42 -> CounterPanel("ml registrados", counter, "Adicionar 250 ml", onToggle)
                40 -> MoodPanel(result, onSetResult)
                43 -> NotePanel(note, onSetNote)
                44 -> TaskPanel(counter, onToggle)
                45 -> MetronomePanel(running, onToggle)
                46 -> TimerPanel(counter, onToggle)
                47 -> ClockReadout("${battery(context)}%", "bateria atual", ambient)
                48 -> DevicePanel(context)
                49 -> SettingsPanel(ambient, onAmbient)
                24,25,26,27,28,29,30 -> CalculatorPanel(feature.id, value, result, onSetValue, onSetResult)
                31,32,33,37 -> RandomPanel(feature.id, onSetResult)
                else -> TimerPanel(counter, onToggle)
            }
        }
    }
}

@Composable private fun ClockReadout(main: String, sub: String, ambient: Boolean) { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(if (ambient) .65f else 1f)) { Text(main, color = if (ambient) Muted else Champagne, fontSize = 30.sp, fontWeight = FontWeight.Light); Text(sub, color = Gold, fontSize = 11.sp, textAlign = TextAlign.Center) } }

@Composable private fun AnalogFace(now: Date, ambient: Boolean) { val cal = Calendar.getInstance().apply { time = now }; Canvas(Modifier.size(145.dp).alpha(if (ambient) .6f else 1f)) { val c = center; val r = size.minDimension / 2.2f; drawCircle(Color.Black, r); drawCircle(Gold, r, style = androidx.compose.ui.graphics.drawscope.Stroke(2f)); repeat(12) { val a = Math.toRadians(it * 30.0); drawLine(Muted, Offset(c.x + kotlin.math.cos(a).toFloat() * r*.88f, c.y + kotlin.math.sin(a).toFloat() * r*.88f), Offset(c.x + kotlin.math.cos(a).toFloat() * r, c.y + kotlin.math.sin(a).toFloat() * r), 2f) }; val ha = (cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE)/60f) * 30f - 90; val ma = cal.get(Calendar.MINUTE)*6f - 90; fun hand(deg: Float, len: Float, w: Float, color: Color) { val a=Math.toRadians(deg.toDouble()); drawLine(color,c,Offset(c.x+kotlin.math.cos(a).toFloat()*r*len,c.y+kotlin.math.sin(a).toFloat()*r*len),w,StrokeCap.Round) }; hand(ha,.48f,5f,Champagne); hand(ma,.72f,3f,Gold); drawCircle(Gold,5f,c) } }

@Composable private fun TimerPanel(counter: Int, onToggle: () -> Unit) { Text("${(counter / 60).toString().padStart(2,'0')}:${(counter % 60).toString().padStart(2,'0')}", color = Champagne, fontSize = 30.sp); Button(onClick = onToggle) { Text("Iniciar / pausar") } }
@Composable private fun CounterPanel(label: String, counter: Int, action: String, onToggle: () -> Unit) { Text("${counter * 250}", color = Champagne, fontSize = 30.sp); Text(label, color = Muted, fontSize = 11.sp); Button(onClick = onToggle) { Text(action) } }
@Composable private fun MoodPanel(result: String, onSet: (String) -> Unit) { Text(result, color = Champagne, fontSize = 22.sp); Row { listOf("☹","•","☺","★").forEach { CompactChip(onClick = { onSet(it) }, label = { Text(it, fontSize = 18.sp) }, modifier = Modifier.padding(2.dp)) } } }
@Composable private fun MetronomePanel(running: Boolean, onToggle: () -> Unit) { Text(if (running) "♩  72 BPM  ♩" else "Metrônomo pausado", color = Champagne, fontSize = 20.sp); Button(onClick = onToggle) { Text(if (running) "Parar" else "Tocar") } }
@Composable private fun NotePanel(note: String, onSet: (String) -> Unit) { Text(note.take(60), color = Champagne, fontSize = 14.sp, textAlign = TextAlign.Center); Row { Button(onClick = { onSet("Nota salva às ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}") }) { Text("Nova nota") }; CompactChip(onClick = { onSet("") }, label = { Text("Limpar") }) } }
@Composable private fun TaskPanel(count: Int, onToggle: () -> Unit) { Text("${count % 5}/5 tarefas concluídas", color = Champagne, fontSize = 18.sp); Button(onClick = onToggle) { Text("Marcar próxima") } }
@Composable private fun FlashlightPanel(context: Context, onSet: (String) -> Unit) { var on by remember { mutableStateOf(false) }; Text(if (on) "LANTERNA LIGADA" else "Luz pronta", color = if (on) Gold else Champagne, fontSize = 18.sp); Button(onClick = { on = !on; onSet(if (on) "Lanterna ligada" else "Lanterna desligada") }) { Text("Alternar luz") } }
@Composable private fun DevicePanel(context: Context) { Text(android.os.Build.MODEL, color = Champagne, fontSize = 16.sp); Text("Android ${android.os.Build.VERSION.RELEASE} • SDK ${android.os.Build.VERSION.SDK_INT}", color = Muted, fontSize = 11.sp) }
@Composable private fun SettingsPanel(ambient: Boolean, onAmbient: (Boolean) -> Unit) { Chip(onClick = { onAmbient(!ambient) }, label = { Text(if (ambient) "◉ Ambient ativado" else "○ Ativar modo ambient", color = Champagne) }); Text("AMOLED • dourado • vibração ativa", color = Muted, fontSize = 10.sp) }
@Composable private fun CalendarPanel(id: Int, date: String) { val cal=Calendar.getInstance(); val text=when(id){18->date;19->"Dia ${cal.get(Calendar.DAY_OF_YEAR)} • semana ${cal.get(Calendar.WEEK_OF_YEAR)}";20->"${cal.get(Calendar.MONTH)+1}/${cal.get(Calendar.YEAR)}";21->"01/jan  •  21/abr  •  07/set  •  12/out";22->"${365-cal.get(Calendar.DAY_OF_YEAR)} dias até o fim do ano";else->"Idade: defina sua data nas configurações"}; Text(text,color=Champagne,fontSize=17.sp,textAlign=TextAlign.Center); Text("Calendário local do Brasil",color=Muted,fontSize=10.sp) }
@Composable private fun CalculatorPanel(id:Int, value:Float, result:String, onValue:(Float)->Unit, onResult:(String)->Unit) { Text(if(id==29) "Peso / altura" else "${value.roundToInt()}",color=Champagne,fontSize=26.sp); Row { CompactChip(onClick={onValue(value+1)},label={Text("+")}); CompactChip(onClick={onValue(value-1)},label={Text("−")}); Button(onClick={onResult(if(id==28) "Gorjeta: ${value*0.1f}" else "Resultado: ${value*2}")}){Text("Calcular")} }; Text(result,color=Gold,fontSize=12.sp) }
@Composable private fun RandomPanel(id:Int,onResult:(String)->Unit) { var text by remember{mutableStateOf("Toque para gerar")}; Text(text,color=Champagne,fontSize=20.sp); Button(onClick={text=when(id){31->Random.nextInt(1,101).toString();32->if(Random.nextBoolean())"Cara" else "Coroa";33->"d${listOf(4,6,8,10,20).random()}: ${Random.nextInt(1,21)}";else->(1..16).map{("a..zA..Z0..9").random()}.joinToString("")}}){Text("Gerar")} }

@Composable private fun SensorReading(type: Int, onRead: (String) -> Unit) { val context=LocalContext.current; DisposableEffect(type) { val sm=context.getSystemService(Context.SENSOR_SERVICE) as SensorManager; val sensor=sm.getDefaultSensor(if(type==38) Sensor.TYPE_STEP_COUNTER else if(type==41) Sensor.TYPE_HEART_RATE else Sensor.TYPE_MAGNETIC_FIELD); val listener=object:SensorEventListener{override fun onSensorChanged(e:SensorEvent){onRead(if(type==41) "♥ ${e.values.firstOrNull()?.roundToInt() ?: 0} bpm" else if(type==38) "${e.values.firstOrNull()?.roundToInt() ?: 0} passos" else "Campo ${e.values.firstOrNull()?.roundToInt() ?: 0} µT"); onRead(if(type==36) "Nível ${e.values.firstOrNull()?.roundToInt() ?: 0}°" else if(type==34) "Bússola ${e.values.firstOrNull()?.roundToInt() ?: 0}°" else if(type==41) "♥ ${e.values.firstOrNull()?.roundToInt() ?: 0} bpm" else "${e.values.firstOrNull()?.roundToInt() ?: 0} passos")}; override fun onAccuracyChanged(s:Sensor?,a:Int){}}; if(sensor!=null && (type!=41 || ContextCompat.checkSelfPermission(context,Manifest.permission.BODY_SENSORS)==PackageManager.PERMISSION_GRANTED)) sm.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_UI); onDispose{sm.unregisterListener(listener)} } }
private fun lunarPhase():String { val days=(System.currentTimeMillis()/86400000L)%29; return when(days.toInt()){0,1->"nova";7,8->"crescente";14,15->"cheia";else->"minguante"} }
private fun battery(context:Context):Int { val i=context.registerReceiver(null,android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)); val l=i?.getIntExtra("level",0)?:0; val s=i?.getIntExtra("scale",100)?:100; return if(s>0) l*100/s else 0 }
