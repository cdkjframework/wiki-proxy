package com.framewiki.network.proxy.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;
import com.framewiki.network.proxy.common.CommonFormat;
import com.framewiki.network.proxy.model.enums.InteractiveTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.model
 * @ClassName: InteractiveModel
 * @Description: 交互基础类型
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class InteractiveModel implements JSONAware {

  /**
   * 创建交互模型
   *
   * @param model 模型
   */
  public InteractiveModel(InteractiveModel model) {
    this.fullValue(model);
  }

  /**
   * 交互基础类型
   *
   * @param interactiveTypeEnum 交互类型
   * @param key                 键
   * @param value               值
   * @return InteractiveModel 交互模型
   */
  public static InteractiveModel of(InteractiveTypeEnum interactiveTypeEnum, String key, String value) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(key, value);

    return new InteractiveModel(CommonFormat.generateInteractiveSeq(), interactiveTypeEnum.name(), jsonObject);
  }

  /**
   * 创建交互模型
   *
   * @param interactiveSeq      交互序列
   * @param interactiveTypeEnum 交互类型
   * @param data                数据
   * @return InteractiveModel 交互模型
   */
  public static InteractiveModel of(String interactiveSeq, InteractiveTypeEnum interactiveTypeEnum, Object data) {
    return new InteractiveModel(interactiveSeq, interactiveTypeEnum.name(),
        data == null ? null : JSON.parseObject(JSON.toJSONString(data)));
  }

  /**
   * 创建交互模型
   *
   * @param interactiveTypeEnum 交互类型
   * @param data                数据
   * @return InteractiveModel 交互模型
   */
  public static InteractiveModel of(InteractiveTypeEnum interactiveTypeEnum, Object data) {
    return new InteractiveModel(CommonFormat.generateInteractiveSeq(), interactiveTypeEnum.name(),
        data == null ? null : JSON.parseObject(JSON.toJSONString(data)));
  }

  /**
   * 创建交互模型
   *
   * @param interactiveType 交互式类型
   * @param data            数据
   * @return InteractiveModel 交互模型
   */
  public static InteractiveModel of(String interactiveType, Object data) {
    return new InteractiveModel(CommonFormat.generateInteractiveSeq(), interactiveType,
        JSON.parseObject(JSON.toJSONString(data)));
  }

  /**
   * 填充值
   *
   * @param model 模型
   */
  public void fullValue(InteractiveModel model) {
    this.setInteractiveSeq(model.getInteractiveSeq());
    this.setInteractiveType(model.getInteractiveType());
    this.setData(model.getData());
  }

  /**
   * 交互序列，用于异步通信
   */
  private String interactiveSeq;
  /**
   * 交互类型
   */
  private String interactiveType;
  /**
   * 交互实体内容
   */
  private JSONObject data;

  /**
   * 序列化为JSON字符串
   *
   * @return JSON字符串
   */
  @Override
  public String toJSONString() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("interactiveSeq", this.interactiveSeq);
    jsonObject.put("interactiveType", this.interactiveType);
    jsonObject.put("data", this.data);
    return jsonObject.toJSONString();
  }

}
