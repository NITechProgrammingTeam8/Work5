import java.util.*;

public class Presenter {
    private Planner planner;

    public static void main(String args[]) {
        Presenter pre = new Presenter();
        System.out.println("!!!!!!");
        System.out.println(pre.getInitialState());
        System.out.println(pre.getGoalList());
        System.out.println(pre.getStepList().get(0).getName());
        System.out.println(pre.getStepList().get(0).getBinding());
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

    // セットした内容で再実行
    void restart() {
        planner.start();
    }
}