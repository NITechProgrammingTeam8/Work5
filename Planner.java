import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import sun.jvm.hotspot.utilities.IntArray;

/*
class Store{

	HashMap<String, String> storeHashMap;

	Store(){
	}

	public void set(HashMap<String, String> hash) {
		this.storeHashMap =  hash;
	}
}
*/

public class Planner {
	ArrayList<Operator> operators;
	Random rand;
	ArrayList<Operator> plan;
	ArrayList<String> goalList;
	ArrayList<String> initialState;
	Attributions attributions;
	ArrayList<String> planResult;
	ArrayList<Operator> planUnifiedResult;
	HashMap<String, String> p_productKeyOnValue;
	//記憶用変数
	ArrayList<Integer> opIndex;
	ArrayList<String> mGoal;
	ArrayList<ArrayList<String>> mState;
	

	public static void main(String argv[]) {
		(new Planner()).start();
	}

	Planner() {
		rand = new Random();
		initOperators();
		attributions = new Attributions();

		//AttributesクラスからPlannerクラスへ
		setHashMap(attributions.a_productKeyOnValue);
		/*
		goalList = sortGoalList(initGoalList());
		initialState = initInitialState();
		*/
		//ゴールと初期状態に属性をしてする場合
		//
		System.out.println("コンストラクタで初期化します");
		goalList = sortGoalList(attributions.editStatementList(initAttributeGoalList()));
		initialState = attributions.editStatementList(initAttributeInitialState());
		System.out.println("コンストラクタを終了します");
		//System.out.println("作った禁止制約のHashMap = " + attributions.keyValueProhibit);
	}


	public HashMap<String, String> getHashMap(){
		return p_productKeyOnValue;
	}

	public void setHashMap(HashMap<String, String> pHashMap){
		this.p_productKeyOnValue = pHashMap;
	}

	public void start() {
		HashMap<String, String> theBinding = new HashMap();
	
		plan = new ArrayList<Operator>();
		System.out.println("goalList = \n" + goalList);
		System.out.println("initGoalList() = \n" + initGoalList());
		if(goalList.size() < initGoalList().size()) {
			System.out.println("禁止制約によってゴールが成立しなくなりました");
			planResult = null;
			planUnifiedResult  = null;
			return;
		}
		if(initialState.size() == 0) {
			System.out.println("初期状態がありません");
			planResult = null;
			planUnifiedResult  = null;
			return;
		}
		planning(goalList, initialState, theBinding);

		System.out.println("***** This is a plan! *****");
		planResult = new ArrayList<>();
		planUnifiedResult  = new ArrayList<>();
		for (int i = 0; i < plan.size(); i++) {
			Operator op = (Operator) plan.get(i);
			Operator result = (op.instantiate(theBinding));
			if(!result.name.contains("?")) {
				System.out.println(result.name);
			}
			planResult.add(result.name);
			for(Operator initOp : operators) {
				Unifier unifier = new Unifier();
				if(unifier.unify(result.name, initOp.getName())) {
					planUnifiedResult.add(new Operator(initOp, unifier.getVars()));
					break;
				}
			}
		}
	}

	private boolean planning(ArrayList<String> theGoalList, ArrayList<String> theCurrentState, HashMap theBinding) {
		//繰り返し処理用変数のコンストラクタ生成
		opIndex = new ArrayList<Integer>();
		mGoal = new ArrayList<String>();
		mState = new ArrayList<ArrayList<String>>();
		
		System.out.println("*** GOALS ***" + theGoalList);
		if (theGoalList.size() == 1) {
			String aGoal = (String) theGoalList.get(0);
			if (planningAGoal(aGoal, theCurrentState, theBinding, 0) != -1) {
				return true;
			} else {
				return false;
			}
		} else {
			String aGoal = (String) theGoalList.get(0);
			int cPoint = 0;
			while (cPoint < operators.size()) {
				// System.out.println("cPoint:"+cPoint);
				// Store original binding
				HashMap orgBinding = new HashMap();
				for (Iterator e = theBinding.keySet().iterator(); e.hasNext();) {
					String key = (String) e.next();
					String value = (String) theBinding.get(key);
					orgBinding.put(key, value);
				}
				ArrayList<String> orgState = new ArrayList<String>();
				for (int i = 0; i < theCurrentState.size(); i++) {
					orgState.add(theCurrentState.get(i));
				}

				int tmpPoint = planningAGoal(aGoal, theCurrentState, theBinding, cPoint);
				// System.out.println("tmpPoint: "+tmpPoint);
				if (tmpPoint != -1) {
					theGoalList.remove(0);
					System.out.println(theCurrentState);

					//ここの条件分岐の役割は...
					if (planning(theGoalList, theCurrentState, theBinding)) {
						System.out.println("Success !");
						return true;
					} else {
						cPoint = tmpPoint;
						// System.out.println("Fail::"+cPoint);
						theGoalList.add(0, aGoal);

						theBinding.clear();
						for (Iterator e = orgBinding.keySet().iterator(); e.hasNext();) {
							String key = (String) e.next();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.clear();
						for (int i = 0; i < orgState.size(); i++) {
							theCurrentState.add(orgState.get(i));
						}
					}
				} else {
					theBinding.clear();
					for (Iterator e = orgBinding.keySet().iterator(); e.hasNext();) {
						String key = (String) e.next();
						String value = (String) orgBinding.get(key);
						theBinding.put(key, value);
					}
					theCurrentState.clear();
					for (int i = 0; i < orgState.size(); i++) {
						theCurrentState.add(orgState.get(i));
					}
					return false;
				}
			}
			return false;
		}
	}

	private int planningAGoal(String theGoal, ArrayList<String> theCurrentState, HashMap theBinding, int cPoint) {
		System.out.println("**" + theGoal);
		int size = theCurrentState.size();
		for (int i = 0; i < size; i++) {
			String aState = (String) theCurrentState.get(i);

			//System.out.println("Unify前のp_productKeyOnValue=" + p_productKeyOnValue);
			//現在のKeyOnValueリストをUnifierクラスからもらってくるため
			Unifier unification = new Unifier();
			if (unification.unify(theGoal, aState, theBinding, attributions.keyValueProhibit, p_productKeyOnValue)) {
				System.out.println("theBinding" + theBinding);

				//UnifierからもらったKeyOnValueリストを,Plannerクラスに保存
				//for(HashMap.Entry<String, String> entry : unification.getHashMap().entrySet()){
				for(String str : unification.getHashMap().keySet()) {
					System.out.println("Key = " + str + " Value = " + unification.getHashMap().get(str));
					p_productKeyOnValue.put(str, unification.getHashMap().get(str));
				}
				return 0;
			}
		}

		/**********************オペレータの選択********************************************/
		/*
		//1.ランダム用
		//
		int randInt = Math.abs(rand.nextInt()) % operators.size();
  		Operator op = (Operator)operators.get(randInt);
		operators.remove(randInt);
		operators.add(op);
		//
		*/

		//
		//現状態と目標の表示
		ArrayList<String> cState = new ArrayList<String>();
		System.out.println("現在の目標\n" + theGoal);
		System.out.println("現在の状態");
		for(int i = 0; i< theCurrentState.size(); i++) {
			if(!theCurrentState.get(i).contains("?")) {
				cState.add(theCurrentState.get(i));
				System.out.println(theCurrentState.get(i));
			}
		}

		
		if(opIndex.size() == 0){
			//一回め
			int randInt = Math.abs(rand.nextInt()) % operators.size();
  			Operator op = (Operator)operators.get(randInt);
			operators.remove(randInt);
			operators.add(op);

			opIndex.add(randInt);
			mGoal.add(theGoal);
			mState.add(cState);
		}

		else{
			//二回目以降
			int flag = 0;
			while(flag == 0){
				//オペレータを選択するための変数を生成
				int randInt = Math.abs(rand.nextInt()) % operators.size();
				//randIntがpoIndexにあるか判定、なかったらそのまま実行
				if(!(opIndex.contains(randInt))){
					Operator op = (Operator)operators.get(randInt);
					operators.remove(randInt);
					operators.add(op);
					//opIndex,state,goalを記憶
					opIndex.add(randInt);
					mGoal.add(theGoal);
					mState.add(cState);
					flag = 1;
				}

				//同じpoIndexで同じcGoalがあるか判定、なかったらそのまま実行
				for(int i = 0; i < opIndex.size(); i++){
					//opIndexとrandIntが同じで
					if(opIndex.get(i) == randInt){
						//その時のmGoalがtheGoalと同じなら
						if(mGoal.get(i) == theGoal){
						
							//cStateを比較
							for(String s : mState.get(i)){
								if(!(cState.contains(s))){
									Operator op = (Operator)operators.get(randInt);
									operators.remove(randInt);
									operators.add(op);
									//opIndex,state,goalを記憶
									opIndex.add(randInt);
									mGoal.add(theGoal);
									mState.add(cState);
									flag = 1;
								}
							}
							for(String s : cState){
								if(!(mState.get(i)).contains(s)){
									Operator op = (Operator)operators.get(randInt);
									operators.remove(randInt);
									operators.add(op);
									//opIndex,state,goalを記憶
									opIndex.add(randInt);
									mGoal.add(theGoal);
									mState.add(cState);
									flag = 1;
								}
							}
						}
					}
				}
			}
		}
		

		/*
		//3.その他開発用  → おすすめ表示用に！
		int numRecOp = RecommentOperator(theGoal);
		Operator opRec = (Operator)operators.get(numRecOp);
		System.out.println("おすすめは = " + opRec.name);
		 */

		/*2.発展課題5-6用
		int numOp = SelectOperatorNL();

		// 2.3を使うときは,このコメントアウトを外してね！
		Operator op = (Operator)operators.get(numOp);
		System.out.println("オペレータ内容は = " + op.name);
		System.out.println("Thank you!");
		cPoint = numOp;
		*/
		/*****************************************************************************/

		//1.まずは選択したオペレータを動かし,
		Operator anOperator = rename((Operator) operators.get(cPoint));
		System.out.println("選択したオペレータ"+ cPoint +":\n"+anOperator);
		// 現在のCurrent state, Binding, planをbackup
		HashMap orgBinding = new HashMap();
		for (Iterator e = theBinding.keySet().iterator(); e.hasNext();) {
			String key = (String) e.next();
			String value = (String) theBinding.get(key);
			orgBinding.put(key, value);
		}
		ArrayList<String> orgState = new ArrayList<String>();
		for (int j = 0; j < theCurrentState.size(); j++) {
			orgState.add(theCurrentState.get(j));
		}
		ArrayList<Operator> orgPlan = new ArrayList<Operator>();
		for (int j = 0; j < plan.size(); j++) {
			orgPlan.add(plan.get(j));
		}

		ArrayList<String> addList = (ArrayList<String>) anOperator.getAddList();
		for (int j = 0; j < addList.size(); j++) {
			//オペレータaddリストに,オペレータと一致するものがあれば,
			//System.out.println("Unify前のp_productKeyOnValue=" + p_productKeyOnValue);
			Unifier unification = new Unifier();
			if (unification.unify(theGoal, (String) addList.get(j), theBinding, attributions.keyValueProhibit, p_productKeyOnValue)) {
				//UnifierからもらったKeyOnValueリストを,Plannerクラスに保存
				for(String str : unification.getHashMap().keySet()) {
					System.out.println("Key = " + str + " Value = " + unification.getHashMap().get(str));
					p_productKeyOnValue.put(str, unification.getHashMap().get(str));
				}
				System.out.println("unify成功");
				Operator newOperator = anOperator.instantiate(theBinding);
				//そのオペレータのIF部を副目標として加え,
				ArrayList<String> newGoals = (ArrayList<String>) newOperator.getIfList();
				System.out.println("オペレータの具体化:" +newOperator.name);
				System.out.println("その時の状態:" + theCurrentState);
				//その副目標が達成されたら,
				if (planning(newGoals, theCurrentState, theBinding)) {
					System.out.println("副目標達成\n" + newOperator.name);
					//そのオペレータを加え,
					System.out.println("newOperator = " + newOperator);
					plan.add(newOperator);
					//状態を変更
					theCurrentState = newOperator.applyState(theCurrentState);
					/**********************************************************************************/
					//まっす～の"禁止制約をはじくメソッド"追加
					theCurrentState = attributions.checkStates(theCurrentState);
					/**********************************************************************************/

					return cPoint + 1;
				} else {
					// 失敗したら元に戻す．
					System.out.println("副目標失敗");
					theBinding.clear();
					for (Iterator e = orgBinding.keySet().iterator(); e.hasNext();) {
						String key = (String) e.next();
						String value = (String) orgBinding.get(key);
						theBinding.put(key, value);
					}
					theCurrentState.clear();
					for (int k = 0; k < orgState.size(); k++) {
						theCurrentState.add(orgState.get(k));
					}
					plan.clear();
					for (int k = 0; k < orgPlan.size(); k++) {
						plan.add(orgPlan.get(k));
					}
				}
			}
		}

		//2.その後,他のオペレータを試す
		System.out.println("そのオペレータは実行できません");
		System.out.println("おすすめのオペレータを使います");
		for (int i = 0; i < operators.size(); i++) {
			if(i != cPoint) {
			anOperator = rename((Operator) operators.get(i));
			System.out.println("オペレータ"+i+":\n"+anOperator);
			// 現在のCurrent state, Binding, planをbackup
			orgBinding = new HashMap();
			for (Iterator e = theBinding.keySet().iterator(); e.hasNext();) {
				String key = (String) e.next();
				String value = (String) theBinding.get(key);
				orgBinding.put(key, value);
			}
			orgState = new ArrayList<String>();
			for (int j = 0; j < theCurrentState.size(); j++) {
				orgState.add(theCurrentState.get(j));
			}
			orgPlan = new ArrayList<Operator>();
			for (int j = 0; j < plan.size(); j++) {
				orgPlan.add(plan.get(j));
				//System.out.println("ここかな？"+plan.get(j));
			}

			addList = (ArrayList<String>) anOperator.getAddList();
			for (int j = 0; j < addList.size(); j++) {
				//オペレータaddリストに,オペレータと一致するものがあれば,
				//System.out.println("Unify前のp_productKeyOnValue=" + p_productKeyOnValue);
				Unifier unification = new Unifier();
				if (unification.unify(theGoal, (String) addList.get(j), theBinding, attributions.keyValueProhibit, p_productKeyOnValue)) {
					//UnifierからもらったKeyOnValueリストを,Plannerクラスに保存
					System.out.println("attributions.keyValueProhibit" + attributions.keyValueProhibit);
					System.out.println("サイズ=" + unification.getHashMap().size());
					for(String str : unification.getHashMap().keySet()) {
						System.out.println("Key = " + str + " Value = " + unification.getHashMap().get(str));
						p_productKeyOnValue.put(str, unification.getHashMap().get(str));
					}
					System.out.println("unify成功");
					Operator newOperator = anOperator.instantiate(theBinding);
					//そのオペレータのIF部を副目標として加え,
					ArrayList<String> newGoals = (ArrayList<String>) newOperator.getIfList();
					System.out.println("オペレータの具体化:" +newOperator.name);
					System.out.println("その時の状態:" + theCurrentState);
					//その副目標が達成されたら,
					if (planning(newGoals, theCurrentState, theBinding)) {
						System.out.println("副目標達成\n" + newOperator.name);
						//そのオペレータを加え,
						System.out.println("newOperator = " + newOperator);
						plan.add(newOperator);
						//状態を変更
						theCurrentState = newOperator.applyState(theCurrentState);
						/**********************************************************************************/
						//まっす～の"禁止制約をはじくメソッド"追加
						theCurrentState = attributions.checkStates(theCurrentState);
						/**********************************************************************************/

						return i + 1;
					} else {
						// 失敗したら元に戻す．
						System.out.println("副目標失敗");
						theBinding.clear();
						for (Iterator e = orgBinding.keySet().iterator(); e.hasNext();) {
							String key = (String) e.next();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.clear();
						for (int k = 0; k < orgState.size(); k++) {
							theCurrentState.add(orgState.get(k));
						}
						plan.clear();
						for (int k = 0; k < orgPlan.size(); k++) {
							plan.add(orgPlan.get(k));
						}
					}
				}
			}
		}
		}
		return -1;
	}

   /*
	* 最適な操作をできるようなオペレータの選択
	*  仮引数  : theGoalの内容
	*  return: オペレータの番号
	*/
	private int RecommentOperator(String theGoal) {
		int opNumber = 0;
		if(theGoal.contains("on")) {
			opNumber = 0;
		}
		else if(theGoal.contains("holding")) {
			opNumber = 2;
		}
		return opNumber;
	}


   /*
	* 自然言語の命令によってオペレータの選択
	*  return オペレータの番号
	*/
	private int SelectOperatorNL() {
		int opNumber = 0;
		String opString = null;
		Scanner scanner = new Scanner(System.in);
		System.out.println("行う操作を入力してください");
		opString = scanner.nextLine();

		if(opString.contains("Place")) {
			opNumber = 0;
		}
		else if(opString.contains("remove")) {
			opNumber = 1;
		}
		else if(opString.contains("pick")) {
			opNumber = 2;
		}
		else if(opString.contains("put")) {
			opNumber = 3;
		}
	 	return	opNumber;
	}

	int uniqueNum = 0;

	private Operator rename(Operator theOperator) {
		Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
		uniqueNum = uniqueNum + 1;
		return newOperator;
	}

	public ArrayList<String> initGoalList() {
		ArrayList<String> goalList = new ArrayList<String>();
		goalList.add("B on C");
        goalList.add("A on B");
		//goalList.add("ontable A");
		//goalList.add("ontable B");
		//goalList.add("ontable C");
		return goalList;
	}

	//目標状態を問題が起こらないように並べ替える（課題５−1）
	public ArrayList<String> sortGoalList(ArrayList<String> goalList){
		ArrayList<String> sortedGoalList = new ArrayList<String>();
		for(String s : goalList) {
			sortedGoalList.add(s);
		}

		for(int k = 0; k < sortedGoalList.size(); k++){
			String[] head = new String[sortedGoalList.size()];
			String[] tail = new String[sortedGoalList.size()];

			//各目標状態の先頭と末尾の文字を配列に格納
			for(int i = 0; i < sortedGoalList.size(); i++){
				head[i] = sortedGoalList.get(i).substring(0,1);
				tail[i] = sortedGoalList.get(i).substring(sortedGoalList.get(i).length()-1);
			}

			int flag = 0;
			for(int i = 0; i < sortedGoalList.size(); i++){
				for(int j = i; j < sortedGoalList.size()-i; j++){
					if(tail[i] == head[j]){
						sortedGoalList.add(j+1, sortedGoalList.get(i));
						sortedGoalList.remove(i);
						flag += 1;
						break;
					}
				}
				if(flag == 1){
					break;
				}			
			}
			if(flag == 0){
				break;
			}

		}

		return sortedGoalList;
	}

	public ArrayList<String> initAttributeGoalList() {
		ArrayList<String> goalList = new ArrayList<String>();
		goalList.add("trapezoid on box");
		goalList.add("ball on trapezoid");
		// for(String goal: goalList) {
		// 	System.out.println("========== goal:"+goal+" ==========");
		// }
		return goalList;
	}

	public ArrayList<String> initInitialState() {
		ArrayList<String> initialState = new ArrayList<String>();
		initialState.add("clear A");
		initialState.add("clear B");
		initialState.add("clear C");

		initialState.add("ontable A");
		initialState.add("ontable B");
		//initialState.add("B on C");
		//initialState.add("A on B");
		initialState.add("ontable C");
		initialState.add("handEmpty");
		return initialState;
	}

	public ArrayList<String> initAttributeInitialState() {
		ArrayList<String> initialState = new ArrayList<String>();
		initialState.add("clear blue");
		initialState.add("clear green");
		initialState.add("clear red");

		initialState.add("ontable ball");
		initialState.add("ontable trapezoid");
		initialState.add("ontable box");
		initialState.add("ontable pyramid");
		initialState.add("handEmpty");
		// for(String state: initialState) {
		// 	System.out.println("---------- initInitialState:"+state+" ----------");
		// }
		return initialState;
	}

	private void initOperators() {
		operators = new ArrayList<Operator>();

		// OPERATOR 1
		/// NAME
		String name1 = new String("Place ?x on ?y");
		/// IF
		ArrayList<String> ifList1 = new ArrayList<String>();
		ifList1.add(new String("clear ?y"));
		ifList1.add(new String("holding ?x"));
		/// ADD-LIST
		ArrayList<String> addList1 = new ArrayList<String>();
		addList1.add(new String("?x on ?y"));
		addList1.add(new String("clear ?x"));
		addList1.add(new String("handEmpty"));
		/// DELETE-LIST
		ArrayList<String> deleteList1 = new ArrayList<String>();
		deleteList1.add(new String("clear ?y"));
		deleteList1.add(new String("holding ?x"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1);
		operators.add(operator1);

		// OPERATOR 2
		/// NAME
		String name2 = new String("remove ?x from on top ?y");
		/// IF
		ArrayList<String> ifList2 = new ArrayList<String>();
		ifList2.add(new String("?x on ?y"));
		ifList2.add(new String("clear ?x"));
		ifList2.add(new String("handEmpty"));
		/// ADD-LIST
		ArrayList<String> addList2 = new ArrayList<String>();
		addList2.add(new String("clear ?y"));
		addList2.add(new String("holding ?x"));
		/// DELETE-LIST
		ArrayList<String> deleteList2 = new ArrayList<String>();
		deleteList2.add(new String("?x on ?y"));
		deleteList2.add(new String("clear ?x"));
		deleteList2.add(new String("handEmpty"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);
		operators.add(operator2);

		// OPERATOR 3
		/// NAME
		String name3 = new String("pick up ?x from the table");
		/// IF
		ArrayList<String> ifList3 = new ArrayList<String>();
		ifList3.add(new String("ontable ?x"));
		ifList3.add(new String("clear ?x"));
		ifList3.add(new String("handEmpty"));
		/// ADD-LIST
		ArrayList<String> addList3 = new ArrayList<String>();
		addList3.add(new String("holding ?x"));
		/// DELETE-LIST
		ArrayList<String> deleteList3 = new ArrayList<String>();
		deleteList3.add(new String("ontable ?x"));
		deleteList3.add(new String("clear ?x"));
		deleteList3.add(new String("handEmpty"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
		operators.add(operator3);

		// OPERATOR 4
		/// NAME
		String name4 = new String("put ?x down on the table");
		/// IF
		ArrayList<String> ifList4 = new ArrayList<String>();
		ifList4.add(new String("holding ?x"));
		/// ADD-LIST
		ArrayList<String> addList4 = new ArrayList<String>();
		addList4.add(new String("ontable ?x"));
		addList4.add(new String("clear ?x"));
		addList4.add(new String("handEmpty"));
		/// DELETE-LIST
		ArrayList<String> deleteList4 = new ArrayList<String>();
		deleteList4.add(new String("holding ?x"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4);
		operators.add(operator4);

		/*
		// OPERATOR 5
		/// NAME
		String name5 = new String("Place ?y on ?x");		//ここだけ,{x,y}を入れ替える??
		/// IF
		ArrayList<String> ifList5 = new ArrayList<String>();
		ifList5.add(new String("clear ?y"));
		ifList5.add(new String("holding ?x"));
		/// ADD-LIST
		ArrayList<String> addList5 = new ArrayList<String>();
		addList5.add(new String("?x on ?y"));
		addList5.add(new String("clear ?x"));
		addList5.add(new String("handEmpty"));
		/// DELETE-LIST
		ArrayList<String> deleteList5 = new ArrayList<String>();
		deleteList5.add(new String("clear ?y"));
		deleteList5.add(new String("holding ?x"));
		Operator operator5 = new Operator(name5, ifList5, addList5, deleteList5);
		operators.add(operator5);

		// OPERATOR 6
		/// NAME
		String name6 = new String("remove ?y from on top ?x");
		/// IF
		ArrayList<String> ifList6 = new ArrayList<String>();
		ifList6.add(new String("?x on ?y"));
		ifList6.add(new String("clear ?x"));
		ifList6.add(new String("handEmpty"));
		/// ADD-LIST
		ArrayList<String> addList6 = new ArrayList<String>();
		addList6.add(new String("clear ?y"));
		addList6.add(new String("holding ?x"));
		/// DELETE-LIST
		ArrayList<String> deleteList6 = new ArrayList<String>();
		deleteList6.add(new String("?x on ?y"));
		deleteList6.add(new String("clear ?x"));
		deleteList6.add(new String("handEmpty"));
		Operator operator6 = new Operator(name6, ifList6, addList6, deleteList6);
		operators.add(operator6);
		*/
	}
}

class Operator {
	String name;
	ArrayList<String> ifList;
	ArrayList<String> addList;
	ArrayList<String> deleteList;
	HashMap<String, String> bindings;

	Operator(String theName, ArrayList<String> theIfList, ArrayList<String> theAddList, ArrayList<String> theDeleteList) {
		name = theName;
		ifList = theIfList;
		addList = theAddList;
		deleteList = theDeleteList;
	}

	Operator(Operator op, HashMap<String, String> theBindings) {
		this(op.getName(), op.getIfList(), op.getAddList(), op.getDeleteList());
		bindings = theBindings;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getAddList() {
		return addList;
	}

	public ArrayList<String> getDeleteList() {
		return deleteList;
	}

	public ArrayList<String> getIfList() {
		return ifList;
	}

	public HashMap<String, String> getBindings() {
		return bindings;
	}

	public String toString() {
		String result = "NAME: " + name + "\n" + "IF :" + ifList + "\n" + "ADD:" + addList + "\n" + "DELETE:"
				+ deleteList;
		return result;
	}

	public ArrayList<String> applyState(ArrayList<String> theState) {
		for (int i = 0; i < addList.size(); i++) {
			theState.add(addList.get(i));
		}
		for (int i = 0; i < deleteList.size(); i++) {
			theState.remove(deleteList.get(i));
		}
		return theState;
	}

	public Operator getRenamedOperator(int uniqueNum) {
		ArrayList<String> vars = new ArrayList<String>();
		// IfListの変数を集める
		for (int i = 0; i < ifList.size(); i++) {
			String anIf = (String) ifList.get(i);
			vars = getVars(anIf, vars);
		}
		// addListの変数を集める
		for (int i = 0; i < addList.size(); i++) {
			String anAdd = (String) addList.get(i);
			vars = getVars(anAdd, vars);
		}
		// deleteListの変数を集める
		for (int i = 0; i < deleteList.size(); i++) {
			String aDelete = (String) deleteList.get(i);
			vars = getVars(aDelete, vars);
		}
		HashMap renamedVarsTable = makeRenamedVarsTable(vars, uniqueNum);

		// 新しいIfListを作る
		ArrayList<String> newIfList = new ArrayList<String>();
		for (int i = 0; i < ifList.size(); i++) {
			String newAnIf = renameVars((String) ifList.get(i), renamedVarsTable);
			newIfList.add(newAnIf);
		}
		// 新しいaddListを作る
		ArrayList<String> newAddList = new ArrayList<String>();
		for (int i = 0; i < addList.size(); i++) {
			String newAnAdd = renameVars((String) addList.get(i), renamedVarsTable);
			newAddList.add(newAnAdd);
		}
		// 新しいdeleteListを作る
		ArrayList<String> newDeleteList = new ArrayList<String>();
		for (int i = 0; i < deleteList.size(); i++) {
			String newADelete = renameVars((String) deleteList.get(i), renamedVarsTable);
			newDeleteList.add(newADelete);
		}
		// 新しいnameを作る
		String newName = renameVars(name, renamedVarsTable);

		return new Operator(newName, newIfList, newAddList, newDeleteList);
	}

	private ArrayList<String> getVars(String thePattern, ArrayList<String> vars) {
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				vars.add(tmp);
			}
		}
		return vars;
	}

	private HashMap makeRenamedVarsTable(ArrayList<String> vars, int uniqueNum) {
		HashMap result = new HashMap();
		for (int i = 0; i < vars.size(); i++) {
			String newVar = (String) vars.get(i) + uniqueNum;
			result.put((String) vars.get(i), newVar);
		}
		return result;
	}

	private String renameVars(String thePattern, HashMap renamedVarsTable) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				result = result + " " + (String) renamedVarsTable.get(tmp);
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	public Operator instantiate(HashMap theBinding) {
		// name を具体化
		String newName = instantiateString(name, theBinding);
		// ifList を具体化
		ArrayList<String> newIfList = new ArrayList<String>();
		for (int i = 0; i < ifList.size(); i++) {
			String newIf = instantiateString((String) ifList.get(i), theBinding);
			newIfList.add(newIf);
		}
		// addList を具体化
		ArrayList<String> newAddList = new ArrayList<String>();
		for (int i = 0; i < addList.size(); i++) {
			String newAdd = instantiateString((String) addList.get(i), theBinding);
			newAddList.add(newAdd);
		}
		// deleteListを具体化
		ArrayList<String> newDeleteList = new ArrayList<String>();
		for (int i = 0; i < deleteList.size(); i++) {
			String newDelete = instantiateString((String) deleteList.get(i), theBinding);
			newDeleteList.add(newDelete);
		}
		return new Operator(newName, newIfList, newAddList, newDeleteList);
	}

	private String instantiateString(String thePattern, HashMap theBinding) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				String newString = (String) theBinding.get(tmp);
				if (newString == null) {
					result = result + " " + tmp;
				} else {
					result = result + " " + newString;  // 変数を具体化
				}
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	private boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}
}

class Unifier {
	StringTokenizer st1;
	String buffer1[];
	StringTokenizer st2;
	String buffer2[];
	HashMap<String, String> vars;
	HashMap<String, List<String>> prohibit;
	HashMap<String, String> productKeyOnValue;

	Unifier() {
		vars = new HashMap<String, String>();
		prohibit = new HashMap<String, List<String>>();
	}

	//生成したKeyOnValueリストをPlannerクラスに返すよ！
	public HashMap<String, String> getHashMap(){
		return productKeyOnValue;
	}

	public void setHashMap(HashMap<String, String> uHashMap){
		this.productKeyOnValue = uHashMap;
	}


	public boolean unify(String string1, String string2, HashMap<String, String> theBindings, HashMap<String, List<String>> KeyonProhibit, HashMap<String, String> productKeyOnValue) {
		this.prohibit = KeyonProhibit;
		this.productKeyOnValue = productKeyOnValue;
		HashMap<String, String> orgBindings = new HashMap<String, String>();
		for (Iterator e = theBindings.keySet().iterator(); e.hasNext();) {
			String key = (String) e.next();
			String value = (String) theBindings.get(key);
			orgBindings.put(key, value);
		}
		this.vars = theBindings;
		if (unify(string1, string2)) {
			return true;
		} else {
			// 失敗したら元に戻す．
			theBindings.clear();
			for (Iterator e = orgBindings.keySet().iterator(); e.hasNext();) {
				String key = (String) e.next();
				String value = (String) orgBindings.get(key);
				theBindings.put(key, value);
			}
			return false;
		}
	}

	public boolean unify(String string1, String string2) {
		// 同じなら成功
		if (string1.equals(string2))
			return true;

		// 各々トークンに分ける
		st1 = new StringTokenizer(string1);
		st2 = new StringTokenizer(string2);

		// 数が異なったら失敗
		if (st1.countTokens() != st2.countTokens())
			return false;

		// 定数同士
		int length = st1.countTokens();
		buffer1 = new String[length];
		buffer2 = new String[length];
		for (int i = 0; i < length; i++) {
			buffer1[i] = st1.nextToken();
			buffer2[i] = st2.nextToken();
		}

		// 初期値としてバインディングが与えられていたら
		if (this.vars.size() != 0) {
			for (Iterator keys = vars.keySet().iterator(); keys.hasNext();) {
				String key = (String) keys.next();
				String value = (String) vars.get(key);
				replaceBuffer(key, value);
			}
		}

		//AttributionsクラスのKey制約を持ってくる
		HashMap<String, List<String>> keyProhibit = prohibit;
		if(productKeyOnValue != null) {
			System.out.println("unify内:keyProhibit" + keyProhibit);
		}
		//これから調べる「Key on Value」の値を格納
		String[] keyOnValue = new String[2];

		for (int i = 0; i < length; i++) {
			if (!tokenMatching(buffer1[i], buffer2[i])) {
				return false;
			}
			//「x on y」の場合, 「keyOnValue[0]="x"(Keyとして), keyOnValue[1]="y"(Value)」を代入
			if(buffer1[1].equals("on")){
			 // 「B on C」と「?x on ?y」を考えて,
				if(i == 0){
					//Key.set(定数のほう:buffer2[i]=B)かな...
					keyOnValue[0] = buffer2[i];
					System.out.println("keyOnValue[0] = " + keyOnValue[0]);
				}
				else if(i == 2){
					//Value.set(定数のほう:buffer2[i]=C)かな...
					keyOnValue[1] = buffer2[i];
					System.out.println("keyOnValue[1] = " + keyOnValue[1]);
				}
			}
		}
		//保存
		if(buffer1[1].equals("on")){
			productKeyOnValue.put(keyOnValue[0], keyOnValue[1]);
			System.out.println("productKeyOnValue = " + productKeyOnValue);
		}

		//このクラスで保存した内容は他のクラスへ
		//Store store = new Store();
		//store.set(productKeyOnValue);
		//setHashMap(productKeyOnValue);


		//Attributesクラスの禁止制約と比較していく
		for(String str : keyProhibit.keySet()){	//Init_Keyの要素分
			System.out.println("Init_Key(str) = " + str);
			if(str.equals(keyOnValue[0])){	//Init_Keyの要素.equlas(this_Keyの要素)
				//for(){ //そのInit_KeyのInit_Valueの要素分
				for(int num = 0; num < keyProhibit.get(str).size(); num++)
				if(keyProhibit.get(str).get(num).equals(keyOnValue[1])){ //Init_Valueの要素.equlas(this_Valueの要素)
					System.out.println("Init_Value = " + keyProhibit.get(str));
					return false;
				}
				//}
			}
		}



		// System.out.println(vars.toString());
		return true;
	}

	boolean tokenMatching(String token1, String token2) {
		if (token1.equals(token2))
			return true;
		if (var(token1) && !var(token2))
			return varMatching(token1, token2);
		if (!var(token1) && var(token2))
			return varMatching(token2, token1);
		if (var(token1) && var(token2))
			return varMatching(token1, token2);
		return false;
	}

	boolean varMatching(String vartoken, String token) {
		if (vars.containsKey(vartoken)) {
			if (token.equals(vars.get(vartoken))) {
				return true;
			} else {
				return false;
			}
		} else {
			replaceBuffer(vartoken, token);
			if (vars.containsValue(vartoken)) {
				replaceBindings(vartoken, token);
			}

			if(productKeyOnValue != null) {
				System.out.println("varMatchingで..." + vartoken +"と"+ token);
				System.out.println("productKeyOnValueで..." + productKeyOnValue);
				for(String str1 : productKeyOnValue.keySet()){	//product_KeyValueの数
					//ここは,Valueに変数「?y2」が入っているとする
					if(var(productKeyOnValue.get(str1))) {
				  	//if((product_KeyValueのValue要素).equals(vartoken)){
				 			//そのvartokenとtokenの組み合わせが,Init_KeyValueと同じだったら...
				 			for(String str2 : prohibit.keySet()){  //Init_KeyValueの要素数
				 				System.out.println("str2 = " + str2);
				 				for(int num = 0; num < prohibit.get(str2).size(); num++) {
				 					System.out.println("prohibit.get(str2).get("+num+") = " + prohibit.get(str2).get(num));
				 					if(str2.equals(str1) & prohibit.get(str2).get(num).equals(token)){
				 						return false;
				 					}
				 				}
				 			}
				 		//}
					}
				  }
			}
			vars.put(vartoken, token);
		}
		return true;
	}

	void replaceBuffer(String preString, String postString) {
		for (int i = 0; i < buffer1.length; i++) {
			if (preString.equals(buffer1[i])) {
				buffer1[i] = postString;
			}
			if (preString.equals(buffer2[i])) {
				buffer2[i] = postString;
			}
		}
	}

	void replaceBindings(String preString, String postString) {
		Iterator keys;
		for (keys = vars.keySet().iterator(); keys.hasNext();) {
			String key = (String) keys.next();
			if (preString.equals(vars.get(key))) {
				vars.put(key, postString);
			}
		}
	}

	boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}

	HashMap<String, String> getVars() {
		return vars;
	}
}

class Attributions {
	// HashMap<String, List<String>> attributions = new HashMap();
	HashMap<String, String> attributions =  new HashMap();
	List<String> rules = new ArrayList();
	ArrayList<String> prohibitRules = new ArrayList<String>();
	ArrayList<String> prohibitBlockStates = new ArrayList<String>();
	//HashMap<String,String> keyValueProhibit =  new HashMap<String, String>();
	HashMap<String,List<String>> keyValueProhibit =  new HashMap<String, List<String>>();
	HashMap<String, String> a_productKeyOnValue = new HashMap<String, String>();
	// デフォルト用コンストラクタ
	public Attributions() {
		rules.add("A is blue");
        //rules.add("A is box");
		rules.add("A is ball");
        rules.add("B is green");
        //rules.add("B is pyramid");
        rules.add("B is trapezoid");
        rules.add("C is red");
		//rules.add("C is ball");
        rules.add("C is box");
		for(String rule: rules) {
			addAttribution(rule);
		}
		addProhibitRules();	//属性禁止制約
		prohibitBlockStates = editStatementList(prohibitRules); //通常禁止制約

		//いらないもの消去
		System.out.println("prohibitBlockStates" + prohibitBlockStates);
		ArrayList<Integer> deleteNumber = new ArrayList<Integer>();
		int size = prohibitBlockStates.size();
		for(int i = 0; i < prohibitBlockStates.size(); i++) {
			if(prohibitBlockStates.get(i).contains("pyramid")
			|| prohibitBlockStates.get(i).contains("trapezoid")
			|| prohibitBlockStates.get(i).contains("ball")) {
				deleteNumber.add(i);
			}
		}
		for(int i = 0; i < deleteNumber.size(); i++) {
			//System.out.println(deleteNumber.get(deleteNumber.size()-i-1));
			prohibitBlockStates.remove((int)deleteNumber.get(deleteNumber.size()-i-1));
		}
		System.out.println("prohibitBlockStates" + prohibitBlockStates);

		//禁止制約をHashMapに格納
		keyValueProhibit(prohibitBlockStates);
	}

	// 自然言語用コンストラクタ
	public Attributions(List<String> rules) {
		this.rules = rules;
		for(String rule: rules) {
			addAttribution(rule);
		}
		addProhibitRules();
		prohibitBlockStates = editStatementList(prohibitRules);
	}

	// ルール分割済み用コンストラクタ
	public Attributions(HashMap<String, String> attributions) {
		this.attributions = attributions;
		addProhibitRules();
		prohibitBlockStates = editStatementList(prohibitRules);
	}

	//禁止制約をHashMapのKeyとValueに格納
	public void keyValueProhibit(ArrayList<String> list){
		StringTokenizer st;
		String tokenBuffer[];
		ArrayList<String> buffer;
		ArrayList<String> xValueList = new ArrayList<String>();
		ArrayList<String> yValueList = new ArrayList<String>();
		ArrayList<String> zValueList = new ArrayList<String>();
		for(int i = 0; i < list.size(); i ++) {
			//リストの1要素をトークンに分解して
			st = new StringTokenizer(list.get(i));
			//tokenBufferに格納
			int length = st.countTokens();
			tokenBuffer = new String[length];
			for (int j = 0; j < length; j++) {
				tokenBuffer[j] = st.nextToken();
				System.out.println("tokenBuffer["+j+"] = " + tokenBuffer[j]);
			}

			//HashMapのKeyとValueを格納していく
			if(tokenBuffer[0].equals("A")) {
				xValueList.add(tokenBuffer[2]);
				System.out.println("Aに追加します");
			}
			else if(tokenBuffer[0].equals("B")) {
				yValueList.add(tokenBuffer[2]);
				System.out.println("Bに追加します");
			}
			else if(tokenBuffer[0].equals("C")) {
				zValueList.add(tokenBuffer[2]);
				System.out.println("Cに追加します");
			}

			System.out.println("xValueList = " + xValueList);
			System.out.println("yValueList = " + yValueList);
			System.out.println("zValueList = " + zValueList);
		}

		keyValueProhibit.put("A", xValueList);
		keyValueProhibit.put("B", yValueList);
		keyValueProhibit.put("C", zValueList);

		System.out.println("keyValueProhibit = " + keyValueProhibit);
	}

	// 禁止制約追加メソッド
	private void addProhibitRules() {
		System.out.println("###### Add prohibitRule ######");
		prohibitRules.add("ball on ball");
		prohibitRules.add("trapezoid on ball");
		prohibitRules.add("trapezoid on trapezoid");
		prohibitRules.add("box on ball");
		prohibitRules.add("box on box");
		prohibitRules.add("pyramid on ball");

		prohibitRules.add("ball on pyramid");
		prohibitRules.add("box on pyramid");
		prohibitRules.add("trapezoid on pyramid");
		prohibitRules.add("pyramid on pyramid");
		for(String prohibitRule: prohibitRules) {
			System.out.println("****** ProhibitRule:"+ prohibitRule+" ******");
		}
	}

	// ブロック状態確認メソッド
	ArrayList<String> checkStates(ArrayList<String> states) {
		ArrayList<String> checkedStates = new ArrayList<String>();
		for(String state: states) {
			if(checkProhibitBlockState(state)) {
				checkedStates.add(state);
			}
		}
		return checkedStates;
	}

	// 禁止制約ブロック状態確認メソッド
	private Boolean checkProhibitBlockState(String state) {
		for(String prohibitBlockState: prohibitBlockStates) {
			if(prohibitBlockState.equals(state)) {
				System.out.println("【Warning!:状態"+state+"は禁止制約です！！】");
				return false;
			}
		}
		return true;
	}

	// 属性追加メソッド
	private void addAttribution(String attributionState) {
		List<String> stateList = Arrays.asList(attributionState.split(" "));
		if(stateList.get(1).equals("is")) {
			attributions.put(stateList.get(2), stateList.get(0));
		}
	}

	// 属性があるか否かの判定
	private Boolean existAttribute(String token) {
		return attributions.containsKey(token);
	}

	//属性から通常状態に変換してくれる
	ArrayList<String> editStatementList(ArrayList<String> statementList) {
		System.out.println("++++++ EditStatement ++++++");
		ArrayList<String> newStatementList = new ArrayList();
		for (String statement: statementList) {
			List<String> tokens = Arrays.asList(statement.split(" "));
			String newStatement = "";
			for(int tokenNum = 0; tokenNum < tokens.size(); tokenNum++) {
				String token = tokens.get(tokenNum);
				if(attributions.containsKey(token)) {
					token = attributions.get(token);
				}
				newStatement += token;
				if(tokenNum < tokens.size()-1) {
					newStatement += " ";
				}
			}
			newStatementList.add(newStatement);
			System.out.println(statement+" =====> "+newStatement);
		}
		return checkStates(newStatementList);
	}
}
