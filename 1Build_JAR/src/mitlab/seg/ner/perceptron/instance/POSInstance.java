package mitlab.seg.ner.perceptron.instance;

import java.util.ArrayList;
import java.util.List;
import mitlab.seg.ner.corpus.document.sentence.Sentence;
import mitlab.seg.ner.corpus.document.sentence.word.Word;
import mitlab.seg.ner.perceptron.feature.FeatureMap;
import mitlab.seg.ner.perceptron.feature.MutableFeatureMap;
import mitlab.seg.ner.perceptron.tagset.POSTagSet;
import mitlab.seg.ner.perceptron.utility.Utility;

public class POSInstance extends Instance {
  /**
   * 构建词性标注实例
   *
   * @param termArray 词语
   * @param posArray 词性
   */
  public POSInstance(String[] termArray, String[] posArray, FeatureMap featureMap) {
    // String sentence = TextUtility.combine(termArray);
    this(termArray, featureMap);

    POSTagSet tagSet = (POSTagSet) featureMap.tagSet;
    tagArray = new int[termArray.length];
    for (int i = 0; i < termArray.length; i++) {
      tagArray[i] = tagSet.add(posArray[i]);
    }
  }

  public POSInstance(String[] termArray, FeatureMap featureMap) {
    initFeatureMatrix(termArray, featureMap);
  }

  protected int[] extractFeature(String[] words, FeatureMap featureMap, int position) {
    boolean create = featureMap instanceof MutableFeatureMap;
    List<Integer> featVec = new ArrayList<Integer>();

    // String pre2Word = position >= 2 ? words[position - 2] : "_B_";
    String preWord = position >= 1 ? words[position - 1] : "_B_";
    String curWord = words[position];

    // System.out.println("cur: " + curWord);
    String nextWord = position <= words.length - 2 ? words[position + 1] : "_E_";
    // String next2Word = position <= words.length - 3 ? words[position + 2] : "_E_";

    StringBuilder sbFeature = new StringBuilder();
    // sbFeature.delete(0, sbFeature.length());
    // sbFeature.append("U[-2,0]=").append(pre2Word);
    // addFeature(sbFeature, featVec, featureMap, create);

    sbFeature.append(preWord).append('1');
    addFeatureThenClear(sbFeature, featVec, featureMap, create);

    sbFeature.append(curWord).append('2');
    addFeatureThenClear(sbFeature, featVec, featureMap, create);

    sbFeature.append(nextWord).append('3');
    addFeatureThenClear(sbFeature, featVec, featureMap, create);

    // sbFeature.delete(0, sbFeature.length());
    // sbFeature.append("U[2,0]=").append(next2Word);
    // addFeature(sbFeature, featVec, featureMap, create);

    // wiwi+1(i = − 1, 0)
    // sbFeature.delete(0, sbFeature.length());
    // sbFeature.append("B[-1,0]=").append(preWord).append("/").append(curWord);
    // addFeature(sbFeature, featVec, featureMap, create);
    //
    // sbFeature.delete(0, sbFeature.length());
    // sbFeature.append("B[0,1]=").append(curWord).append("/").append(nextWord);
    // addFeature(sbFeature, featVec, featureMap, create);
    //
    // sbFeature.delete(0, sbFeature.length());
    // sbFeature.append("B[-1,1]=").append(preWord).append("/").append(nextWord);
    // addFeature(sbFeature, featVec, featureMap, create);

    // last char(w−1)w0
    // String lastChar = position >= 1 ? "" + words[position - 1].charAt(words[position -
    // 1].length() - 1) : "_BC_";
    // sbFeature.delete(0, sbFeature.length());
    // sbFeature.append("CW[-1,0]=").append(lastChar).append("/").append(curWord);
    // addFeature(sbFeature, featVec, featureMap, create);
    //
    // // w0 ﬁrst_char(w1)
    // String nextChar = position <= words.length - 2 ? "" + words[position + 1].charAt(0) : "_EC_";
    // sbFeature.delete(0, sbFeature.length());
    // sbFeature.append("CW[1,0]=").append(curWord).append("/").append(nextChar);
    // addFeature(sbFeature, featVec, featureMap, create);
    //
    int length = curWord.length();
    //
    // // ﬁrstchar(w0)lastchar(w0)
    // sbFeature.delete(0, sbFeature.length());
    // sbFeature.append("BE=").append(curWord.charAt(0)).append("/").append(curWord.charAt(length -
    // 1));
    // addFeature(sbFeature, featVec, featureMap, create);

    // prefix
    sbFeature.append(curWord.substring(0, 1)).append('4');
    addFeatureThenClear(sbFeature, featVec, featureMap, create);

    if (length > 1) {
      sbFeature.append(curWord.substring(0, 2)).append('4');
      addFeatureThenClear(sbFeature, featVec, featureMap, create);
    }

    if (length > 2) {
      sbFeature.append(curWord.substring(0, 3)).append('4');
      addFeatureThenClear(sbFeature, featVec, featureMap, create);
    }

    // sufﬁx(w0, i)(i = 1, 2, 3)
    sbFeature.append(curWord.charAt(length - 1)).append('5');
    addFeatureThenClear(sbFeature, featVec, featureMap, create);

    if (length > 1) {
      sbFeature.append(curWord.substring(length - 2)).append('5');
      addFeatureThenClear(sbFeature, featVec, featureMap, create);
    }

    if (length > 2) {
      sbFeature.append(curWord.substring(length - 3)).append('5');
      addFeatureThenClear(sbFeature, featVec, featureMap, create);
    }

    // length
    // if (length >= 5)
    // {
    // addFeature("le=" + 5, featVec, featureMap, create);
    // }
    // else
    // {
    // addFeature("le=" + length, featVec, featureMap, create);
    // }

    // label feature
    // String preLabel;
    // if (position >= 1)
    // {
    // preLabel = label[position - 1];
    // }
    // else
    // {
    // preLabel = "_BL_";
    // }
    //
    // addFeature("BL=" + preLabel, featVec, featureMap, create);

    // for (int i = 0; i < curWord.length(); i++)
    // {
    // String prefix = curWord.substring(0, 1) + curWord.charAt(i) + "";
    // addFeature("p2f=" + prefix, featVec, featureMap, create);
    // String suffix = curWord.substring(curWord.length() - 1) + curWord.charAt(i) + "";
    // addFeature("s2f=" + suffix, featVec, featureMap, create);

    // if ((i < curWord.length() - 1) && (curWord.charAt(i) == curWord.charAt(i + 1)))
    // {
    // addFeature("dulC=" + curWord.substring(i, i + 1), featVec, featureMap, create);
    // }
    // if ((i < curWord.length() - 2) && (curWord.charAt(i) == curWord.charAt(i + 2)))
    // {
    // addFeature("dul2C=" + curWord.substring(i, i + 1), featVec, featureMap, create);
    // }
    // }

    // boolean isDigit = true;
    // for (int i = 0; i < curWord.length(); i++)
    // {
    // if (CharType.get(curWord.charAt(i)) != CharType.CT_NUM)
    // {
    // isDigit = false;
    // break;
    // }
    // }
    // if (isDigit)
    // {
    // addFeature("wT=d", featVec, featureMap, create);
    // }

    // boolean isPunt = true;
    // for (int i = 0; i < curWord.length(); i++)
    // {
    // if (!CharType.punctSet.contains(curWord.charAt(i) + ""))
    // {
    // isPunt = false;
    // break;
    // }
    // }
    // if (isPunt)
    // {
    // featVec.add("wT=p");
    // }

    // boolean isLetter = true;
    // for (int i = 0; i < curWord.length(); i++)
    // {
    // if (CharType.get(curWord.charAt(i)) != CharType.CT_LETTER)
    // {
    // isLetter = false;
    // break;
    // }
    // }
    // if (isLetter)
    // {
    // addFeature("wT=l", featVec, featureMap, create);
    // }
    // sbFeature = null;

    return toFeatureArray(featVec);
  }

  private void initFeatureMatrix(String[] termArray, FeatureMap featureMap) {
    featureMatrix = new int[termArray.length][];
    for (int i = 0; i < featureMatrix.length; i++) {
      featureMatrix[i] = extractFeature(termArray, featureMap, i);
    }
  }

  public static POSInstance create(String segmentedTaggedSentence, FeatureMap featureMap) {
    return create(Sentence.create(segmentedTaggedSentence), featureMap);
  }

  public static POSInstance create(Sentence sentence, FeatureMap featureMap) {
    if (sentence == null || featureMap == null) {
      return null;
    }
    List<Word> wordList = sentence.toSimpleWordList();
    String[] termArray = new String[wordList.size()];
    String[] posArray = new String[wordList.size()];
    int i = 0;
    for (Word word : wordList) {
      termArray[i] = word.getValue();
      posArray[i] = word.getLabel();
      ++i;
    }
    return new POSInstance(termArray, posArray, featureMap);
  }
}