import Foundation
import SwiftData
import Observation

@Observable final class LedgerStore {
 let context: ModelContext; var account: InvestmentAccount?; var transactions: [Transaction] = []; var pending: [PendingImport] = []
 init(context: ModelContext) { self.context = context; reload() }
 func reload() { transactions = (try? context.fetch(FetchDescriptor<Transaction>(sortBy: [SortDescriptor(\Transaction.date, order: .reverse)]))) ?? []; account = (try? context.fetch(FetchDescriptor<InvestmentAccount>()))?.first; pending = (try? context.fetch(FetchDescriptor<PendingImport>())) ?? [] }
 func seed() { guard account == nil else { return }; let a = InvestmentAccount(initialCash: 42680); context.insert(a); context.insert(Benchmark()); context.insert(Transaction(type: .deposit, assetName: "现金账户", amount: 286540.8)); try? context.save(); reload() }
 func add(type: TransactionType, asset: String, amount: Decimal, date: Date) { context.insert(Transaction(date: date, type: type, assetName: asset, amount: amount)); try? context.save(); reload() }
 func confirm(_ item: PendingImport) { add(type: TransactionType(rawValue: item.typeRaw) ?? .sell, asset: item.assetName, amount: item.amount, date: .now); item.state = .confirmed; try? context.save(); reload() }
 var summary: PortfolioSummary { PortfolioCalculator.summary(account: account, transactions: transactions) }
}
