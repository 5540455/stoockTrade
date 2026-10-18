import SwiftUI
struct HoldingsView: View { let store: LedgerStore; var body: some View { List { Section("持仓总市值") { Text(store.summary.invested, format: .number.precision(.fractionLength(2))).font(.largeTitle.bold()); Text("今日 +1,086.40（+0.45%）").foregroundStyle(.green) }; Section("资产分布") { ForEach(AssetType.allCases, id: \.self) { type in HStack { Circle().fill(.blue).frame(width: 10, height: 10); Text(type.rawValue); Spacer(); Text(type == .cash ? store.summary.cash : store.summary.invested / 3, format: .number.precision(.fractionLength(2))) } } } }.navigationTitle("持仓") }
}
