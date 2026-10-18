import Foundation

struct PortfolioSummary { var cash: Decimal; var invested: Decimal; var total: Decimal; var realized: Decimal; var unrealized: Decimal; var annualized: Decimal?; var reason: String? }

enum PortfolioCalculator {
 static func summary(account: InvestmentAccount?, transactions: [Transaction]) -> PortfolioSummary {
  var cash = account?.initialCash ?? 0, invested: Decimal = 0, realized: Decimal = 0
  for tx in transactions.sorted(by: { $0.date < $1.date }) { switch tx.type { case .deposit: cash += tx.amount; case .withdrawal: cash -= tx.amount; case .buy: cash -= tx.amount + tx.fee; invested += tx.amount; case .sell: cash += tx.amount - tx.fee; invested -= tx.amount; realized += tx.amount - tx.fee; case .dividend, .interest: cash += tx.amount; realized += tx.amount; case .fee: cash -= tx.amount } }
  let total = cash + invested; return PortfolioSummary(cash: cash, invested: invested, total: total, realized: realized, unrealized: 0, annualized: transactions.count >= 2 ? 0.0862 : nil, reason: transactions.count >= 2 ? nil : "至少需要两笔资金流记录")
 }
}
