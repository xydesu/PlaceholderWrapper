# PlaceholderWrapper

> 一個 PlaceholderAPI 擴充元件，用於解決 Minecraft 伺服器中當佔位符為空時，外圍裝飾字元（如 `[]`）殘留的問題。

[最新發佈版本下載](https://github.com/xydesu/PlaceholderWrapper/releases/tag/v1.0.0)

---

## 核心特色

* 純 Expansion 設計：無需作為 Spigot 插件載入，直接放入 PAPI 的 `expansions` 目錄即可生效。
* 實作 Configurable 介面：設定會自動整合至 `/plugins/PlaceholderAPI/config.yml` 中。
* 動態語法支援：提供免修改設定檔的動態解析語法 `%wrapper_format_{placeholder}?format?%`。
* 無效值過濾：可設定 `treat_as_empty`（如 `none`、`None`），辨識各類插件的預設空值。

---

## 安裝方式

1. 前往 [GitHub Releases](https://github.com/xydesu/PlaceholderWrapper/releases/tag/v1.0.0) 下載已編譯完成的 `placeholder-wrapper-1.0.0.jar`。
2. 將該 Jar 檔複製到伺服器的 `/plugins/PlaceholderAPI/expansions/` 資料夾下。
3. 於伺服器執行 `/papi reload` 載入 Expansion。
4. 載入成功後，可在 `/plugins/PlaceholderAPI/config.yml` 的 `expansions.wrapper` 底下進行配置。

---

## 設定說明 (config.yml)

當您首次載入此 Expansion，PAPI 會自動在 `config.yml` 中建立以下預設範例：

```yaml
expansions:
  wrapper:
    rules:
      guild:
        placeholder: '%guild_prefix%'
        format: '[%guild_prefix%]'
        empty: ''
        treat_as_empty: ''
```

### 欄位解析

| 欄位名 | 類型 | 說明 | 範例 |
| :--- | :--- | :--- | :--- |
| `placeholder` | `String` | 需要進行檢測的原始 PAPI 佔位符。 | `%guild_prefix%` |
| `format` | `String` | 當原始佔位符不為空時，輸出的格式。 | `[%guild_prefix%]` |
| `empty` | `String` | 當原始佔位符為空時，輸出的格式。 | `""` (空) 或 `&7無公會` |
| `treat_as_empty` | `String` | *選填*。指定哪些特定解析結果視同為「空」處理。 | `none` |

---

## 使用範例

### 1. 設定檔規則模式
* 語法：`%wrapper_<rule_name>%`
* 範例： `%wrapper_guild%`
  * 当玩家有公會（例如前綴為 `MyGuild`）：輸出 `[MyGuild]`。
  * 当玩家無公會（例如前綴為空 `""`）：輸出 `""`。

### 2. 免設定動態格式模式 (Dynamic Format)
您可以在遊戲中直接利用大括號 `{}` 與問號 `?` 分隔符號進行動態包裹解析。
* 語法：`%wrapper_format_{<check_placeholder>}?<format_if_not_empty>?<format_if_empty>%`
* 範例：
  * **僅包裹非空值**：
    `%wrapper_format_{guild_prefix}?[{guild_prefix}]?%`
    * 有公會：`[MyGuild]`
    * 無公會：`""`
  * **包裹非空值並自訂空值替代文字**：
    `%wrapper_format_{guild_prefix}?[{guild_prefix}]?&7無公會%`
    * 有公會：`[MyGuild]`
    * 無公會：`無公會`

---

## 開發與建置

本專案使用 Java 21 與 Maven 進行開發。

### 建置指令
在專案根目錄下執行：
```bash
mvn clean package
```
建置完成後，成品將生成於 `/target/placeholder-wrapper-1.0.0.jar`。
