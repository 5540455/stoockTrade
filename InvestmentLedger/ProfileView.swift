import SwiftUI
struct ProfileView: View { var body: some View { List { Section { Text("投资账本").font(.headline); Text("本地数据 · 优先保存于本设备").font(.caption).foregroundStyle(.secondary) }; Section { Label("账户管理", systemImage: "person.2"); Label("基准收益率", systemImage: "chart.line.uptrend.xyaxis"); Label("数据导入与导出", systemImage: "square.and.arrow.up"); Label("隐私与安全", systemImage: "lock.shield") } }.navigationTitle("我的") }
}
