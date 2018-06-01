package uno.meng.crf_seg;

import org.nlpcn.commons.lang.util.WordAlert;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;
import uno.meng.crf_seg.util.item;
import java.util.ArrayList;
import java.util.List;

public class Config {

  public String splitStr = "\\s+";

  public Config(int[][] template) {
    this.template = template;
  }

  public static final int TAG_NUM = 4; // 标记类型写死了4个

  // 特殊字符的标注
  public static final char BEGIN = 128;

  public static final char END = 129;

  public static final char NUM_BEGIN = 130;

  public static final char EN_BEGIN = 140;

  public static final char FEATURE_BEGIN = 150;

  public static char getNum(String str) {
    if (str.length() > 9) {
      return NUM_BEGIN;
    } else {
      return (char) (NUM_BEGIN + str.length());
    }
  }

  public static char getEn(String str) {
    if (str.length() > 9) {
      return EN_BEGIN;
    } else {
      return (char) (EN_BEGIN + str.length());
    }

  }

  // 字标注类型
  public static int S = 0;
  public static int B = 1;
  public static int M = 2;
  public static int E = 3;

  private int[][] template =
      {{-2}, {-1}, {0}, {1}, {2}, {-2, -1}, {-1, 0}, {0, 1}, {1, 2}, {-1, 1}};

  public int[][] getTemplate() {
    return template;
  }

  public void setTemplate(int[][] template) {
    this.template = template;
  }

  /**
   * 词语标准化
   * 
   * @param word
   * @return
   */
  public static List<item> wordAlert(String word) {

    char[] chars = WordAlert.alertStr(word);

    List<item> list = new ArrayList<item>();

    StringBuilder tempSb = new StringBuilder();

    int status = 0; // 1 num 2 english

    item item = null;

    for (int i = 0; i < chars.length; i++) {

      if (chars[i] >= '0' && chars[i] <= '9') {
        if (status == 2) {
          item = new item(Config.getNum(tempSb.toString()));
          item.len = tempSb.length();
          list.add(item);
          tempSb = new StringBuilder();
        }
        tempSb.append(chars[i]);
        status = 1;
      } else if (chars[i] >= 'A' && chars[i] <= 'z') {
        if (status == 1) {
          item = new item(Config.getEn(tempSb.toString()));
          item.len = tempSb.length();
          list.add(item);
          tempSb = new StringBuilder();
        }
        tempSb.append(chars[i]);
        status = 2;
      } else {
        if (status == 1) {
          item = new item(Config.getNum(tempSb.toString()));
          item.len = tempSb.length();
          list.add(item);
        } else if (status == 2) {
          item = new item(Config.getEn(tempSb.toString()));
          item.len = tempSb.length();
          list.add(item);
        }
        tempSb = new StringBuilder();
        list.add(new item(chars[i]));
        status = 0;
      }

    }

    if (tempSb.length() > 0) {
      if (status == 1) {
        item = new item(Config.getNum(tempSb.toString()));
        item.len = tempSb.length();
        list.add(item);
      } else if (status == 2) {
        item = new item(Config.getEn(tempSb.toString()));
        item.len = tempSb.length();
        list.add(item);
      } else {
        System.out.println("err! status :" + status);
      }
    }

    return list;
  }

  /**
   * @param temp
   * @return
   */
  public static List<item> makeToElementList(String temp, String splitStr) {
    String[] split = temp.split(splitStr);
    List<item> list = new ArrayList<item>(temp.length());

    for (String word : split) {

      List<item> wordAlert = wordAlert(word);

      int len = wordAlert.size();

      if (len == 1) {
        wordAlert.get(0).updateTag(Config.S);
      } else if (len == 2) {
        wordAlert.get(0).updateTag(Config.B);
        wordAlert.get(1).updateTag(Config.E);
      } else if (len > 2) {
        wordAlert.get(0).updateTag(Config.B);
        for (int i = 1; i < len - 1; i++) {
          wordAlert.get(i).updateTag(Config.M);
        }
        wordAlert.get(len - 1).updateTag(Config.E);
      }

      list.addAll(wordAlert);
    }
    return list;
  }

  public List<item> makeToElementList(String temp) {
    return wordAlert(temp);
  }

  public char getNameIfOutArr(List<item> list, int index) {
    if (index < 0) {
      return Config.BEGIN;
    } else if (index >= list.size()) {
      return Config.END;
    } else {
      return list.get(index).name;
    }
  }

  public char getTagIfOutArr(List<item> list, int index) {
    if (index < 0 || index >= list.size()) {
      return 0;
    } else {
      return (char) list.get(index).getTag();
    }
  }

  /**
   * 得到一个位置的所有特征
   * 
   * @param list
   * @param index
   * @return KeyValue(词语,featureLength*tagNum)
   */
  public char[][] makeFeatureArr(List<item> list, int index) {
    char[][] result = new char[template.length][];
    char[] chars = null;
    int len = 0;
    int i = 0;
    for (; i < template.length; i++) {
      if (template[i].length == 0) {
        continue;
      }
      chars = new char[template[i].length + 1];
      len = chars.length - 1;
      for (int j = 0; j < len; j++) {
        chars[j] = getNameIfOutArr(list, index + template[i][j]);
      }
      chars[len] = (char) (FEATURE_BEGIN + i);
      result[i] = chars;
    }

    return result;
  }

  public static char getTagName(int tag) {
    switch (tag) {
      case 0:
        return 'S';
      case 1:
        return 'B';
      case 2:
        return 'M';
      case 3:
        return 'E';
      default:
        return '?';
    }
  }
}