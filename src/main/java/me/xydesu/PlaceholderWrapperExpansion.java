package me.xydesu;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Configurable;
import org.bukkit.OfflinePlayer;
import java.util.HashMap;
import java.util.Map;

/**
 * PlaceholderWrapperExpansion 是一個 PlaceholderAPI 擴充元件，
 * 用於根據其他佔位符是否為空來動態決定顯示內容或套用格式。
 */
public class PlaceholderWrapperExpansion extends PlaceholderExpansion implements Configurable {

    @Override
    public String getIdentifier() {
        return "wrapper";
    }

    @Override
    public String getAuthor() {
        return "xy";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        // 預設公會前綴格式化規則：當 %guild_prefix% 不為空時套用中括號，為空時顯示空字串
        defaults.put("rules.guild.placeholder", "%guild_prefix%");
        defaults.put("rules.guild.format", "[%guild_prefix%]");
        defaults.put("rules.guild.empty", "");
        defaults.put("rules.guild.treat_as_empty", "");
        return defaults;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        // 處理動態格式解析
        // 語法：%wrapper_format_{check_placeholder}?format_if_not_empty?format_if_empty%
        if (params.startsWith("format_")) {
            return handleDynamicFormat(player, params);
        }

        // 從設定檔讀取自訂規則的路徑
        String rulePath = "rules." + params;
        
        // 取得目標佔位符，若無設定則此規則不存在，回傳 null
        String targetPlaceholder = getString(rulePath + ".placeholder", null);
        if (targetPlaceholder == null) {
            return null;
        }

        String format = getString(rulePath + ".format", "");
        String empty = getString(rulePath + ".empty", "");
        String treatAsEmpty = getString(rulePath + ".treat_as_empty", "");

        // 解析目標佔位符的值
        String value = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, targetPlaceholder);

        // 判斷解析結果是否為空
        boolean isEmpty = (value == null 
                || value.isEmpty() 
                || value.equals(targetPlaceholder) 
                || (!treatAsEmpty.isEmpty() && value.equalsIgnoreCase(treatAsEmpty)));

        if (isEmpty) {
            // 解析結果為空，回傳空值對應的格式（經 PAPI 解析）
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, empty);
        } else {
            // 解析結果不為空，回傳正常格式（經 PAPI 解析）
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
        }
    }

    /**
     * 處理動態格式解析邏輯
     */
    private String handleDynamicFormat(OfflinePlayer player, String params) {
        // 移除 "format_" 前綴
        String content = params.substring(7);
        
        // 以 "?" 作為分隔符號切分參數
        String[] parts = content.split("\\?", -1);
        if (parts.length < 1) {
            return "";
        }

        String checkPlaceholderRaw = parts[0];
        // 將大括號 {placeholder} 轉換為 PAPI 的百分號 %placeholder% 格式
        String checkPlaceholder = convertCurlyToPercent(checkPlaceholderRaw);

        // 解析用於檢查的佔位符
        String value = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, checkPlaceholder);

        // 判斷解析結果是否為空
        boolean isEmpty = (value == null || value.isEmpty() || value.equals(checkPlaceholder));

        if (isEmpty) {
            // 若為空，且有指定空時格式則解析回傳，否則回傳空字串
            String emptyFormat = parts.length > 2 ? parts[2] : "";
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, convertCurlyToPercent(emptyFormat));
        } else {
            // 若不為空，且有指定非空格式則解析回傳，否則回傳原本解析的值
            String notEmptyFormat = parts.length > 1 ? parts[1] : "";
            if (notEmptyFormat.isEmpty()) {
                return value;
            }
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, convertCurlyToPercent(notEmptyFormat));
        }
    }

    /**
     * 將字串中的大括號表示法 {placeholder} 轉換為 PAPI 的百分號表示法 %placeholder%
     */
    private String convertCurlyToPercent(String input) {
        if (input == null) {
            return "";
        }
        // 將 {abc} 替換成 %abc%
        return input.replaceAll("\\{([^{}]+)\\}", "%$1%");
    }
}
