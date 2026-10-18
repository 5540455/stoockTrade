import SwiftUI
import SwiftData

@main struct InvestmentLedgerApp: App { var body: some Scene { WindowGroup { ContentView().modelContainer(Persistence.container) } } }
