import java.util.*;

public class Presenter {
    private Planner planner;

    public static void main(String args[]) {
        Presenter pre = new Presenter();
        System.out.println("-----on Presenter-----");
        System.out.println(pre.getAttributeInitialState());
        System.out.println(pre.getAttributeGoalList());
        System.out.println(pre.getStepList().get(0).getName());
        System.out.println(pre.getStepList().get(0).getBindings());
    }

    Presenter() {
        planner = new Planner();
        planner.start();
    }

    // デフォルトの初期状態を取得
    ArrayList<String> getInitialState() {
        return planner.initInitialState();
    }

    // デフォルトのゴールを取得
    ArrayList<String> getGoalList() {
        return planner.initGoalList();
    }

    // デフォルトの初期状態を取得（属性版）
    ArrayList<String> getAttributeInitialState() {
        return planner.initAttributeInitialState();
    }

    // デフォルトのゴールを取得
    ArrayList<String> getAttributeGoalList() {
        return planner.initAttributeGoalList();
    }

    // オペレータ一覧の取得
    // 各OperatorはgetName(), getIfList(), getAddList(), getDeleteList()メソッドで各値を取得できる．
    ArrayList<Operator> getOperatorList() {
        return planner.operators;
    }

    // 過程を文字列で取得
    ArrayList<String> getPlan() {
        return planner.planResult;
    }

    // 適用したオペレーション(各ステップごと)
    // getBinding()で変数束縛のHashMap<String, String>を取得できる．
    ArrayList<Operator> getStepList() {
        return planner.planUnifiedResult;
    }

    // 初期状態をセット
    void setInitialState(ArrayList<String> initialState) {
        planner.initialState = initialState;
    }

    // ゴールをセット
    void setGoalList(ArrayList<String> goalList) {
        planner.goalList = goalList;
    }

    // 属性をセット（自然言語）
    void setAttribution(ArrayList<String> attributions) {
        planner.attributions = new Attributions(attributions);
    }
    
    // セットした内容で再実行
    void restart() {
        planner.start();
    }
}