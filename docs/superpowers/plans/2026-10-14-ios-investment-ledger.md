# iOS 个人投资账本实现计划

> **For agentic workers:** 按任务顺序实现并在每个任务后进行可执行检查。

**目标：** 创建一个 SwiftUI + SwiftData 的本地优先 iOS 投资账本首版工程。

**架构：** 使用 SwiftUI 页面与 Observable 视图模型，SwiftData 保存交易与标的，计算服务独立处理组合汇总和收益率。首版以可运行的 Xcode 工程源码为交付物。

**技术栈：** Swift 5.9、SwiftUI、SwiftData、Charts、iOS 17+。

**规格：** `docs/superpowers/specs/2026-10-14-ios-investment-ledger-design.md`

## 全局约束

- 使用中文界面文案。
- 首版本地优先，不依赖后端。
- 金额使用 `Decimal`，日期使用 `Date`。
- OCR 仅实现待确认数据结构和确认界面，暂不接入真实识别服务。

### 任务 1：工程与数据模型

**文件：** 创建 `InvestmentLedger/InvestmentLedgerApp.swift`、`Models.swift`、`Persistence.swift`。

- [ ] 创建 SwiftData 模型：账户、标的、交易、基准、资产快照、待确认导入。
- [ ] 添加交易类型和资产类型枚举。
- [ ] 配置 `ModelContainer` 与预览内存容器。

### 任务 2：计算与状态层

**文件：** 创建 `Calculator.swift`、`LedgerStore.swift`。

- [ ] 实现现金、持仓数量、成本、总资产和收益汇总。
- [ ] 实现交易保存、删除、筛选和待确认导入确认。
- [ ] 对数据不足返回中文原因。

### 任务 3：SwiftUI 页面

**文件：** 创建 `ContentView.swift`、`HomeView.swift`、`LedgerView.swift`、`HoldingsView.swift`、`AnalysisView.swift`、`ProfileView.swift`、`Components.swift`。

- [ ] 实现五 Tab 页面和 HTML 稿对应的视觉样式。
- [ ] 实现新增交易表单与校验。
- [ ] 实现截图待确认演示入口。

### 任务 4：验证与卡点记录

**文件：** 修改 `docs/项目进度记录.md`，创建 `InvestmentLedger/README.md`。

- [ ] 检查 Swift 文件结构、模型引用和 iOS 17 API 使用。
- [ ] 记录当前环境无法运行 Xcode 模拟器的验证限制。
- [ ] 记录后续接入 OCR、行情和真机测试的卡点。
