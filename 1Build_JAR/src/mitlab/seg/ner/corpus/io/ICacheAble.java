package mitlab.seg.ner.corpus.io;

import java.io.DataOutputStream;

/**
 * 可写入或读取二进制
 */
public interface ICacheAble {
  /**
   * 写入
   * 
   * @param out
   * @throws Exception
   */
  void save(DataOutputStream out) throws Exception;

  /**
   * 载入
   * 
   * @param byteArray
   * @return
   */
  boolean load(ByteArray byteArray); // 目前的设计并不好，应该抛异常而不是返回布尔值
}
