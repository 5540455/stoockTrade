package com.example.investmentledger

import android.os.Bundle
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Trade(val asset: String, val type: String, val amount: Double, val date: String, val fee: Double = 0.0)
private const val PREFS = "investment_ledger"
private const val TRADES = "trades"

private fun loadTrades(context: Context): List<Trade> {
    val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(TRADES, null)
        ?: return listOf(Trade("易方达沪深300", "申购", 5000.0, "今天 09:42"), Trade("招商银行", "卖出", 8460.0, "昨天 14:36"))
    return runCatching {
        val json = JSONArray(raw)
        List(json.length()) { i ->
            val item = json.getJSONObject(i)
            Trade(item.getString("asset"), item.getString("type"), item.getDouble("amount"), item.getString("date"), item.optDouble("fee", 0.0))
        }
    }.getOrDefault(emptyList())
}

private fun saveTrades(context: Context, trades: List<Trade>) {
    val json = JSONArray().apply { trades.forEach { put(JSONObject().apply { put("asset", it.asset); put("type", it.type); put("amount", it.amount); put("date", it.date); put("fee", it.fee) }) } }
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(TRADES, json.toString()).apply()
}

class MainActivity : ComponentActivity() { override fun onCreate(b: Bundle?) { super.onCreate(b); setContent { InvestmentLedgerApp() } } }

@Composable fun InvestmentLedgerApp() { val context = androidx.compose.ui.platform.LocalContext.current; var tab by remember { mutableIntStateOf(0) }; val trades = remember { mutableStateListOf<Trade>().apply { addAll(loadTrades(context)) } }; MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF3767E8))) { Scaffold(bottomBar = { NavigationBar { listOf("首页" to "⌂", "记账" to "↕", "持仓" to "◒", "分析" to "☷", "我的" to "⋯").forEachIndexed { i, item -> NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = { Text(item.second, fontSize = 22.sp) }, label = { Text(item.first) }) } } }) { p -> when(tab) { 0 -> Home(padding = p, trades = trades); 1 -> Ledger(padding = p, trades = trades) { trades.add(it); saveTrades(context, trades) }; 2 -> Holdings(p, trades); 3 -> Analysis(p, trades); else -> Profile(p) } } } }

@Composable fun Home(padding: PaddingValues, trades: List<Trade>) { val cash = 50000.0 + trades.sumOf { if (it.type == "卖出") it.amount - it.fee else -it.amount - it.fee }; val invested = trades.filter { it.type != "卖出" }.sumOf { it.amount }; val annual = xirr(trades, cash + invested); LazyColumn(contentPadding = PaddingValues(bottom = 90.dp), modifier = Modifier.padding(padding)) { item { Header(cash + invested) }; item { Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Metric("累计投入", money(invested), false); Metric("可用现金", money(cash), false) }; Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Metric("交易笔数", trades.size.toString(), false); Metric("手续费", money(trades.sumOf { it.fee }), false) }; Text("年化收益率", fontWeight = FontWeight.Bold, fontSize = 17.sp); Card { Column(Modifier.padding(16.dp)) { Text("资金加权年化（XIRR）", color = Color.Gray, fontSize = 12.sp); Text(annual?.let { String.format("%.2f%%", it * 100) } ?: "待补充有效现金流", fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("根据交易记录和当前资产估算", color = Color(0xFF16A673)) } }; Text("最近记录", fontWeight = FontWeight.Bold, fontSize = 17.sp); trades.takeLast(10).reversed().forEach { TradeRow(it) } } } } }
private fun money(value: Double) = String.format("%,.2f", value)
private fun xirr(trades: List<Trade>, terminal: Double): Double? {
    if (trades.isEmpty() || terminal <= 0.0) return null
    val today = LocalDate.now()
    val flows = trades.map { (if (it.type == "卖出") it.amount - it.fee else -(it.amount + it.fee)) to today } + (terminal to today)
    if (flows.none { it.first < 0 } || flows.none { it.first > 0 }) return null
    var rate = 0.1
    repeat(50) {
        var npv = 0.0; var derivative = 0.0
        flows.forEach { (cash, date) -> val years = ChronoUnit.DAYS.between(date, today) / 365.0; val factor = Math.pow((1 + rate).coerceAtLeast(0.0001), years); npv += cash / factor; derivative -= years * cash / ((1 + rate) * factor) }
        if (abs(derivative) < 1e-9) return@repeat
        val next = rate - npv / derivative
        if (abs(next - rate) < 1e-7) return next
        rate = next.coerceIn(-0.99, 10.0)
    }
    return rate.takeIf { it.isFinite() }
}
@Composable fun Header(total: Double) { Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF182844), Color(0xFF294D8F))).let { it }).padding(20.dp)) { Text("我的投资", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(24.dp)); Text("总资产（元）", color = Color(0xFFB9C8E5), fontSize = 13.sp); Text(money(total), color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold); Text("根据交易记录实时计算", color = Color(0xFF77E0AE)) } }
@Composable fun Metric(name: String, value: String, green: Boolean) { Card(Modifier.width(170.dp)) { Column(Modifier.padding(15.dp)) { Text(name, color = Color.Gray, fontSize = 12.sp); Text(value, color = if(green) Color(0xFF16A673) else Color(0xFF182230), fontSize = 19.sp, fontWeight = FontWeight.Bold) } } }
@Composable fun TradeRow(t: Trade) { Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Text(if(t.type == "卖出") "卖" else "买", Modifier.background(Color(0xFFEEF3FF), RoundedCornerShape(10.dp)).padding(10.dp), color = Color(0xFF3767E8)); Column(Modifier.padding(start = 10.dp).weight(1f)) { Text("${t.asset} · ${t.type}", fontWeight = FontWeight.Bold); Text(t.date, color = Color.Gray, fontSize = 11.sp) }; Text(if(t.type == "卖出") "+${t.amount}" else "-${t.amount}") } } }
@Composable fun Ledger(padding: PaddingValues, trades: List<Trade>, onAdd: (Trade) -> Unit) { var show by remember { mutableStateOf(false) }; Scaffold(modifier = Modifier.padding(padding), floatingActionButton = { FloatingActionButton(onClick = { show = true }) { Text("＋") } }) { inner -> LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(inner)) { item { Text("记账", fontSize = 28.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(12.dp)) }; items(trades) { TradeRow(it) } } }; if(show) AddTradeDialog({ show = false }, onAdd) }
@Composable fun AddTradeDialog(close: () -> Unit, add: (Trade) -> Unit) { var asset by remember { mutableStateOf("") }; var amount by remember { mutableStateOf("") }; var fee by remember { mutableStateOf("0") }; var type by remember { mutableStateOf("买入") }; var error by remember { mutableStateOf<String?>(null) }; AlertDialog(onDismissRequest = close, title = { Text("新增交易") }, text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("买入", "卖出").forEach { Button(onClick = { type = it }, colors = ButtonDefaults.buttonColors(containerColor = if (type == it) MaterialTheme.colorScheme.primary else Color.LightGray)) { Text(it) } } }; OutlinedTextField(asset, { asset = it }, label = { Text("标的名称") }); OutlinedTextField(amount, { amount = it }, label = { Text("金额") }); OutlinedTextField(fee, { fee = it }, label = { Text("手续费（可选）") }); error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) } } }, confirmButton = { Button(onClick = { val value = amount.toDoubleOrNull(); val feeValue = fee.toDoubleOrNull(); when { asset.isBlank() -> error = "请输入标的名称"; value == null || value <= 0 -> error = "请输入大于 0 的金额"; feeValue == null || feeValue < 0 -> error = "手续费不能为负数"; else -> { add(Trade(asset.trim(), type, value, "刚刚", feeValue)); close() } } }) { Text("保存") } }, dismissButton = { TextButton(onClick = close) { Text("取消") } }) }
@Composable fun Holdings(p: PaddingValues, trades: List<Trade>) { val grouped = trades.groupBy { it.asset }; SimplePage(p, "持仓", grouped.map { (asset, rows) -> "$asset · 净投入 ${money(rows.sumOf { if (it.type == "卖出") -it.amount else it.amount })}" }.ifEmpty { listOf("暂无持仓记录") }) }
@Composable fun Analysis(p: PaddingValues, trades: List<Trade>) { val total = 50000.0 + trades.sumOf { if (it.type == "卖出") it.amount - it.fee else -it.amount - it.fee } + trades.filter { it.type != "卖出" }.sumOf { it.amount }; val annual = xirr(trades, total); SimplePage(p, "分析", listOf("交易笔数 · ${trades.size}", "累计买入 · ${money(trades.filter { it.type != "卖出" }.sumOf { it.amount })}", "累计卖出 · ${money(trades.filter { it.type == "卖出" }.sumOf { it.amount })}", "XIRR · ${annual?.let { String.format("%.2f%%", it * 100) } ?: "待补充有效现金流"}")) }
@Composable fun Profile(p: PaddingValues) { SimplePage(p, "我的", listOf("账户管理  · 2 个账户", "基准收益率 · 货币基金 5.20%", "数据导入与导出", "隐私与安全")) }
@Composable fun SimplePage(p: PaddingValues, title: String, rows: List<String>) { LazyColumn(Modifier.padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold) }; items(rows) { Card(Modifier.fillMaxWidth()) { Text(it, Modifier.padding(18.dp), fontSize = 16.sp) } } } }
