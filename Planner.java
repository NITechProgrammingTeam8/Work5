import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Planner {
 ArrayList operators;
 Random rand;
 ArrayList plan;

 public static void main(String argv[]){
  (new Planner()).start();
 }

 Planner(){
  rand = new Random();
 }

 public void start(){
  initOperators();
  ArrayList<String> goalList     = initGoalList();
  ArrayList<String> initialState = initInitialState();

  HashMap<String, String> theBinding = new HashMap<String, String>();
  plan = new ArrayList();
  planning(goalList,initialState,theBinding);	//プランニング実行
  //System.out.println("theBindingの中身=\n"+theBinding);

  System.out.println("***** This is a plan! *****");
   for(int i = 0 ; i < plan.size() ; i++){
    Operator op = (Operator)plan.get(i);
    System.out.println((op.instantiate(theBinding)).name);
   }
  }

 private boolean planning(ArrayList theGoalList,
                          ArrayList theCurrentState,
                          HashMap theBinding){
  System.out.println("*** GOALS ***" + theGoalList);

  //目標集合の目標が1つだけだった場合,
  if(theGoalList.size() == 1){
   System.out.println("目標は1つです");
   String aGoal = (String)theGoalList.get(0);
   if(planningAGoal(aGoal,theCurrentState,theBinding,0) != -1){
    return true;
   } else {
    return false;
   }
  }

//目標集合の目標が複数だった場合,
  else {
   System.out.println("目標は複数です");
   String aGoal = (String)theGoalList.get(0);
   int cPoint = 0;
   while(cPoint < operators.size()){
    System.out.println("cPoint:"+cPoint);
    // Store original binding
    HashMap orgBinding = new HashMap();
    for(Iterator e = theBinding.keySet().iterator() ; e.hasNext();){
     String key = (String)e.next();
     String value = (String)theBinding.get(key);
     orgBinding.put(key,value);
    }
    ArrayList orgState = new ArrayList();
    for(int i = 0; i < theCurrentState.size() ; i++){
     orgState.add(theCurrentState.get(i));
    }

    int tmpPoint = planningAGoal(aGoal,theCurrentState,theBinding,cPoint);
    //System.out.println("tmpPoint: "+tmpPoint);
    if(tmpPoint != -1){
     theGoalList.remove(0);		//removeget(0)でした
     System.out.println(theCurrentState);
     if(planning(theGoalList,theCurrentState,theBinding)){
      System.out.println("Success !");
      return true;
     } else {
      System.out.println("Mistaken !");
      cPoint = tmpPoint;
      //System.out.println("Fail::"+cPoint);
      theGoalList.add(0,aGoal);

      theBinding.clear();
      for(Iterator e=orgBinding.keySet().iterator();e.hasNext();){
       String key = (String)e.next();
       String value = (String)orgBinding.get(key);
       theBinding.put(key,value);
      }
      theCurrentState.clear();
      for(int i = 0 ; i < orgState.size() ; i++){
       theCurrentState.add(orgState.get(i));
      }
     }
    } else {
     theBinding.clear();
     for(Iterator e=orgBinding.keySet().iterator();e.hasNext();){
      String key = (String)e.next();
      String value = (String)orgBinding.get(key);
      theBinding.put(key,value);
     }
     theCurrentState.clear();
     for(int i = 0 ; i < orgState.size() ; i++){
      theCurrentState.add(orgState.get(i));
     }
     return false;
    }
   }
   return false;
  }
 }

 private int planningAGoal(String theGoal,ArrayList theCurrentState,
                           HashMap theBinding,int cPoint){
  System.out.println("**"+theGoal);

  //現在の状態で目標が満たされているか
  int size = theCurrentState.size();
  for(int i =  0; i < size ; i++){
   String aState = (String)theCurrentState.get(i);
   if((new Unifier()).unify(theGoal,aState,theBinding)){
    return 0;
   }
  }

  System.out.println("theCurrentState = \n" + theCurrentState);

  /**********************オペレータをランダムに選択********************************************/
  //int randInt = Math.abs(rand.nextInt()) % operators.size();
  //Operator op = (Operator)operators.get(randInt);

  int numOp = SelectOperatorNL();
  Operator op = (Operator)operators.get(numOp);
  System.out.println("オペレータ内容は = " + op.name);
  System.out.println("Thank you!");
  //operators.remove(randInt);	//ここも！
  //operators.add(op);
  cPoint = numOp;
  /**********************ここをいじるよ！***************************************************/

  for(int i = cPoint ; i < operators.size() ; i++){
   Operator anOperator = rename((Operator)operators.get(i));
   System.out.println("その後のオペレータ"+i+":\n"+anOperator);
   // 現在のCurrent state, Binding, planを保存
   HashMap orgBinding = new HashMap();
   for(Iterator e = theBinding.keySet().iterator() ; e.hasNext();){
    String key = (String)e.next();
    String value = (String)theBinding.get(key);
    orgBinding.put(key,value);
   }
   ArrayList orgState = new ArrayList();
   for(int j = 0; j < theCurrentState.size() ; j++){
    orgState.add(theCurrentState.get(j));
   }
   ArrayList orgPlan = new ArrayList();
   for(int j = 0; j < plan.size() ; j++){
    orgPlan.add(plan.get(j));
   }


   ArrayList addList = (ArrayList)anOperator.getAddList();
   for(int j = 0 ; j < addList.size() ; j++){
    if((new Unifier()).unify(theGoal,						//オペレータaddリストに
                             (String)addList.get(j),		//オペレータと一致するものが
                             theBinding)){					//あれば...
     System.out.println("unify成功");
     Operator newOperator = anOperator.instantiate(theBinding);
     ArrayList newGoals = (ArrayList)newOperator.getIfList();	//そのオペレータのIF部を副目標として加え,
     System.out.println(newOperator.name);
     if(planning(newGoals,theCurrentState,theBinding)){		//その副目標が達成されたら,
      System.out.println("副目標達成\n" + newOperator.name);
      plan.add(newOperator);								//そのオペレータを加え,
      theCurrentState =										//状態を変更
       newOperator.applyState(theCurrentState);
      return i+1;
     } else {
      // 失敗したら元に戻す．
      System.out.println("副目標失敗");
      theBinding.clear();
      for(Iterator e=orgBinding.keySet().iterator();e.hasNext();){
       String key = (String)e.next();
       String value = (String)orgBinding.get(key);
       theBinding.put(key,value);
      }
      theCurrentState.clear();
      for(int k = 0 ; k < orgState.size() ; k++){
       theCurrentState.add(orgState.get(k));
      }
      plan.clear();
      for(int k = 0 ; k < orgPlan.size() ; k++){
       plan.add(orgPlan.get(k));
      }
     }
    }
   }
  }
  return -1;
 }

 /*
  *	自然言語の命令によってオペレータの選択
  * return オペレータの番号
  */
 private int SelectOperatorNL() {
	 int opNumber = 0;
     Scanner scanner = new Scanner(System.in);
     System.out.println("数値を入力してください。");
     opNumber = scanner.nextInt();
	 return	opNumber;
 }

 int uniqueNum = 0;
 private Operator rename(Operator theOperator){
  Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
  uniqueNum = uniqueNum + 1;
  return newOperator;
 }

 private ArrayList<String> initGoalList(){
  ArrayList<String> goalList = new ArrayList<String>();
  goalList.add("B on C");
  goalList.add("A on B");
  return goalList;
 }

 private ArrayList<String> initInitialState(){
  ArrayList<String> initialState = new ArrayList<String>();
  initialState.add("clear A");
  initialState.add("clear B");
  initialState.add("clear C");

  initialState.add("ontable A");
  initialState.add("ontable B");
  initialState.add("ontable C");
  initialState.add("handEmpty");
  return initialState;
 }

 private void initOperators(){
  operators = new ArrayList<String>();

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
  Operator operator1 =
	    new Operator(name1,ifList1,addList1,deleteList1);
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
	Operator operator2 =
	    new Operator(name2,ifList2,addList2,deleteList2);
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
	Operator operator3 =
	    new Operator(name3,ifList3,addList3,deleteList3);
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
	Operator operator4 =
	    new Operator(name4,ifList4,addList4,deleteList4);
	operators.add(operator4);
    }
}

class Operator{
    String name;
    ArrayList ifList;
    ArrayList addList;
    ArrayList deleteList;

    Operator(String theName,
	     ArrayList theIfList,ArrayList theAddList,ArrayList theDeleteList){
	name       = theName;
	ifList     = theIfList;
	addList    = theAddList;
	deleteList = theDeleteList;
    }

    public ArrayList getAddList(){
	return addList;
    }

    public ArrayList getDeleteList(){
	return deleteList;
    }

    public ArrayList getIfList(){
	return ifList;
    }

    public String toString(){
	String result =
	    "NAME: "+name + "\n" +
	    "IF :"+ifList + "\n" +
	    "ADD:"+addList + "\n" +
	    "DELETE:"+deleteList;
	return result;
    }

    public ArrayList applyState(ArrayList theState){
	for(int i = 0 ; i < addList.size() ; i++){
	    theState.add(addList.get(i));
	}
	for(int i = 0 ; i < deleteList.size() ; i++){
	    theState.remove(deleteList.get(i));
	}
	return theState;
    }


    public Operator getRenamedOperator(int uniqueNum){
	ArrayList vars = new ArrayList();
	// IfListの変数を集める
	for(int i = 0 ; i < ifList.size() ; i++){
	    String anIf = (String)ifList.get(i);
	    vars = getVars(anIf,vars);
	}
	// addListの変数を集める
	for(int i = 0 ; i < addList.size() ; i++){
	    String anAdd = (String)addList.get(i);
	    vars = getVars(anAdd,vars);
	}
	// deleteListの変数を集める
	for(int i = 0 ; i < deleteList.size() ; i++){
	    String aDelete = (String)deleteList.get(i);
	    vars = getVars(aDelete,vars);
	}
	HashMap renamedVarsTable = makeRenamedVarsTable(vars,uniqueNum);

	// 新しいIfListを作る
	ArrayList newIfList = new ArrayList();
	for(int i = 0 ; i < ifList.size() ; i++){
	    String newAnIf =
		renameVars((String)ifList.get(i),
			   renamedVarsTable);
	    newIfList.add(newAnIf);
	}
	// 新しいaddListを作る
	ArrayList newAddList = new ArrayList();
	for(int i = 0 ; i < addList.size() ; i++){
	    String newAnAdd =
		renameVars((String)addList.get(i),
			   renamedVarsTable);
	    newAddList.add(newAnAdd);
	}
	// 新しいdeleteListを作る
	ArrayList newDeleteList = new ArrayList();
	for(int i = 0 ; i < deleteList.size() ; i++){
	    String newADelete =
		renameVars((String)deleteList.get(i),
			   renamedVarsTable);
	    newDeleteList.add(newADelete);
	}
	// 新しいnameを作る
	String newName = renameVars(name,renamedVarsTable);

	return new Operator(newName,newIfList,newAddList,newDeleteList);
    }

    private ArrayList getVars(String thePattern,ArrayList vars){
	StringTokenizer st = new StringTokenizer(thePattern);
	for(int i = 0 ; i < st.countTokens();){
	    String tmp = st.nextToken();
	    if(var(tmp)){
		vars.add(tmp);
	    }
	}
	return vars;
    }

    private HashMap makeRenamedVarsTable(ArrayList vars,int uniqueNum){
	HashMap result = new HashMap();
	for(int i = 0 ; i < vars.size() ; i++){
	    String newVar =
		(String)vars.get(i) + uniqueNum;
	    result.put((String)vars.get(i),newVar);
	}
	return result;
    }

    private String renameVars(String thePattern,
			      HashMap renamedVarsTable){
	String result = new String();
	StringTokenizer st = new StringTokenizer(thePattern);
	for(int i = 0 ; i < st.countTokens();){
	    String tmp = st.nextToken();
	    if(var(tmp)){
		result = result + " " +
		    (String)renamedVarsTable.get(tmp);
	    } else {
		result = result + " " + tmp;
	    }
	}
	return result.trim();
    }


    public Operator instantiate(HashMap theBinding){
	// name を具体化
	String newName =
	    instantiateString(name,theBinding);
	// ifList    を具体化
	ArrayList newIfList = new ArrayList();
	for(int i = 0 ; i < ifList.size() ; i++){
	    String newIf =
		instantiateString((String)ifList.get(i),theBinding);
	    newIfList.add(newIf);
	}
	// addList   を具体化
	ArrayList newAddList = new ArrayList();
	for(int i = 0 ; i < addList.size() ; i++){
	    String newAdd =
		instantiateString((String)addList.get(i),theBinding);
	    newAddList.add(newAdd);
	}
	// deleteListを具体化
	ArrayList newDeleteList = new ArrayList();
	for(int i = 0 ; i < deleteList.size() ; i++){
	    String newDelete =
		instantiateString((String)deleteList.get(i),theBinding);
	    newDeleteList.add(newDelete);
	}
	return new Operator(newName,newIfList,newAddList,newDeleteList);
    }

    private String instantiateString(String thePattern, HashMap theBinding){
        String result = new String();
        StringTokenizer st = new StringTokenizer(thePattern);
        for(int i = 0 ; i < st.countTokens();){
            String tmp = st.nextToken();
            if(var(tmp)){
		String newString = (String)theBinding.get(tmp);
		if(newString == null){
		    result = result + " " + tmp;
		} else {
		    result = result + " " + newString;
		}
            } else {
                result = result + " " + tmp;
            }
        }
        return result.trim();
    }

    private boolean var(String str1){
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

    Unifier(){
	//vars = new HashMap();
    }

    public boolean unify(String string1,String string2,HashMap<String, String> theBindings){
	HashMap<String, String> orgBindings = new HashMap<String, String>();
	for(Iterator e = theBindings.keySet().iterator() ; e.hasNext();){
	    String key = (String)e.next();
	    String value = (String)theBindings.get(key);
	    orgBindings.put(key,value);
	}
	this.vars = theBindings;
	if(unify(string1,string2)){
	    return true;
	} else {
	    // 失敗したら元に戻す．
	    theBindings.clear();
	    for(Iterator e = orgBindings.keySet().iterator() ; e.hasNext();){
		String key = (String)e.next();
		String value = (String)orgBindings.get(key);
		theBindings.put(key,value);
	    }
	    return false;
	}
    }

    public boolean unify(String string1,String string2){
	// 同じなら成功
	if(string1.equals(string2)) return true;

	// 各々トークンに分ける
	st1 = new StringTokenizer(string1);
	st2 = new StringTokenizer(string2);

	// 数が異なったら失敗
	if(st1.countTokens() != st2.countTokens()) return false;

	// 定数同士
	int length = st1.countTokens();
	buffer1 = new String[length];
	buffer2 = new String[length];
	for(int i = 0 ; i < length; i++){
	    buffer1[i] = st1.nextToken();
	    buffer2[i] = st2.nextToken();
	}

	// 初期値としてバインディングが与えられていたら
	if(this.vars.size() != 0){
	    for(Iterator keys = vars.keySet().iterator(); keys.hasNext();){
		String key = (String)keys.next();
		String value = (String)vars.get(key);
		replaceBuffer(key,value);
	    }
	}

	for(int i = 0 ; i < length ; i++){
	    if(!tokenMatching(buffer1[i],buffer2[i])){
		return false;
	    }
	}

	return true;
    }

    boolean tokenMatching(String token1,String token2){
	if(token1.equals(token2)) return true;
	if( var(token1) && !var(token2)) return varMatching(token1,token2);
	if(!var(token1) &&  var(token2)) return varMatching(token2,token1);
	if( var(token1) &&  var(token2)) return varMatching(token1,token2);
	return false;
    }

    boolean varMatching(String vartoken,String token){
	if(vars.containsKey(vartoken)){
	    if(token.equals(vars.get(vartoken))){
		return true;
	    } else {
		return false;
	    }
	} else {
	    replaceBuffer(vartoken,token);
	    if(vars.containsValue(vartoken)){
		replaceBindings(vartoken,token);
	    }
	    vars.put(vartoken,token);
	}
	return true;
    }

    void replaceBuffer(String preString,String postString){
	for(int i = 0 ; i < buffer1.length ; i++){
	    if(preString.equals(buffer1[i])){
		buffer1[i] = postString;
	    }
	    if(preString.equals(buffer2[i])){
		buffer2[i] = postString;
	    }
	}
    }

    void replaceBindings(String preString,String postString){
	Iterator keys;
	for(keys = vars.keySet().iterator(); keys.hasNext();){
	    String key = (String)keys.next();
	    if(preString.equals(vars.get(key))){
		vars.put(key,postString);
	    }
	}
    }

    boolean var(String str1){
	// 先頭が ? なら変数
	return str1.startsWith("?");
    }

}

