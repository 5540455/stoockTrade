import SwiftUI
import SwiftData

struct ContentView: View { @Environment(\ModelContext.self) private var context; @State private var store: LedgerStore?; var body: some View { Group { if let store { TabView { HomeView(store: store).tabItem { Label("首页", systemImage: "house.fill") }; LedgerView(store: store).tabItem { Label("记账", systemImage: "arrow.up.arrow.down") }; HoldingsView(store: store).tabItem { Label("持仓", systemImage: "chart.pie.fill") }; AnalysisView(store: store).tabItem { Label("分析", systemImage: "chart.xyaxis.line") }; ProfileView().tabItem { Label("我的", systemImage: "person.fill") } } } else { ProgressView() } }.task { let s = LedgerStore(context: context); s.seed(); store = s } }
}
