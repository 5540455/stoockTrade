import SwiftData

enum Persistence { static let container: ModelContainer = { try! ModelContainer(for: InvestmentAccount.self, Asset.self, Transaction.self, Benchmark.self, PortfolioSnapshot.self, PendingImport.self) }() }
