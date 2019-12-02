import java.util.*;

public class Presenter {
    private Planner planner;

    Presenter() {
        planner = new Planner();
        planner.start();
    }

    // デフォルトの初期状態を取得
    ArrayList<String> getinitialState() {
        return planner.initialState;
    }

    
    // デフォルトのゴールを取得
    ArrayList<String> getGoalList() {
        return planner.goalList;
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
    
    // 適用したオペレーションと変数束縛をまとめた一覧(各ステップごと))
    LinkedHashMap<Operator, HashMap<String, String>> getStepList() {
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