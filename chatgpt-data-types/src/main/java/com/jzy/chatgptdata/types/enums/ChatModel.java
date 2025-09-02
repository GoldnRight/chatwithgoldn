package com.jzy.chatgptdata.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 模型对象
 */
@Getter
@AllArgsConstructor
public enum ChatModel {

    /**
     * 智谱
     */
    COGVIEW_3("cogview-3", "根据用户的文字描述生成图像,使用同步调用方式请求接口"),
    GLM_45_FLASH("glm-4.5-flash","在确保强大推理能力、稳定代码生成和多工具协同处理能力的同时，具备显著的运行速度优势"),
    GLM_Z1_AIR("glm-z1-air","数理推理能力显著增强，适合高频调用场景"),
    GLM_Z1_FLASH("glm-z1-flash","数学逻辑推理、长文档处理、代码生成等场景表现十分出色"),

    /**
     * DeepSeek
     */
    DeepSeek_V3("deepseek_v3", "deepseek_v3"),

    /**
     * 通义千问
     */
    Qwen3_Coder_Plus("qwen3-coder-plus", "通义千问3.0"),
    ;

    private final String code;
    private final String info;

    public static ChatModel get(String code) {
        switch (code) {
            case "cogview-3":
                return ChatModel.COGVIEW_3;
            case "chatglm-4.5-flash":
                return ChatModel.GLM_45_FLASH;
            case "chatglm-z1-air":
                return ChatModel.GLM_Z1_AIR;
            case "chatglm-z1-flash":
                return ChatModel.GLM_Z1_FLASH;
            case "deepseek_v3":
                return ChatModel.DeepSeek_V3;
            case "qwen3-coder-plus":
                return ChatModel.Qwen3_Coder_Plus;
            default:
                return ChatModel.GLM_45_FLASH;
        }
    }

}
