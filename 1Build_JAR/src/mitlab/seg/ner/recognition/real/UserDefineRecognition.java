package mitlab.seg.ner.recognition.real;

import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.SmartForest;
import mitlab.seg.crf_seg.util.Graph;
import mitlab.seg.crf_seg.util.TermUtil;
import mitlab.seg.crf_seg.util.TermUtil.InsertTermType;
import mitlab.seg.ner.domain.Term;
import mitlab.seg.ner.domain.TermNature;
import mitlab.seg.ner.domain.TermNatures;
import mitlab.seg.ner.library.UserDicLibrary;

/**
 * 用户自定义词典
 */
public class UserDefineRecognition {

  private Term[] terms = null;

  private Forest[] forests = {UserDicLibrary.get()};

  private int offe = -1;
  private int endOffe = -1;
  private int tempFreq = 50;
  private String tempNature;

  private SmartForest<String[]> branch = null;
  private SmartForest<String[]> forest = null;

  private InsertTermType type = InsertTermType.SKIP;

  public UserDefineRecognition(InsertTermType type, Forest... forests) {
    this.type = type;
    if (forests != null && forests.length > 0) {
      this.forests = forests;
    }

  }

  public void recognition(Graph graph) {
    this.terms = graph.terms;
    for (Forest forest : forests) {
      if (forest == null || forest.branches == null) {
        continue;
      }
      reset();
      this.forest = forest;

      branch = forest;

      int length = terms.length - 1;

      boolean flag = true;
      for (int i = 0; i < length; i++) {
        if (terms[i] == null) {
          continue;
        }
        if (branch == forest) {
          flag = false;
        } else {
          flag = true;
        }

        branch = termStatus(branch, terms[i]);
        if (branch == null) {
          if (offe != -1) {
            i = offe;
          }
          reset();
        } else if (branch.getStatus() == 3) {
          endOffe = i;
          tempNature = branch.getParam()[0];
          tempFreq = getInt(branch.getParam()[1], 50);
          if (offe != -1 && offe < endOffe) {
            i = offe;
            makeNewTerm();
            reset();
          } else {
            reset();
          }
        } else if (branch.getStatus() == 2) {
          endOffe = i;
          if (offe == -1) {
            offe = i;
          } else {
            tempNature = branch.getParam()[0];
            tempFreq = getInt(branch.getParam()[1], 50);
            if (flag) {
              makeNewTerm();
            }
          }
        } else if (branch.getStatus() == 1) {
          if (offe == -1) {
            offe = i;
          }
        }
      }
      if (offe != -1 && offe < endOffe) {
        makeNewTerm();
      }
    }
  }

  private int getInt(String str, int def) {
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException e) {
      System.out.println(str + "不是一个数字");
      return def;
    }
  }

  private void makeNewTerm() {
    StringBuilder sb = new StringBuilder();
    for (int j = offe; j <= endOffe; j++) {
      if (terms[j] == null) {
        continue;
      } else {
        sb.append(terms[j].getName());
      }
    }

    TermNatures termNatures = new TermNatures(new TermNature(tempNature, tempFreq));
    Term term = new Term(sb.toString(), offe, termNatures);
    term.selfScore(-1 * tempFreq);
    TermUtil.insertTerm(terms, term, type);

    if (terms[offe].getRealNameIfnull() != null) { // 后面增加了非原生graph的合并，所以需要补充realname
      StringBuilder sb1 = new StringBuilder();
      for (int j = offe; j <= endOffe; j++) {
        if (terms[j] == null) {
          continue;
        } else {
          sb1.append(terms[j].getRealName());
        }
      }
      term.setRealName(sb1.toString());
    }


  }

  /**
   * 重置
   */
  private void reset() {
    offe = -1;
    endOffe = -1;
    tempFreq = 50;
    tempNature = null;
    branch = forest;
  }

  /**
   * 传入一个term 返回这个term的状态
   *
   * @param branch
   * @param term
   * @return
   */
  private SmartForest<String[]> termStatus(SmartForest<String[]> branch, Term term) {
    String name = term.getName();
    SmartForest<String[]> sf = branch;
    for (int j = 0; j < name.length(); j++) {
      sf = sf.get(name.charAt(j));
      if (sf == null) {
        return null;
      }
    }
    return sf;
  }

}
