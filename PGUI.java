import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class PGUI extends JFrame implements ActionListener{

	Presenter presenter;
  JPanel cardPanel;
  CardLayout layout;
  ArrayList<JPanel> card; // パネル
  JRadioButton[] radio;
  JRadioButton[] radio2;
  JButton firstButton;
  JButton prevButton;
  JButton nextButton;
  JButton lastButton;
  JPanel page1;
  int row;
  int col;
  ImageIcon arm;
  ImageIcon icon1;
  ImageIcon icon2;
  ImageIcon icon3;
  ImageIcon[] icon;
  String armname;
  String icon1name;
  String icon2name;
  String icon3name;
  String[] iconName;
  String no;
  ArrayList<String> results;
  ArrayList<Operator> pUR;
  ArrayList<String> initialState; // テキストフィールド入力
  ArrayList<String> goalList;
  ArrayList<String> gIS; // 処理可能言語(自然言語ではない,セッタから取得)
  JTextArea iArea;
  JTextArea gArea;
  DefaultListModel modelName;
  DefaultListModel modelColor;
  DefaultListModel modelShape;
  JList namelist;
  JList colorlist;
  JList shapelist;
  JTextField newNameText;
  static String[][] images;
  static Map<String, Integer> imageMapC;
  static Map<String, Integer> imageMapS;
  static Map<String, Integer> Attribution;
  int cardPage;
  int armX;
  int armY;
  static String nodata;
  static ArrayList<String> prohibitRules;

  public static void main(String[] args){
	  // 画像の定義
	  images = new String[5][4];
	  images[0][0] = "image/squBt.png";
	  images[0][1] = "image/sanBt.png";
	  images[0][2] = "image/enBt.png";
	  images[0][3] = "image/daiBt.png";
	  images[1][0] = "image/squGt.png";
	  images[1][1] = "image/sanGt.png";
	  images[1][2] = "image/enGt.png";
	  images[1][3] = "image/daiGt.png";
	  images[2][0] = "image/squRt.png";
	  images[2][1] = "image/sanRt.png";
	  images[2][2] = "image/enRt.png";
	  images[2][3] = "image/daiRt.png";
	  images[3][0] = "image/squYt.png";
	  images[3][1] = "image/sanYt.png";
	  images[3][2] = "image/enYt.png";
	  images[3][3] = "image/daiYt.png";
	  images[4][0] = "image/squDeft.png";
	  images[4][1] = "image/sanDeft.png";
	  images[4][2] = "image/enDeft.png";
	  images[4][3] = "image/daiDeft.png";

	  // 対応番号の指定
	  imageMapC = new HashMap<>();
	  imageMapC.put("Blue", 0);
	  imageMapC.put("Green", 1);
	  imageMapC.put("Red", 2);
	  imageMapC.put("Yellow", 3);
	  imageMapC.put("Default", 4);

	  imageMapS = new HashMap<>();
	  imageMapS.put("box", 0);
	  imageMapS.put("pyramid", 1);
	  imageMapS.put("ball", 2);
	  imageMapS.put("trapezoid", 3);
	  imageMapS.put("default", 0);

	  // 属性指定での入出力に対応(defaultは予め弾く)
	  Attribution = new HashMap<>();
	  Attribution.put("Blue", 1);
	  Attribution.put("Green", 1);
	  Attribution.put("Red", 1);
	  Attribution.put("Yellow", 1);
	  Attribution.put("box", 2);
	  Attribution.put("pyramid", 2);
	  Attribution.put("ball", 2);
	  Attribution.put("trapezoid", 2);

    PGUI frame = new PGUI();

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(10, 10, 650, 450);
    frame.setTitle("プランニングシステム");
    frame.setVisible(true);
  }

  // その時使っていた属性データしか残らない(再実行時)？
  // 枠を超える出力のスクロールバー(結果描画の対処)
  // 禁止制約
  // 属性が被っているものを入出力で指定したら禁止メッセージ
  // 属性名指定のプランニング

  PGUI(){
	  // プレゼンターとの連結
	  presenter = new Presenter();
	  // 結果の格納(メソッド呼び出し)
	  results = new ArrayList<>();
	  ArrayList<String> result = presenter.getPlan();
	  results = new ArrayList<>(result);
	  // 初期状態の追加
	  results.add(0, "default position");
	  // 結果ステップデータの取得
	  pUR = new ArrayList<>();
	  pUR = presenter.getStepList();
	  // 入力デフォルト値の格納(メソッド呼び出し)
	  initialState = presenter.getInitialState();
	  // 出力デフォルト値の格納(メソッド呼び出し)
	  goalList = presenter.getGoalList();
	  // 属性デフォルト値
	  //ArrayList<String> initialAttribution = presenter.getAttributeInitialState();

	  // 初期状態の格納
	  modelName = new DefaultListModel();
	  modelColor = new DefaultListModel();
	  modelShape = new DefaultListModel();
	  String[] initialName = {"A", "B", "C"};
	  //String[] initialAColor = {"Blue", "Green", "Red"};
	  //String[] initialAShape = {"box", "pyramid", "ball"};
	  String[] initialAColor = {"Default", "Default", "Default"};
	  String[] initialAShape = {"default", "default", "default"};
	  for (int i = 0; i < initialName.length; i++) {
		  modelName.addElement(new String(initialName[i]));
		  modelColor.addElement(new String(initialAColor[i]));
		  modelShape.addElement(new String(initialAShape[i]));
	  }
	  // 初期状態表示用
	  gIS = presenter.getInitialState();

	  // 禁止制約の格納(メソッド呼び出し？)
	  prohibitRules = new ArrayList<>();
	  prohibitRules.add("box on pyramid");
	  prohibitRules.add("box on ball");
	  prohibitRules.add("ball on pyramid");
	  prohibitRules.add("pyramid on ball");
	  prohibitRules.add("trapezoid on pyramid");
	  prohibitRules.add("trapezoid on ball");

	  // 2ページ目以降のカード作成用メソッド
	  createResultPage(pUR);
	  // ボタンの作成メソッド
	  createButton();
	  // 最終処理メソッド
	  finishData();
  }

  public void actionPerformed(ActionEvent e){
	// ボタン選択アクション
    String cmd = e.getActionCommand();
    if (cmd.equals("First")){
      layout.first(cardPanel);
    }else if (cmd.equals("Last")){
      layout.last(cardPanel);
    }else if (cmd.equals("Next")){
      layout.next(cardPanel);
    }else if (cmd.equals("Prev")){
      layout.previous(cardPanel);
    } else if (cmd.equals("Planning")) {
    // 入力値のセット
    String iStr = iArea.getText();
    String gStr = gArea.getText();
    if (nodata.equals(iStr)) {
    	JPanel msg = new JPanel();
        msg.setLayout(new BoxLayout(msg, BoxLayout.PAGE_AXIS));
        JLabel msg1 = new JLabel("NO DATA");
	    msg1.setForeground(Color.RED);
        JLabel msg2 = new JLabel("Write SetInitial");
        msg.add(msg1);
        msg.add(msg2);
        JOptionPane.showMessageDialog(this, msg);
    } else if (nodata.equals(gStr)) {
    	JPanel msg = new JPanel();
        msg.setLayout(new BoxLayout(msg, BoxLayout.PAGE_AXIS));
        JLabel msg1 = new JLabel("NO DATA");
	    msg1.setForeground(Color.RED);
        JLabel msg2 = new JLabel("Write SetGoal");
        msg.add(msg1);
        msg.add(msg2);
        JOptionPane.showMessageDialog(this, msg);
    } else {
    iStr = iStr.replaceAll("\\\\n", "\r\n");
    iStr = iStr.replaceAll("Default", "Black");
    iStr = iStr.replaceAll("default", "box");
   	String[] iStrs = iStr.split("\\\n");
   	ArrayList<String> istrs  = new ArrayList<>(Arrays.asList(iStrs));
   	// テキストフィールドにもう一度表示させるため、入力言語のままでOK
   	initialState = new ArrayList<>( istrs );
	// 初期状態のセットと返却(今はこれだと使えないので、ごたくくんのプログラムが出来たら使う)
   	gIS = presenter.setInitialState(istrs);
   	// 【仮】上書き
   	gIS = new ArrayList<>( istrs );
   	gStr = gStr.replaceAll("Default", "Black");
    gStr = gStr.replaceAll("default", "box");
   	gStr = gStr.replaceAll("\\\\n", "\r\n");
   	String[] gStrs = gStr.split("\\\n");
   	ArrayList<String> gstrs  = new ArrayList<>(Arrays.asList(gStrs));
  	ArrayList<String> g = presenter.setGoalList(gstrs); // 使わないか(形としては使える)
  	// ゴールは状態は分からず、データの内容が反映できればOK
  	goalList = new ArrayList<>( gstrs );

  	// 属性情報のセット
  	ArrayList<String> update = new ArrayList<>();
  	for (int i = 0; i < modelName.size(); i++) {
  		String s = (String)modelName.get(i);
  		String s1 = (String)modelColor.get(i);
  		if (s1.equals("Default")) {
  			s1 = "Black";
  		}
  		String s2 = (String)modelShape.get(i);
  		if (s2.equals("default")) {
  			s2 = "box";
  		}
  		StringBuffer buf1 = new StringBuffer();
  		StringBuffer buf2 = new StringBuffer();
  		buf1.append(s);
  		buf1.append(" is ");
  		buf1.append(s1);
  		buf2.append(s);
  		buf2.append(" is ");
  		buf2.append(s2);
  		update.add(buf1.toString());
  		update.add(buf2.toString());
  	}
  	// hashmapが返ってきている(使用しない)
  	presenter.setAttribution(update);

    // 探索開始
    presenter.restart();
    pUR = presenter.getStepList();
    //pUR = null; // 【仮】実験用
    // pURがnullのときは、プランは空リスト？
    ArrayList<String> result = presenter.getPlan();
    results = new ArrayList<>(result);
    results.add(0, "default position");
    page1.removeAll();
    createResultPage(pUR);
    createButton();
    finishData();

    if (pUR == null) {
    	// 探索が禁止制約で出来なかった時
    	JPanel msg = new JPanel();
	    msg.setLayout(new BoxLayout(msg, BoxLayout.PAGE_AXIS));
	    JLabel msg1 = new JLabel("WARNING!!");
	    msg1.setForeground(Color.RED);
	    JLabel msg2 = new JLabel("Goal is not allowed");
	    msg.add(msg1);
	    msg.add(msg2);
	    JOptionPane.showMessageDialog(this, msg);
	    // 禁止制約のページに移動
	    // default position が必要がどうか(出力による)
	    layout.show(cardPanel, "label1");
    } else {
    // 探索終了のメッセージ表示
    JPanel msg = new JPanel();
    msg.setLayout(new BoxLayout(msg, BoxLayout.PAGE_AXIS));
    JLabel msg1 = new JLabel("Search completed");
    JLabel msg2 = new JLabel("Go on to the next page!");
    msg.add(msg1);
    msg.add(msg2);
    JOptionPane.showMessageDialog(this, msg);
    // 結果ページへの遷移
    layout.show(cardPanel, "label0");
    }
    }

    } else if (cmd.equals("addButton")) {
    	// 同じ名前の追加は認めない
    	if (!(nodata.equals(newNameText.getText()))) {
    		if (modelName.contains(newNameText.getText())) {
    			// 同じ名前を含んでいるとき
    			JPanel msg = new JPanel();
        	    msg.setLayout(new BoxLayout(msg, BoxLayout.PAGE_AXIS));
        	    JLabel msg1 = new JLabel("SAME DATA");
        	    msg1.setForeground(Color.RED);
        	    JLabel msg2 = new JLabel("Write other Name");
        	    msg.add(msg1);
        	    msg.add(msg2);
        	    JOptionPane.showMessageDialog(this, msg);
    		} else {
    			String color = "Default";
    			String shape = "default";
    			for (int i = 0 ; i < radio.length; i++){
    				if (radio[i].isSelected()){
    					color = radio[i].getText();
    				}
    			}
    			for (int i = 0 ; i < radio2.length; i++){
    				if (radio2[i].isSelected()){
    					shape = radio2[i].getText();
    				}
    			}
    			colorlist.setEnabled(true);
    			shapelist.setEnabled(true);
    			modelName.addElement(newNameText.getText());
    			modelColor.addElement(color);
    			modelShape.addElement(shape);
    			namelist.ensureIndexIsVisible(modelName.getSize() - 1);
    			colorlist.ensureIndexIsVisible(modelColor.getSize() - 1);
    			shapelist.ensureIndexIsVisible(modelShape.getSize() - 1);
    			colorlist.setEnabled(false);
    			shapelist.setEnabled(false);
    			// テキストフィールドの初期化
    			newNameText.setText(nodata);
    		}
    	} else {
    		JPanel msg = new JPanel();
    	    msg.setLayout(new BoxLayout(msg, BoxLayout.PAGE_AXIS));
    	    JLabel msg1 = new JLabel("NO DATA");
    	    msg1.setForeground(Color.RED);
    	    JLabel msg2 = new JLabel("Write new Name");
    	    msg.add(msg1);
    	    msg.add(msg2);
    	    JOptionPane.showMessageDialog(this, msg);
    	}
    } else if (cmd.equals("deleteButton")) {
    	if (namelist.isSelectionEmpty()) {
    		JPanel msg = new JPanel();
    	    msg.setLayout(new BoxLayout(msg, BoxLayout.PAGE_AXIS));
    	    JLabel msg1 = new JLabel("NO DATA");
    	    msg1.setForeground(Color.RED);
    	    JLabel msg2 = new JLabel("Serect delete Data");
    	    msg.add(msg1);
    	    msg.add(msg2);
    	    JOptionPane.showMessageDialog(this, msg);
    	} else if (!namelist.isSelectionEmpty()){
            int index = namelist.getSelectedIndex();
            modelName.remove(index);
            colorlist.setEnabled(true);
        	shapelist.setEnabled(true);
        	modelColor.remove(index);
        	modelShape.remove(index);
        	colorlist.setEnabled(false);
        	shapelist.setEnabled(false);
    	}
    } else if (cmd.equals("setButton")) {
    	if (namelist.isSelectionEmpty()) {
    		JPanel msg = new JPanel();
    	    msg.setLayout(new BoxLayout(msg, BoxLayout.PAGE_AXIS));
    	    JLabel msg1 = new JLabel("NO DATA");
    	    msg1.setForeground(Color.RED);
    	    JLabel msg2 = new JLabel("Serect edit Data");
    	    msg.add(msg1);
    	    msg.add(msg2);
    	    JOptionPane.showMessageDialog(this, msg);
    	} else if (nodata.equals(newNameText.getText())) {
    		if (!namelist.isSelectionEmpty()){
        		String color = "Default";
            	String shape = "default";
            	for (int i = 0 ; i < radio.length; i++){
            		if (radio[i].isSelected()){
            			color = radio[i].getText();
            		}
            	}
            	for (int i = 0 ; i < radio2.length; i++){
            		if (radio2[i].isSelected()){
            			shape = radio2[i].getText();
            		}
            	}
                int index = namelist.getSelectedIndex();
                // 名前は変えない
                colorlist.setEnabled(true);
            	shapelist.setEnabled(true);
            	modelColor.set(index, color);
            	modelShape.set(index, shape);
            	colorlist.setEnabled(false);
            	shapelist.setEnabled(false);
        	}
    	} else {
    		if (!namelist.isSelectionEmpty()){
    			String color = "Default";
    			String shape = "default";
    			for (int i = 0 ; i < radio.length; i++){
    				if (radio[i].isSelected()){
    					color = radio[i].getText();
    				}
    			}
    			for (int i = 0 ; i < radio2.length; i++){
    				if (radio2[i].isSelected()){
    					shape = radio2[i].getText();
    				}
    			}
    			int index = namelist.getSelectedIndex();
    			modelName.set(index, newNameText.getText());
    			colorlist.setEnabled(true);
    			shapelist.setEnabled(true);
    			modelColor.set(index, color);
    			modelShape.set(index, shape);
    			colorlist.setEnabled(false);
    			shapelist.setEnabled(false);
    			// テキストフィールドの初期化
    	    	newNameText.setText(nodata);
    		}
    	}
    }
  }

  public void createResultPage(ArrayList<Operator> pUR) {
	// 切り替え用ページの設定
	  card = new ArrayList<>();
	  // 初期状態選択画面
	  card.add(new JPanel()); // ページの追加
	  // 選択用ラジオボタンパネル
	  JPanel allRadio = new JPanel();
	  allRadio.setLayout(new BoxLayout(allRadio, BoxLayout.LINE_AXIS));
	  // 色選択
	  JPanel p2 = new JPanel();
	  p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));
	  radio = new JRadioButton[4];
	  radio[0] = new JRadioButton("Blue");
	  radio[1] = new JRadioButton("Green");
	  radio[2] = new JRadioButton("Red");
	  radio[3] = new JRadioButton("Yellow");
	  // ボタンのグループ化
	  ButtonGroup group = new ButtonGroup();
	  group.add(radio[0]);
	  group.add(radio[1]);
	  group.add(radio[2]);
	  group.add(radio[3]);
	  p2.add(new JLabel("Select Color"));
	  p2.add(radio[0]);
	  p2.add(radio[1]);
	  p2.add(radio[2]);
	  p2.add(radio[3]);
	  // 形状選択
	  JPanel p3 = new JPanel();
	  p3.setLayout(new BoxLayout(p3, BoxLayout.PAGE_AXIS));
	  radio2 = new JRadioButton[4];
	  radio2[0] = new JRadioButton("box");
	  radio2[1] = new JRadioButton("pyramid");
	  radio2[2] = new JRadioButton("ball");
	  radio2[3] = new JRadioButton("trapezoid");
	  // ボタンのグループ化
	  ButtonGroup group2 = new ButtonGroup();
	  group2.add(radio2[0]);
	  group2.add(radio2[1]);
	  group2.add(radio2[2]);
	  group2.add(radio2[3]);
	  p3.add(new JLabel("Select Shape"));
	  p3.add(radio2[0]);
	  p3.add(radio2[1]);
	  p3.add(radio2[2]);
	  p3.add(radio2[3]);

	  // 属性編集用ボタンの作成
	  JPanel ADS = new JPanel();
	  ADS.setLayout(new BoxLayout(ADS, BoxLayout.PAGE_AXIS));
	  ADS.add(new JLabel("new Name "));
	  nodata = null;
	  newNameText = new JTextField(5);
	  // 空の入力を取得
	  nodata = newNameText.getText();
	  ADS.add(newNameText);
	  ADS.add(Box.createRigidArea(new Dimension(5,5)));
	  JButton add = new JButton("追加");
	  add.addActionListener(this);
	  add.setActionCommand("addButton");
	  JButton delete = new JButton("削除");
	  delete.addActionListener(this);
	  delete.setActionCommand("deleteButton");
	  JButton set = new JButton("編集");
	  set.addActionListener(this);
	  set.setActionCommand("setButton");
	  ADS.add(add);
	  ADS.add(Box.createRigidArea(new Dimension(5,5)));
	  ADS.add(delete);
	  ADS.add(Box.createRigidArea(new Dimension(5,5)));
	  ADS.add(set);
	  allRadio.add(ADS);
	  allRadio.add(Box.createRigidArea(new Dimension(20,20)));
	  allRadio.add(p2);
	  allRadio.add(Box.createRigidArea(new Dimension(20,20)));
	  allRadio.add(p3);
	  allRadio.add(Box.createRigidArea(new Dimension(20,20)));

	  // 属性入力用パネルの作成
	  JPanel attribution = new JPanel();
	  attribution.setLayout(new BoxLayout(attribution, BoxLayout.PAGE_AXIS));
	  attribution.setPreferredSize(new Dimension(300, 360));
	  JPanel p4 = new JPanel();
	  p4.setLayout(new BoxLayout(p4, BoxLayout.PAGE_AXIS));
	  // 属性の決定用パネル
	  JPanel nameD = new JPanel();
	  nameD.add(new JLabel("Determine Attribution "));
	  p4.add(nameD);
	  JPanel attribute = new JPanel();
	  attribute.setLayout(new BoxLayout(attribute, BoxLayout.LINE_AXIS));
	  JPanel NAME = new JPanel();
	  NAME.setLayout(new BoxLayout(NAME, BoxLayout.PAGE_AXIS));
	  NAME.add(new JLabel("Name"));
	  // リストで実現(行ごとに選択できる)
	  namelist = new JList(modelName);
	  JScrollPane namesp = new JScrollPane();
	  namesp.getViewport().setView(namelist);
	  namesp.setPreferredSize(new Dimension(30, 150));
	  NAME.add(namesp);
	  attribute.add(NAME);
	  attribute.add(Box.createRigidArea(new Dimension(5,5)));
	  attribute.add(new JLabel("is"));
	  attribute.add(Box.createRigidArea(new Dimension(5,5)));
	  // 色選択リストパネル
	  colorlist = new JList(modelColor);
	  JScrollPane colorsp = new JScrollPane();
	  colorsp.getViewport().setView(colorlist);
	  // リストを選択不可にする
	  colorlist.setEnabled(false);
	  colorsp.setPreferredSize(new Dimension(30, 150));
	  BevelBorder borderC = new BevelBorder(BevelBorder.LOWERED);
	  colorsp.setBorder(borderC);
	  JPanel COLOR = new JPanel();
	  COLOR.setLayout(new BoxLayout(COLOR, BoxLayout.PAGE_AXIS));
	  COLOR.add(new JLabel("Color "));
	  COLOR.add(colorsp);
	  attribute.add(COLOR);
	  attribute.add(Box.createRigidArea(new Dimension(5,5)));
	  attribute.add(new JLabel("&"));
	  attribute.add(Box.createRigidArea(new Dimension(5,5)));
	  shapelist = new JList(modelShape);
	  JScrollPane shapesp = new JScrollPane();
	  shapesp.getViewport().setView(shapelist);
	  // リストを選択不可にする
	  shapelist.setEnabled(false);
	  shapesp.setPreferredSize(new Dimension(30, 150));
	  BevelBorder borderS = new BevelBorder(BevelBorder.LOWERED);
	  shapesp.setBorder(borderS);
	  JPanel SHAPE = new JPanel();
	  SHAPE.setLayout(new BoxLayout(SHAPE, BoxLayout.PAGE_AXIS));
	  SHAPE.add(new JLabel("Shape "));
	  SHAPE.add(shapesp);
	  attribute.add(SHAPE);
	  p4.add(attribute);
	  attribution.add(p4);
	  attribution.add(Box.createRigidArea(new Dimension(10,10)));
	  attribution.add(allRadio);
	  // 入力確認用ボタン
	  JPanel buttonpanel = new JPanel();
	  JButton button = new JButton("Planning");
	  button.addActionListener(this);
	  buttonpanel.add(button);
	  // 手動入力用パネル(スクロールにした)
	  JPanel natural = new JPanel();
	  natural.setLayout(new BoxLayout(natural, BoxLayout.PAGE_AXIS));
	  // 入力(setInitialState)
	  JPanel sI = new JPanel();
	  sI.setLayout(new BoxLayout(sI, BoxLayout.PAGE_AXIS));
	  JLabel setInitial = new JLabel("SetInitial");
	  iArea = new JTextArea(9, 20);
	  JScrollPane iScroll = new JScrollPane(iArea);
	  String ii = "";
	  for(String i : initialState) {
		  ii += i + "\n";
	  }
	  iArea.setText(ii);
	  sI.add(setInitial);
	  sI.add(iScroll);
	  // 入力(setGoal)
	  JPanel sG = new JPanel();
	  sG.setLayout(new BoxLayout(sG, BoxLayout.PAGE_AXIS));
	  JLabel setGoal = new JLabel("SetGoal");
	  gArea = new JTextArea(4, 20);
	  JScrollPane gScroll = new JScrollPane(gArea);
	  String gg = "";
	  for(String g : goalList) {
		  gg += g + "\n";
	  }
	  gArea.setText(gg);
	  sG.add(setGoal);
	  sG.add(gScroll);
	  natural.add(sI);
	  natural.add(Box.createRigidArea(new Dimension(10,5)));
	  natural.add(sG);
	  page1 = new JPanel();
	  page1.setPreferredSize(new Dimension(550, 360));
	  page1.setLayout(new BorderLayout(20, 5));
	  page1.add(attribution, BorderLayout.LINE_START);
	  page1.add(natural, BorderLayout.CENTER);
	  page1.add(buttonpanel, BorderLayout.PAGE_END);
	  card.get(0).add(page1);

	  // 初期状態の取得
	  ArrayList<String> blocks = new ArrayList<>();
	  ArrayList<String> dataTable = new ArrayList<>();
	  ArrayList<String> dataOn = new ArrayList<>();
	  for (String gis : gIS) {
		  if (gis.contains("clear")) {
			  String[] state = gis.split(" ", 0);
			  if (Attribution.get(state[1]) == null) {
				  // 指定が属性ではないとき
				  if (blocks.indexOf(state[1]) < 0) {
					  // 要素が入っていなければ入れる
					  blocks.add(state[1]);
				  }
			  } else if (Attribution.get(state[1]) == 1) {
				  // 指定が色属性のとき
				  for (int i = 0; i < modelName.size(); i++) {
					  if (((String)modelColor.get(i)).equals(state[1])) {
						  // 色名と同じ入力の時
						  if (blocks.indexOf((String)modelName.get(i)) < 0) {
							  // 要素が入っていなければ入れる
							  blocks.add((String)modelName.get(i));
						  }
					  }
				  }
			  } else if (Attribution.get(state[1]) == 2) {
				  // 指定が色属性のとき
				  for (int i = 0; i < modelName.size(); i++) {
					  if (((String)modelShape.get(i)).equals(state[1])) {
						  // 形状名と同じ入力の時
						  if (blocks.indexOf((String)modelName.get(i)) < 0) {
							  // 要素が入っていなければ入れる
							  blocks.add((String)modelName.get(i));
						  }
					  }
				  }
			  }
		  } else if (gis.contains("ontable")) {
			  // onは引っかからない
			  String[] state = gis.split(" ", 0);
			  if (Attribution.get(state[1]) == null) {
				  // 指定が属性ではないとき
				  if (blocks.indexOf(state[1]) < 0) {
					  // 要素が入っていなければ入れる
					  blocks.add(state[1]);
				  }
			  } else if (Attribution.get(state[1]) == 1) {
				  // 指定が色属性のとき
				  for (int i = 0; i < modelName.size(); i++) {
					  if (((String)modelColor.get(i)).equals(state[1])) {
						  // 色名と同じ入力の時
						  if (blocks.indexOf((String)modelName.get(i)) < 0) {
							  // 要素が入っていなければ入れる
							  blocks.add((String)modelName.get(i));
						  }
					  }
				  }
			  } else if (Attribution.get(state[1]) == 2) {
				  // 指定が色属性のとき
				  for (int i = 0; i < modelName.size(); i++) {
					  if (((String)modelShape.get(i)).equals(state[1])) {
						  // 形状名と同じ入力の時
						  if (blocks.indexOf((String)modelName.get(i)) < 0) {
							  // 要素が入っていなければ入れる
							  blocks.add((String)modelName.get(i));
						  }
					  }
				  }
			  }
			  dataTable.add(gis);
		  } else if (gis.contains("on")) {
			  String[] state = gis.split(" ", 0);
			  if (Attribution.get(state[0]) == null) {
				  // 指定が属性ではないとき
				  if (blocks.indexOf(state[0]) < 0) {
					  // 要素が入っていなければ入れる
					  blocks.add(state[0]);
				  }
			  } else if (Attribution.get(state[0]) == 1) {
				  // 指定が色属性のとき
				  for (int i = 0; i < modelName.size(); i++) {
					  if (((String)modelColor.get(i)).equals(state[0])) {
						  // 色名と同じ入力の時
						  if (blocks.indexOf((String)modelName.get(i)) < 0) {
							  // 要素が入っていなければ入れる
							  blocks.add((String)modelName.get(i));
						  }
					  }
				  }
			  } else if (Attribution.get(state[0]) == 2) {
				  // 指定が色属性のとき
				  for (int i = 0; i < modelName.size(); i++) {
					  if (((String)modelShape.get(i)).equals(state[0])) {
						  // 形状名と同じ入力の時
						  if (blocks.indexOf((String)modelName.get(i)) < 0) {
							  // 要素が入っていなければ入れる
							  blocks.add((String)modelName.get(i));
						  }
					  }
				  }
			  }
			  if (Attribution.get(state[2]) == null) {
				  // 指定が属性ではないとき
				  if (blocks.indexOf(state[2]) < 0) {
					  // 要素が入っていなければ入れる
					  blocks.add(state[2]);
				  }
			  } else if (Attribution.get(state[2]) == 1) {
				  // 指定が色属性のとき
				  for (int i = 0; i < modelName.size(); i++) {
					  if (((String)modelColor.get(i)).equals(state[2])) {
						  // 色名と同じ入力の時
						  if (blocks.indexOf((String)modelName.get(i)) < 0) {
							  // 要素が入っていなければ入れる
							  blocks.add((String)modelName.get(i));
						  }
					  }
				  }
			  } else if (Attribution.get(state[2]) == 2) {
				  // 指定が色属性のとき
				  for (int i = 0; i < modelName.size(); i++) {
					  if (((String)modelShape.get(i)).equals(state[2])) {
						  // 形状名と同じ入力の時
						  if (blocks.indexOf((String)modelName.get(i)) < 0) {
							  // 要素が入っていなければ入れる
							  blocks.add((String)modelName.get(i));
						  }
					  }
				  }
			  }
			  dataOn.add(gis);
		  }
	  }

	  // 変数値決定
	  row = blocks.size() + 2;
	  col = blocks.size();
	  arm = new ImageIcon("image/arm't.png");
	  no = "";
	  iconName = new String[col];
	  // ここで、ブロックの名前とどの画像かの一致をさせる(HashMap)
	  for (int i = 0; i < col; i++) {
		  iconName[i] = blocks.get(i);
	  }
	  armname = "   ";
	  icon = new ImageIcon[col];

	  // 属性名の初期化
	  for (int i = 0; i < blocks.size(); i++) {
		  boolean def = false;
		  for (int j = 0; j < modelName.size(); j++) {
			  if (blocks.get(i).equals((String)modelName.get(j))){
				  // 定義された記号を使用しているとき
				  String image = images[imageMapC.get((String)modelColor.get(j))][imageMapS.get((String)modelShape.get(j))];
				  icon[i] = new ImageIcon(image);
				  def = true;
			  }
		  }
		  if (def == false) {
			  // 定義されていない記号が使用されているとき
			  String image = images[imageMapC.get("Default")][imageMapS.get("default")];
			  icon[i] = new ImageIcon(image);
		  }
	  }
	  // 配置用座標配列を定義
	  int[] iconX = new int[blocks.size()];
	  int[] iconY = new int[blocks.size()];
	  for (int i = 0; i < blocks.size(); i++) {
		  iconX[i] = -1;
		  iconY[i] = -1;
	  }
	  // アーム用座標配列
	  armX = -1;
	  armY = -1;

	  // 2ページ目の設定
	  card.add(new JPanel());
	  JPanel page2 = new JPanel();
	  JLabel[][] p2Label = new JLabel[row][col];
	  page2.setPreferredSize(new Dimension(col*110, row*60));
	  page2.setBackground(Color.WHITE);
	  GridLayout page2layout = new GridLayout();
	  page2layout.setRows(row); // 行数
	  page2layout.setColumns(col); // 列数
	  page2.setLayout(page2layout);
	  // テーブルの上に乗っているブロックの初期化
	  int next = 0;
	  for (String dataS : dataTable) {
		  String[] state = dataS.split(" ", 0);
		  for (int i = 0; i < blocks.size(); i++) {
			  // 初めに入手したブロック名が何番目のものかcheck
			  if (state[1].equals(blocks.get(i))) {
				  // 名称一致のとき
				  iconX[i] = row - 1;
				  iconY[i] = next;
				  next++;
			  }
		  }
	  }
	  // 他ブロックの上に乗っているブロックの初期化
	  int ue = -1;
	  int sita = -1;
	  for (String dataS : dataOn) {
		  String[] state = dataS.split(" ", 0);
		  for (int i = 0; i < blocks.size(); i++) {
			  // それぞれの属性の番号を取得
			  if (state[0].equals(blocks.get(i))) {
				  ue = i;
			  } else if (state[2].equals(blocks.get(i))) {
				  sita = i;
			  }
		  }
		  // 上部分の座標の確定
		  iconX[ue] = iconX[sita] - 1;
		  iconY[ue] = iconY[sita];
	  }
	  // レイアウトの設定(上書きが一番良い？)
	  for (int i = 0; i < row; i++) {
		  for (int j = 0; j < col; j++) {
			  p2Label[i][j] = new JLabel(no);
		  }
	  }
	  // blockの上書き
	  for (int i = 0; i < blocks.size(); i++) {
		  p2Label[iconX[i]][iconY[i]] = new JLabel(icon[i]);
		  p2Label[iconX[i]][iconY[i]].setText(iconName[i]);
	  }
	  // アームの上書き
	  armX = 0;
	  armY = col - 1; // 妥協
	  p2Label[armX][armY] = new JLabel(arm);
	  p2Label[armX][armY].setText(armname);
	  // アイコンの挿入
	  for (int i = 0; i < row; i++) {
		  for (int j = 0; j < col; j++) {
			  page2.add(p2Label[i][j]);
		  }
	  }
	  JPanel list = new JPanel();
	  list.setLayout(new BoxLayout(list, BoxLayout.PAGE_AXIS));
	  for (int i = 0; i < results.size(); i++) {
		  JLabel now = new JLabel(results.get(i));
		  if (i == 0) {
			  now.setBackground(Color.ORANGE);
			  now.setOpaque(true);
		  }
		  list.add(now);
		  if (i == results.size()-1) {
			  list.add(new JLabel(" "));
			  list.add(new JLabel(" "));
		  }
	  }
	  JScrollPane splist = new JScrollPane(list);
	  splist.setPreferredSize(new Dimension(180, 300));
	  card.get(1).add(splist);
	  card.get(1).add(page2);

	  // 3ページ目以降
	  if (pUR == null) {
		  // 禁止制約
		  card.add(new JPanel());
		  JPanel prohibit = new JPanel();
		  prohibit.setLayout(new BoxLayout(prohibit, BoxLayout.PAGE_AXIS));
		  prohibit.setPreferredSize(new Dimension(500, 300));
		  prohibit.add(Box.createRigidArea(new Dimension(15,15)));
		  JPanel hosoku = new JPanel();
		  hosoku.setPreferredSize(new Dimension(300, 50));
		  hosoku.setLayout(new BoxLayout(hosoku, BoxLayout.PAGE_AXIS));
		  LineBorder inborder = new LineBorder(Color.red, 2);
		  TitledBorder border = new TitledBorder(inborder,
				  "Warning!!", TitledBorder.LEFT, TitledBorder.TOP);
		  // 具体的に禁止制約に引っかかったことを伝える
		  JLabel setumei = new JLabel("      This Goal is not allowed by ProhibitRules");
		  hosoku.add(setumei);
		  JLabel setumei2 = new JLabel("      Try to change Goal !");
		  hosoku.add(setumei2);
		  hosoku.setBorder(border);
		  JPanel kari = new JPanel();
		  kari.setPreferredSize(new Dimension(500, 55));
		  kari.setLayout(new BorderLayout());
		  kari.add(hosoku, BorderLayout.CENTER);
		  prohibit.add(kari);
		  prohibit.add(Box.createRigidArea(new Dimension(20,20)));
		  JPanel prohibit2_1 = new JPanel();
		  prohibit2_1.setLayout(new BoxLayout(prohibit2_1, BoxLayout.PAGE_AXIS));
		  for (int i = 0; i < modelName.size(); i++) {
			  StringBuilder buf_1 = new StringBuilder();
			  buf_1.append((String)modelName.get(i));
			  buf_1.append(" is ");
			  if (((String)modelShape.get(i)).equals("default")) {
				  buf_1.append("box");
			  } else {
				  buf_1.append((String)modelShape.get(i));
			  }
			  prohibit2_1.add( new JLabel( buf_1.toString()) );
		  }
		  JScrollPane scrollpane1 = new JScrollPane(prohibit2_1);
		  scrollpane1.setPreferredSize(new Dimension(160, 200));
		  BevelBorder border1 = new BevelBorder(BevelBorder.LOWERED);
		  scrollpane1.setBorder(border1);
		  JPanel AN = new JPanel();
		  AN.setLayout(new BoxLayout(AN, BoxLayout.PAGE_AXIS));
		  JPanel an = new JPanel();
		  an.setPreferredSize(new Dimension(160, 35));
		  an.setLayout(new BoxLayout(an, BoxLayout.PAGE_AXIS));
		  an.add( new JLabel("Attribution ") );
		  an.add( new JLabel("(Name & Shape)") );
		  AN.add(an);
		  AN.add(scrollpane1);
		  // 禁止制約
		  JPanel prohibit2 = new JPanel();
		  prohibit2.setLayout(new BoxLayout(prohibit2, BoxLayout.PAGE_AXIS));
		  for (int i = 0; i < prohibitRules.size(); i++) {
			  prohibit2.add( new JLabel(prohibitRules.get(i)) );
		  }
		  JScrollPane scrollpane2 = new JScrollPane(prohibit2);
		  scrollpane2.setPreferredSize(new Dimension(160, 200));
		  BevelBorder border2 = new BevelBorder(BevelBorder.LOWERED);
		  scrollpane2.setBorder(border2);
		  JPanel PR = new JPanel();
		  PR.setLayout(new BoxLayout(PR, BoxLayout.PAGE_AXIS));
		  JPanel pr = new JPanel();
		  pr.setPreferredSize(new Dimension(160, 35));
		  pr.setLayout(new BoxLayout(pr, BoxLayout.PAGE_AXIS));
		  pr.add( new JLabel("ProhibitRules") );
		  pr.add( new JLabel("(Shape)                ") );
		  PR.add(pr);
		  PR.add(scrollpane2);
		  // 目標状態
		  JPanel prohibit2_2 = new JPanel();
		  prohibit2_2.setLayout(new BoxLayout(prohibit2_2, BoxLayout.PAGE_AXIS));
		  prohibit2_2.setBackground(Color.WHITE);
		  for (int i = 0; i < goalList.size(); i++) {
			  prohibit2_2.add( new JLabel( goalList.get(i) ) );
		  }
		  JScrollPane scrollpane3 = new JScrollPane(prohibit2_2);
		  scrollpane3.setPreferredSize(new Dimension(160, 200));
		  BevelBorder border3 = new BevelBorder(BevelBorder.LOWERED);
		  scrollpane3.setBorder(border3);
		  JPanel GL = new JPanel();
		  GL.setLayout(new BoxLayout(GL, BoxLayout.PAGE_AXIS));
		  JPanel gl = new JPanel();
		  gl.setPreferredSize(new Dimension(160, 35));
		  gl.setLayout(new BoxLayout(gl, BoxLayout.PAGE_AXIS));
		  gl.add( new JLabel("GoalState ") );
		  gl.add( new JLabel("(Name or Shape)") );
		  GL.add(gl);
		  GL.add(scrollpane3);
		  // 目標状態・禁止制約・属性名とその形
		  JPanel details = new JPanel();
		  details.setLayout(new BoxLayout(details, BoxLayout.LINE_AXIS));
		  details.add(AN);
		  details.add(Box.createRigidArea(new Dimension(10,10)));
		  details.add(PR);
		  details.add(Box.createRigidArea(new Dimension(10,10)));
		  details.add(GL);
		  prohibit.add(details);
		  card.get(2).add(prohibit);
		  cardPage = 1;
	  } else if (pUR.size() == 0) {
		  cardPage = 1; // pUR.size()だと0
		  // 初期状態と目標状態が一致
		  card.add(new JPanel());
		  list = new JPanel();
		  list.setLayout(new BoxLayout(list, BoxLayout.PAGE_AXIS));
		  for (int i = 0; i < results.size(); i++) {
			  JLabel now = new JLabel(results.get(i));
			  if (i == 0) {
				  now.setBackground(Color.ORANGE);
				  now.setOpaque(true);
			  }
			  list.add(now);
			  if (i == results.size()-1) {
				  list.add(new JLabel(" "));
				  JLabel finish = new JLabel("already Goal !!");
				  LineBorder border = new LineBorder(Color.RED, 2, true);
				  finish.setBorder(border);
				  list.add(finish);
			  }
		  }
		  splist = new JScrollPane(list);
		  splist.setPreferredSize(new Dimension(180, 300));
		  card.get(2).add(splist);
		  // ページの再利用は出来ない
		  JPanel already = new JPanel();
		  JLabel[][] alreadyL = new JLabel[row][col];
		  already.setPreferredSize(new Dimension(col*110, row*60));
		  already.setBackground(Color.WHITE);
		  GridLayout alreadylayout = new GridLayout();
		  alreadylayout.setRows(row); // 行数
		  alreadylayout.setColumns(col); // 列数
		  already.setLayout(alreadylayout);
		  // テーブルの上に乗っているブロックの初期化
		  next = 0;
		  for (String dataS : dataTable) {
			  String[] state = dataS.split(" ", 0);
			  for (int i = 0; i < blocks.size(); i++) {
				  // 初めに入手したブロック名が何番目のものかcheck
				  if (state[1].equals(blocks.get(i))) {
					  // 名称一致のとき
					  iconX[i] = row - 1;
					  iconY[i] = next;
					  next++;
				  }
			  }
		  }
		  ue = -1;
		  sita = -1;
		  for (String dataS : dataOn) {
			  String[] state = dataS.split(" ", 0);
			  for (int i = 0; i < blocks.size(); i++) {
				  // それぞれの属性の番号を取得
				  if (state[0].equals(blocks.get(i))) {
					  ue = i;
				  } else if (state[2].equals(blocks.get(i))) {
					  sita = i;
				  }
			  }
			  // 上部分の座標の確定
			  iconX[ue] = iconX[sita] - 1;
			  iconY[ue] = iconY[sita];
		  }
		  // レイアウトの設定(上書きが一番良い？)
		  for (int i = 0; i < row; i++) {
			  for (int j = 0; j < col; j++) {
				  alreadyL[i][j] = new JLabel(no);
			  }
		  }
		  // blockの上書き
		  for (int i = 0; i < blocks.size(); i++) {
			  alreadyL[iconX[i]][iconY[i]] = new JLabel(icon[i]);
			  alreadyL[iconX[i]][iconY[i]].setText(iconName[i]);
		  }
		  // アームの上書き
		  armX = 0;
		  armY = col - 1; // 妥協
		  alreadyL[armX][armY] = new JLabel(arm);
		  alreadyL[armX][armY].setText(armname);
		  // アイコンの挿入
		  for (int i = 0; i < row; i++) {
			  for (int j = 0; j < col; j++) {
				  already.add(alreadyL[i][j]);
			  }
		  }
		  card.get(2).add(already);
	  } else {
		  // 経路導出時
		  cardPage = pUR.size();  // 初期状態を除いたSTEP数
		  // 経路出力
		  for (int i = 0; i < cardPage; i++) {
			  // 新しいページの挿入
			  card.add(new JPanel());
			  JPanel newpage = new JPanel();
			  JLabel[][] newLabel = new JLabel[row][col];
			  newpage.setPreferredSize(new Dimension(col*110, row*60));
			  newpage.setBackground(Color.WHITE);
			  GridLayout newpagelayout = new GridLayout();
			  newpagelayout.setRows(row); // 行数
			  newpagelayout.setColumns(col); // 列数
			  newpage.setLayout(newpagelayout);
			  String hatenaX = pUR.get(i).getBindings().get("?x");
			  String hatenaY = pUR.get(i).getBindings().get("?y");
			  int hXz = blocks.indexOf(hatenaX); // ブロックの要素番号
			  int hYz = blocks.indexOf(hatenaY); // ブロックの要素番号
			  if (pUR.get(i).getName().equals("Place ?x on ?y")) {
				  // xの操作
				  iconX[hXz] = iconX[hYz] - 1;
				  iconY[hXz] = iconY[hYz];
				  // アームの操作
				  armX = iconX[hXz] - 1;
				  armY = iconY[hYz];
			  } else if (pUR.get(i).getName().equals("remove ?x from on top ?y")) {
				  // xの操作
				  iconX[hXz] = iconX[hXz] - 1;
				  iconY[hXz] = iconY[hYz];
				  // アームの操作
				  armX = iconX[hXz] - 1;
				  armY = iconY[hYz];
			  } else if (pUR.get(i).getName().equals("pick up ?x from the table")) {
				  // xの操作(tableであることを意識するか否か)
				  iconX[hXz] = iconX[hXz] - 1;
				  iconY[hXz] = iconY[hXz];
				  // アームの操作
				  armX = iconX[hXz] - 1;
				  armY = iconY[hXz];
			  } else if (pUR.get(i).getName().equals("put ?x down on the table")) {
				  // xの操作,アームの操作
				  iconX[hXz] = row - 1;
				  armX = iconX[hXz] - 1;
				  boolean umu;
				  for (int j = 0; j < col; j++) {
					  umu = true;
					  for (int k = 0; k < iconY.length; k++) {
						  if (j == iconY[k]) {
							  umu = false;
							  break;
						  }
					  }
					  if (umu == true) {
						  iconY[hXz] = j;
						  armY = j;
						  break;
					  }
				  }
			  }
			  // レイアウトの設定(上書きが一番良い？)
			  for (int k = 0; k < row; k++) {
				  for (int j = 0; j < col; j++) {
					  newLabel[k][j] = new JLabel(no);
				  }
			  }
			  // blockの上書き
			  for (int k = 0; k < blocks.size(); k++) {
				  newLabel[iconX[k]][iconY[k]] = new JLabel(icon[k]);
				  newLabel[iconX[k]][iconY[k]].setText(iconName[k]);
			  }
			  // A on Aとかが許されるので、時々おかしくなってindex-1指定とかしてしまう
			  newLabel[armX][armY] = new JLabel(arm);
			  newLabel[armX][armY].setText(armname);
			  // アイコンの挿入
			  for (int k = 0; k < row; k++) {
				  for (int j = 0; j < col; j++) {
					  newpage.add(newLabel[k][j]);
				  }
			  }
			  // 出力結果(各ステップ)の表示
			  list = new JPanel();
			  list.setLayout(new BoxLayout(list, BoxLayout.PAGE_AXIS));
			  for (int k = 0; k < results.size(); k++) {
				  JLabel now = new JLabel(results.get(k));
				  if (k == i+1) {
					  now.setBackground(Color.ORANGE);
					  now.setOpaque(true);
				  }
				  list.add(now);
				  if (k == results.size()-1) {
					  list.add(new JLabel(" "));
					  if (i != cardPage-1) {
						  list.add(new JLabel(" "));
					  } else {
						  // 最後の行
						  JLabel finish = new JLabel("finish!!");
						  LineBorder border = new LineBorder(Color.RED, 2, true);
						  finish.setBorder(border);
						  list.add(finish);
					  }
				  }
			  }
			  splist = new JScrollPane(); // 追加
			  splist.setPreferredSize(new Dimension(180, 300));
			  JViewport view = splist.getViewport(); // 追加
			  view.setView(list); // 追加
			  view.setViewPosition(new Point(0, 15*i)); // スクロースバー位置
			  card.get(i+2).add(splist);
			  card.get(i+2).add(newpage);
		  }
	  }

	  // 最終ページのみ別で作成
	  card.add(new JPanel());
	  ArrayList<Operator> operators = presenter.getOperatorList();
	
	  JPanel tostring = new JPanel();
	  tostring.setLayout(new BoxLayout(tostring, BoxLayout.PAGE_AXIS));
	  int i = 1;
	  for (Operator operator : operators) {
		  tostring.add(new JLabel("●Operator" + i));
		  tostring.add(new JLabel("NAME: " + operator.getName()));
		  tostring.add(new JLabel("ADD: " + operator.getAddList()));
		  tostring.add(new JLabel("DELETE: " + operator.getDeleteList()));
		  if (i < operators.size()) {
			  tostring.add(new JLabel("  "));
		  }
		  i++;
	  }
	  JScrollPane scrollpane = new JScrollPane(tostring);
	  scrollpane.setPreferredSize(new Dimension(260, 310));
	  BevelBorder border = new BevelBorder(BevelBorder.LOWERED);
	  scrollpane.setBorder(border);
	  JPanel tostring2 = new JPanel();
	  tostring2.setLayout(new BoxLayout(tostring2, BoxLayout.PAGE_AXIS));
	  tostring2.setBackground(Color.WHITE);
	  ArrayList<String> printResult = presenter.getPlan();
	  tostring2.add(new JLabel("***** This is a plan! *****"));
	  for (String printR : printResult) {
		  tostring2.add(new JLabel(printR));
	  }
	  JScrollPane scrollpane2 = new JScrollPane(tostring2);
	  scrollpane2.setPreferredSize(new Dimension(200, 310));
	  BevelBorder border2 = new BevelBorder(BevelBorder.LOWERED);
	  scrollpane2.setBorder(border2);
	  card.get(card.size()-1).add(scrollpane);
	  card.get(card.size()-1).add(scrollpane2);

	  cardPanel = new JPanel();
	  layout = new CardLayout();
	  cardPanel.setLayout(layout);
	  // for文で回す必要あり
	  cardPanel.add(card.get(0), "start");
	  for (int j = 0; j <= cardPage; j++) {
		  StringBuffer buf = new StringBuffer();
		  buf.append("label");
		  buf.append(String.valueOf(j));
		  cardPanel.add(card.get(j+1), buf.toString());
	  }
	  cardPanel.add(card.get(card.size()-1), "finish");
  }

  public void createButton() {
	    // カード移動用ボタン
	    firstButton = new JButton("First");
	    firstButton.addActionListener(this);
	    firstButton.setActionCommand("First");
	    prevButton = new JButton("Prev");
	    prevButton.addActionListener(this);
	    prevButton.setActionCommand("Prev");
	    nextButton = new JButton("Next");
	    nextButton.addActionListener(this);
	    nextButton.setActionCommand("Next");
	    lastButton = new JButton("Last");
	    lastButton.addActionListener(this);
	    lastButton.setActionCommand("Last");
  }

  public void finishData() {
	  // カード遷移設定ボタンを一列の画面に
	  JPanel btnPanel = new JPanel();
	  btnPanel.add(firstButton);
	  btnPanel.add(prevButton);
	  btnPanel.add(nextButton);
	  btnPanel.add(lastButton);
	  getContentPane().add(btnPanel, BorderLayout.PAGE_START);
	  getContentPane().add(cardPanel, BorderLayout.CENTER);
  }
}