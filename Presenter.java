import java.util.*;

public class Presenter {
    private Planner planner;

    Presenter() {
        planner = new Planner();
        planner.start();
    }

    // HashMap: 状態(String)とその状態についての変数束縛(String[])をまとめた一覧
    // LinkedHashMap: 各ステップを，HashMapと何のOperatorを使ってそこに至ったかについての一覧（ステップ順）
    // 初期状態はLinkedHashMapの1つ目の要素．Operatorはnull
    LinkedHashMap<HashMap<String, String[]>, Operator> getStepList() {
        return new LinkedHashMap<HashMap<String, String[]>, Operator>();
    }

    // オペレータ一覧の取得
    // 各OperatorはgetName(), getIfList(), getAddList(), getDeleteList()メソッドで各値を取得できる．
    ArrayList<Operator> getOperatorList() {
        return planner.operators;
    }

    // 新たなオペレータを作る
    // 戻り値は作成したオペレータインスタンス（一応）
    Operator makeOperator(String name, ArrayList<String> iflist, ArrayList<String> addList, ArrayList<String> deleteList) {
        Operator op = new Operator(name, iflist, addList, deleteList);

        // Plannnerに追加を知らせる

        return op;
    }

    // 既存のオペレータoperatorを編集する(名前以外)
    // 編集しない引数はnullでもOK
    // 戻り値は編集後のオペレータインスタンス（一応）
    Operator editOperator(Operator operator, ArrayList<String> newIflist, ArrayList<String> newAddList, ArrayList<String> newDeleteList) {
        if(newIflist != null) {
            operator.setIfList(newIflist);
        }
        if(newAddList != null) {
            operator.setAddList(newAddList);
        }
        if(newDeleteList != null) {
            operator.setDeleteList(newDeleteList);
        }

        // Plannerに変更を知らせる

        return operator;
    }

    // 既存のオペレータoperatorを削除する
    void deleteOperator(Operator operator) {

        // Plannnerから削除する
    }

    
    void setInitialState(ArrayList<String> initialState) {
        planner.initialState = initialState;
    }

    void setGoal(ArrayList<String> goalList) {
        planner.goalList = goalList;
    }

    void restart() {
        planner.start();
    }
}