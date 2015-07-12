import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.*;

public class PokemonClient implements ActionListener {
	public BufferedReader in;
	public PrintWriter out;
	public Socket socket;
	public Pokemon myPokemon;
	public Pokemon rivalPokemon;
	public PokemonWaza myWaza;
	public PokemonWaza rivalWaza;
	public String ipAddress;
	public boolean check = false;
	public String[] text1 = new String[2];
	public String[] text2 = new String[2];
	public int[] pokemonHP = new int[2];
	public int myPokemonMaxHP;
	public int rivalPokemonMaxHP;
	
	public JFrame mainFrame;
	public Container contentPane;
	public JPanel cardPane;
	public CardLayout layout;
	public JPanel titlemoviePane;
	public JPanel titlePane;
	public JPanel selectPane;
	public JPanel battlePane;
	public JPanel resultPane;
	public JTextField IP;
	public ImageIcon myPokemonIcon;
	public ImageIcon rivalPokemonIcon;
	public JTextField myPokemonHP;
	public JTextField rivalPokemonHP;
	public JTextField text;
	public JButton textButton;
	public JButton[] button;
	
	/*追加部分*/
	public static final int windowx =  960, windowy = 600;
	public Timer titlemovie;
	public boolean time = true;
	public int state = 0;
	
	public PokemonClient() {
		// JFrameクラスのインスタンスを作成
		mainFrame = new JFrame("Pokemon");
		// 閉じるボタン押下時のアプリケーションの振る舞いを決定
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// ウィンドウの初期サイズ(幅,高さ)をピクセル単位で設定
		mainFrame.setSize(windowx, windowy);
		// ウィンドウの表示場所を規定
		mainFrame.setLocationRelativeTo(null);
		// JFrameよりContentPaneを取得
		contentPane = mainFrame.getContentPane();
		
		// 表示画面の切り替え用のパネルを生成
		cardPane = new JPanel();
		// カードレイアウトを生成
		layout = new CardLayout();
		// カードレイアウトをcardPaneに適用
		cardPane.setLayout(layout);
		
		// タイトルムービー画面titlemoviePaneの生成
		titlemoviePane = new JPanel();
		// タイトル画面titlePaneの生成
		titlePane = new JPanel();
		// 起動時の画面selectPaneの生成
		selectPane = new JPanel();
		// 戦闘時の画面battlePaneの生成
		battlePane = new JPanel();
		// 終了時の画面resultPaneの生成
		resultPane = new JPanel();
		
		// cardPaneにtitlemoviePaneを追加
		cardPane.add(titlemoviePane);
		// cardPaneにtitlePaneを追加
		cardPane.add(titlePane);
		// cardPaneにselectPaneを追加
		cardPane.add(selectPane);
		// cardPaneにbattlePaneを追加
		cardPane.add(battlePane);
		// cardPaneにresultPaneを追加
		cardPane.add(resultPane);
		
		// cardPaneをContentPaneに配置
		contentPane.add(cardPane);
	}
	
	/* 音楽再生 BGM */
	public void titlemovieSound(String filePath) {
		AudioFormat format = null;
		DataLine.Info info = null;
		Clip line = null;
		File audioFile = null;
		
		try{
			audioFile = new File(filePath);
			format = AudioSystem.getAudioFileFormat(audioFile).getFormat();
			info = new DataLine.Info(Clip.class, format);
			line = (Clip)AudioSystem.getLine(info);
			line.open(AudioSystem.getAudioInputStream(audioFile));
			line.start();
		} catch(Exception e) {
			errorMessage(e);
		}
	}
	
	/* 0.タイトルムービー画面 titlemoviePane */
	public void titlemoviePane() {
		titlemovie = new Timer(22800, this);
		titlemovie.start();
		
		JPanel pict1 = new pict();
		pict1.setOpaque(false);
		pict1.setPreferredSize(new Dimension(windowx,windowy));
		
		JButton TitleButton = new JButton("");
		TitleButton.setPreferredSize(new Dimension(windowx,windowy));
		//TitleButton.addActionListener(this); // ボタン押下時のイベントを追加
		//TitleButton.setActionCommand("Title"); // ボタンのアクションのコマンド
		TitleButton.setContentAreaFilled(false);
		TitleButton.setBorderPainted(false);
		pict1.add(TitleButton);
		
		titlemoviePane.add(pict1);
	}
	
	/* 1.タイトル画面 titlePane */
	public void titlePane() {
		titlemovie.stop(); // アニメーションの停止
		
		JPanel pict2 = new pict();
		pict2.setOpaque(false);
		pict2.setPreferredSize(new Dimension(windowx,windowy));
		// menuPaneにSpringLayoutを適用
		SpringLayout titleLayout = new SpringLayout();
		titlePane.setLayout(titleLayout);
		
		// menuButtonの作成
		JButton[] menuButton = new JButton[2];
		menuButton[0] = new JButton("部屋を作る");
		menuButton[1] = new JButton("部屋に入る");
		// ボタンのアクションのコマンド
		menuButton[0].setActionCommand("hostStart");
		menuButton[1].setActionCommand("guestStart");
		for(int i=0; i<2; i++) {
			menuButton[i].setPreferredSize(new Dimension(300, 60)); // menuButtonのサイズの設定
			menuButton[i].setFont(new Font("MS 明朝", Font.PLAIN, 24)); // ボタンの文字の設定
			menuButton[i].addActionListener(this); // ボタン押下時のイベントを追加
			// menuLayoutの制約を規定
			titleLayout.putConstraint(SpringLayout.NORTH, menuButton[i], 230, SpringLayout.NORTH, titlePane);
			titleLayout.putConstraint(SpringLayout.WEST, menuButton[i], 180+315*i, SpringLayout.WEST, titlePane);
			titlePane.add(menuButton[i]); //menuPaneにmenuButtonを追加
		}
		
		titlePane.add(pict2);
	}
	
	/* 2.ポケモン選択画面 selectPane */
	public void selectPane() {
		// 起動時の画面selectPaneにBorderLayoutを適用
		selectPane.setLayout(new BorderLayout(5, 25));
		
		// IPアドレス部分のパネルipPaneを生成
		JPanel ipPane = new JPanel();
		// ipPaneにFlowLayoutを適用
		ipPane.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
		// ipPaneのサイズの設定
		ipPane.setPreferredSize(new Dimension(800, 80));
		// ipLabelの生成
		JLabel ipLabel = new JLabel(" サーバーのIPアドレス");
		// ipLabelのサイズの設定
		ipLabel.setPreferredSize(new Dimension(250, 60));
		// 文字を中央揃えに設定
		ipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		// 文字フォント設定
		ipLabel.setFont(new Font("MS 明朝", Font.PLAIN, 24));
		// IPアドレス入力欄
		IP = new JTextField(20);
		// IPアドレス入力欄のサイズの設定
		IP.setPreferredSize(new Dimension(300, 60));
		// 文字フォント設定
		IP.setFont(new Font("Century", Font.PLAIN, 20));
		// ipPaneにipLabelとIPを配置
		ipPane.add(ipLabel);
		ipPane.add(IP);
		
		// ポケモン選択部分のパネルを生成
		JPanel pokemonPane = new JPanel();
		// pokemonPaneにBorderLayoutを適用
		pokemonPane.setLayout(new BorderLayout());
		// pokemonPaneのサイズの設定
		pokemonPane.setPreferredSize(new Dimension(900, 300));
		// pokemonPaneに枠線を設定
		pokemonPane.setBorder(new LineBorder(Color.GRAY, 2, true));
		// "ポケモンを選択してください"を表示
		JLabel selectLabel = new JLabel("ポケモンを選択してください");
		// selectLabelのサイズの設定
		selectLabel.setPreferredSize(new Dimension(600, 60));
		// 文字を中央揃えに設定
		selectLabel.setHorizontalAlignment(SwingConstants.CENTER);
		// 文字フォント設定
		selectLabel.setFont(new Font("MS 明朝", Font.PLAIN, 24));
		// pokemonPaneを貼り付けるselectPaneの生成
		JPanel pokemonSelectPane = new JPanel();
		pokemonSelectPane.setLayout(new GridLayout(0,4));
		// 各ポケモンの画像とボタンを貼り付けるpokemonPaneの生成
		JPanel[] pokemonPanel = new JPanel[4];
		// 各ポケモンの画像を貼り付けるpokemonLabelの生成
		JLabel[] pokemonLabel = new JLabel[4];
		// 各ポケモンの画像を取り扱うpokemonIconの生成
		ImageIcon[] pokemonIcon = new ImageIcon[4];
		pokemonIcon[0] = new ImageIcon(this.getClass().getResource("image/pikachu.jpg"));
		pokemonIcon[1] = new ImageIcon(this.getClass().getResource("image/fushigidane.jpg"));
		pokemonIcon[2] = new ImageIcon(this.getClass().getResource("image/hitokage.jpg"));
		pokemonIcon[3] = new ImageIcon(this.getClass().getResource("image/zenigame.jpg"));
		// 各ポケモンのボタンの生成
		JButton[] pokemonButton = new JButton[4];
		pokemonButton[0] = new JButton("ピカチュウ");
		pokemonButton[1] = new JButton("フシギダネ");
		pokemonButton[2] = new JButton("ヒトカゲ");
		pokemonButton[3] = new JButton("ゼニガメ");
		for(int i=0; i<4; i++) {
			pokemonPanel[i] = new JPanel(); // pokemonPaneの生成
			pokemonPanel[i].setLayout(new BorderLayout()); // pokemonPaneにBorderLayoutを適用
			pokemonLabel[i] = new JLabel(); // pokemonLabelの生成
			pokemonLabel[i].setIcon(pokemonIcon[i]); // pokemonLabelに画像を貼り付け
			pokemonButton[i].setPreferredSize(new Dimension(240, 50)); // pokemonButtonのサイズの設定
			pokemonButton[i].setFont(new Font("MS 明朝", Font.BOLD, 18)); // pokemonButtonのフォントの設定(Windows)
			pokemonButton[i].addActionListener(this); // ボタン押下時のイベントを追加
			pokemonButton[i].setActionCommand("pokemonButton" + i); // ボタンのアクションのコマンド
			pokemonPanel[i].add(pokemonLabel[i], BorderLayout.CENTER); // pokemonLabelをpokemonPaneの中央に配置
			pokemonPanel[i].add(pokemonButton[i], BorderLayout.SOUTH); // pokemonButtonをpokemonPaneの下側に配置
			pokemonSelectPane.add(pokemonPanel[i]); // selectPaneにpokemonPanelを配置
		}
		// selectLabelをpokemonPaneの上部に設置
		pokemonPane.add(selectLabel, BorderLayout.NORTH);
		// selectPaneをpokemonPaneの中央に設置
		pokemonPane.add(pokemonSelectPane, BorderLayout.CENTER);	
		
		// OKボタンを生成
		JButton OKbutton = new JButton("OK"); // OKボタンの生成
		OKbutton.setPreferredSize(new Dimension(800, 50)); // サイズの設定
		OKbutton.setFont(new Font("Century", Font.PLAIN, 20)); // フォントの設定(Windows)
		OKbutton.addActionListener(this); // ボタン押下時のイベントを追加
		OKbutton.setActionCommand("OKButton"); // ボタンのアクションのコマンド
		
		// 空のパネルを用意(レイアウト用)
		JPanel westPanel = new JPanel();
		JPanel eastPanel = new JPanel();
		// 空のパネルを左右に設置(レイアウト用)
		selectPane.add(westPanel, BorderLayout.WEST);
		selectPane.add(eastPanel, BorderLayout.EAST);
		// ipPaneをselectPaneに配置
		selectPane.add(ipPane, BorderLayout.NORTH);
		// pokemonPaneをselectPaneに配置
		selectPane.add(pokemonPane, BorderLayout.CENTER);
		// OKbuttonをselectPaneに配置
		selectPane.add(OKbutton, BorderLayout.SOUTH);
	}
	
	/* 3.戦闘画面 battlePane */
	public void battlePane() {
		// 音楽BGM流す
		titlemovieSound("music/music2.wav");
		// 戦闘時の画面battlePaneにBorderLayoutを適用
		battlePane.setLayout(new BorderLayout());
		
		// ラベルを貼り付けるパネルを生成
		JPanel labelPane = new JPanel();
		// labelPaneにSpringLayoutを適用
		SpringLayout pokemonLayout = new SpringLayout();
		labelPane.setLayout(pokemonLayout);
		// ラベルのインスタンスを生成
		JLabel myPokemonLabel = new JLabel();
		JLabel rivalPokemonLabel = new JLabel();
		// ラベルに画像を表示
		myPokemonLabel.setIcon(myPokemonIcon);
		rivalPokemonLabel.setIcon(rivalPokemonIcon);
		// ポケモンの名前を表示するmyPokemonNameとrivalPokemonNameの生成
		JLabel myPokemonName = new JLabel();
		JLabel rivalPokemonName = new JLabel();
		// ポケモンの名前の長さによって文字列を調節
		if(myPokemon.name.equals("フシギダネ") || myPokemon.name.equals("ゼニガメ"))
			myPokemonName = new JLabel(myPokemon.name + "         Lv: 50");
		else
			myPokemonName = new JLabel(myPokemon.name + "       Lv: 50");
		if(rivalPokemon.name.equals("フシギダネ") || rivalPokemon.name.equals("ゼニガメ"))
			rivalPokemonName = new JLabel(rivalPokemon.name + "         Lv: 50");
		else
			rivalPokemonName = new JLabel(rivalPokemon.name + "       Lv: 50");
		// フォントの設定(Windows)
		myPokemonName.setFont(new Font("", Font.BOLD, 28));
		rivalPokemonName.setFont(new Font("", Font.BOLD, 28));
		// HPを表示するmyPokemonHPとrivalPokemonHPの生成
		myPokemonHP = new JTextField(myPokemon.HP + " / " + myPokemon.HP);
		rivalPokemonHP = new JTextField(rivalPokemon.HP + " / " + rivalPokemon.HP);
		// myPokemonHPとrivalPokemonHPの編集を不可にする
		myPokemonHP.setEditable(false);
		rivalPokemonHP.setEditable(false);
		// フォントの設定(Windows)
		myPokemonHP.setFont(new Font("Century", Font.BOLD, 24));
		rivalPokemonHP.setFont(new Font("Century", Font.BOLD, 24));
		// ポケモンのタイプを表示するmyPokemonTypeとrivalPokemonTypeの生成
		// JLabel myPokemonType = new JLabel("タイプ： " + myPokemon.type);
		// JLabel rivalPokemonType = new JLabel("タイプ： " + rivalPokemon.type);
		// myPokemonLabelとrivalPokemonLabelのレイアウトを設定
		pokemonLayout.putConstraint(SpringLayout.SOUTH, myPokemonLabel, 0, SpringLayout.SOUTH, labelPane);
		pokemonLayout.putConstraint(SpringLayout.WEST, myPokemonLabel, 120, SpringLayout.WEST, labelPane);
		pokemonLayout.putConstraint(SpringLayout.NORTH, rivalPokemonLabel, 60, SpringLayout.NORTH, labelPane);
		pokemonLayout.putConstraint(SpringLayout.WEST, rivalPokemonLabel, 560, SpringLayout.WEST, labelPane);
		// myPokemonLabelとrivalPokemonLabelをlabelPaneに配置
		labelPane.add(myPokemonLabel);
		labelPane.add(rivalPokemonLabel);
		// myPokemonNameとrivalPokemonNameのレイアウトを設定
		pokemonLayout.putConstraint(SpringLayout.SOUTH, myPokemonName, -110, SpringLayout.SOUTH, labelPane);
		pokemonLayout.putConstraint(SpringLayout.EAST, myPokemonName, -85, SpringLayout.EAST, labelPane);
		pokemonLayout.putConstraint(SpringLayout.NORTH, rivalPokemonName, 80, SpringLayout.NORTH, labelPane);
		pokemonLayout.putConstraint(SpringLayout.WEST, rivalPokemonName, 90, SpringLayout.WEST, labelPane);
		// myPokemonLabelとrivalPokemonLabelのレイアウトを設定
		labelPane.add(myPokemonName);
		labelPane.add(rivalPokemonName);
		// myPokemonHPとrivalPokemonHPのレイアウトを設定
		pokemonLayout.putConstraint(SpringLayout.SOUTH, myPokemonHP, -44, SpringLayout.SOUTH, labelPane);
		pokemonLayout.putConstraint(SpringLayout.EAST, myPokemonHP, -102, SpringLayout.EAST, labelPane);
		pokemonLayout.putConstraint(SpringLayout.NORTH, rivalPokemonHP, 150, SpringLayout.NORTH, labelPane);
		pokemonLayout.putConstraint(SpringLayout.WEST, rivalPokemonHP, 235, SpringLayout.WEST, labelPane);
		// myPokemonLabelとrivalPokemonLabelのレイアウトを設定
		labelPane.add(myPokemonHP);
		labelPane.add(rivalPokemonHP);
		// 背景backgroundの設定
		JLabel background = new JLabel(new ImageIcon("Image/battle.jpg"));
		labelPane.add(background);
		
		// テキストとボタンを貼り付けるactionPaneの生成
		JPanel actionPane = new JPanel();
		actionPane.setLayout(new GridLayout(2, 0));
		
		// テキストを貼り付けるパネルを用意
		JPanel textPane = new JPanel();
		textPane.setLayout(new BorderLayout());
		// テキストのインスタンスを生成
		text = new JTextField(" わざを選んでください");
		// テキストのサイズの設定
		text.setPreferredSize(new Dimension(900, 50));
		// フォントの設定(Windows)
		text.setFont(new Font("MS 明朝", Font.PLAIN, 18));
		// テキストの枠線を設定
		text.setBorder(new LineBorder(Color.LIGHT_GRAY, 4, true));
		// テキストの編集を不可にする
		text.setEditable(false);
		// パネルにテキストを追加
		textPane.add(text, BorderLayout.CENTER);
		
		// テキストを読み進めるためのボタンを生成
		textButton = new JButton("▽");
		// フォントの設定(Windows)
		textButton.setFont(new Font("MS 明朝", Font.PLAIN, 20));
		// ボタンのサイズの設定
		textButton.setPreferredSize(new Dimension(80, 50));
		// ボタン押下時のイベントを追加
		textButton.addActionListener(this);
		// ボタンのアクションのコマンド
		textButton.setActionCommand("textButton");
		// パネルにボタンを追加
		textPane.add(textButton, BorderLayout.EAST);
		
		// ボタンを貼り付けるパネルを生成
		JPanel buttonPane = new JPanel();
		// パネルのレイアウトを設定
		buttonPane.setLayout(new GridLayout(0,4));
		// ボタンのインスタンスを生成
		button = new JButton[4];
		for(int i=0; i<4; i++) {
			button[i] = new JButton(myPokemon.waza[i].name);
			button[i].setPreferredSize(new Dimension(220, 50)); // ボタンのサイズを設定
			button[i].setFont(new Font("MS 明朝", Font.BOLD, 16)); // フォントの設定(Windows)
			button[i].addActionListener(this); // ボタン押下時のイベントを追加
			button[i].setActionCommand(myPokemon.waza[i].name); // ボタンのアクションのコマンド
			buttonPane.add(button[i]); // パネルにボタンを追加
		}
		
		// textPaneとbuttonPaneをactionPaneに配置
		actionPane.add(textPane);
		actionPane.add(buttonPane);
		// actionPaneをbattlePaneの下側に配置
		battlePane.add(actionPane, BorderLayout.SOUTH);
		// lanelPaneをbattlePaneの中央に配置
		battlePane.add(labelPane, BorderLayout.CENTER);
	}
	
	/* 4. 戦闘終了後の画面 resultPane */
	public void resultPane() {
		JLabel label = new JLabel("Thank you for playing!");
		label.setFont(new Font("Century", Font.PLAIN, 24));
		
		// タイトルに戻った後にもう一度対戦できるようにしたかったが、実装が間に合わなかったため断念
		// JButton button = new JButton("タイトルに戻る");
		// button.setPreferredSize(new Dimension(400, 60)); // menuButtonのサイズの設定
		// button.setFont(new Font("MS 明朝", Font.PLAIN, 24)); // ボタンの文字の設定
		// button.addActionListener(this); // ボタン押下時のイベントを追加
		// button.setActionCommand("titleButton"); // ボタンのアクションのコマンド
		
		resultPane.add(label);
		// resultPane.add(button);
	}
	
	@Override
	/* ボタンが押された時のアクション */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		String cmd = e.getActionCommand();
		if(source == titlemovie||cmd.equals("Title")) {
			titlePane();
			layout.next(cardPane);
		} else if(cmd.equals("hostStart")) {
			new Thread(new Runnable(){
				public void run() {
					try {
						PokemonServer.pokemonServer();
					} catch (IOException ioe) {
						errorMessage(ioe);
					}
				}
			}).start();
			try {
				selectPane();
				IP.setText(InetAddress.getLocalHost().getHostAddress());
				IP.setEditable(false);
				layout.next(cardPane);
			} catch (UnknownHostException uhe) {
				errorMessage(uhe);
			}
		} else if(cmd.equals("guestStart")) {
			selectPane();
			layout.next(cardPane);
		} else if(cmd.equals("OKButton")) {
			// もしIPアドレスが未記入orポケモンが未選択の場合は戻る
			if(IP.getText().equals("") || check == false) return;
			ipAddress = IP.getText();
			// System.out.println("自分のポケモン： " + myPokemon.name);
			try {
				connect(); // サーバーと接続
			} catch (IOException ioe) {
				errorMessage(ioe);
			}
		} else if(cmd.equals("pokemonButton0")) {
			myPokemon = new Pikachu();
			myPokemonIcon = new ImageIcon(this.getClass().getResource("image/pikachu.jpg"));
			check = true;
		} else if(cmd.equals("pokemonButton1")) {
			myPokemon = new Fushigidane();
			myPokemonIcon = new ImageIcon(this.getClass().getResource("image/fushigidane.jpg"));
			check = true;
		} else if(cmd.equals("pokemonButton2")) {
			myPokemon = new Hitokage();
			myPokemonIcon = new ImageIcon(this.getClass().getResource("image/hitokage.jpg"));
			check = true;
		} else if(cmd.equals("pokemonButton3")) {
			myPokemon = new Zenigame();
			myPokemonIcon = new ImageIcon(this.getClass().getResource("image/zenigame.jpg"));
			check = true;
		} else if(cmd.equals(myPokemon.waza[0].name)) {
			myWaza = new PokemonWaza(myPokemon.waza[0]);
			buttonDisabled();
			try {
				pokemonBattle(0);
			} catch (IOException | InterruptedException e0) {}
		} else if(cmd.equals(myPokemon.waza[1].name)) {
			myWaza = new PokemonWaza(myPokemon.waza[1]);
			buttonDisabled();
			try {
				pokemonBattle(1);
			} catch (IOException | InterruptedException e1) {}
		} else if(cmd.equals(myPokemon.waza[2].name)) {
			myWaza = new PokemonWaza(myPokemon.waza[2]);
			buttonDisabled();
			try {
				pokemonBattle(2);
			} catch (IOException | InterruptedException e2) {}
		} else if(cmd.equals(myPokemon.waza[3].name)) {
			myWaza = new PokemonWaza(myPokemon.waza[3]);
			buttonDisabled();
			try {
				pokemonBattle(3);
			} catch (IOException | InterruptedException e3) {}
		} else if(cmd.equals("textButton")) {
			try {
				recieveText(1); //後攻の処理
				textButton.setEnabled(false); // textButtonを操作不可にする
				buttonEnabled(); // 技ボタン操作可能にする
			} catch (IOException ioe) {
				errorMessage(ioe);
			}
		} else if(cmd.equals("titleButton")) {
			try {
				socket.close();
			} catch (IOException ioe) {
				errorMessage(ioe);
			} finally {
				layout.first(cardPane);				
			}
		}
	}
	
	/* 通信処理を行うメソッド */
	public void connect() throws IOException {
		socket = new Socket(ipAddress, 8080);
		try {
			// System.out.println("socket = " + socket);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			// mainFrameの閉じるボタンを押下時に接続を終了する
			mainFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					try {
						socket.close();
					} catch (IOException ioe) {
						errorMessage(ioe);
					}
				}
			});
			pokemonSelect(); // ポケモンの選択結果を送信するメソッド
			battlePane(); // 戦闘画面battlePaneの生成
			layout.next(cardPane); // カードの切り替え(battlePaneに切り替え)
		} finally {
			// System.out.println("closing...");
		}
	}
	
	/* ポケモンの選択結果を送信するメソッド */
	public void pokemonSelect() throws IOException {
		// 自分のポケモンの名前を送信
		out.println(myPokemon.name);
		// 相手のポケモンの名前を受信
		String name = in.readLine();
		// 相手の名前によって相手のポケモンを判定
		if(name.equals("ピカチュウ")) {
			rivalPokemon = new Pikachu();
			rivalPokemonIcon = new ImageIcon(this.getClass().getResource("image/pikachu.jpg"));
		} else if(name.equals("フシギダネ")) {
			rivalPokemon = new Fushigidane();
			rivalPokemonIcon = new ImageIcon(this.getClass().getResource("image/fushigidane.jpg"));
		} else if(name.equals("ヒトカゲ")) {
			rivalPokemon = new Hitokage();
			rivalPokemonIcon = new ImageIcon(this.getClass().getResource("image/hitokage.jpg"));
		} else if(name.equals("ゼニガメ")) {
			rivalPokemon = new Zenigame();
			rivalPokemonIcon = new ImageIcon(this.getClass().getResource("image/zenigame.jpg"));
		}
		// 自分のポケモンと相手のポケモンの最大HPを保存
		myPokemonMaxHP = myPokemon.HP;
		rivalPokemonMaxHP = rivalPokemon.HP;
		// System.out.println("相手のポケモン： " + rivalPokemon.name);
	}
	
	/* バトル処理を行うメソッド */
	public void pokemonBattle(int cmd) throws IOException, InterruptedException {
		// 自分の選択した技の番号を送信
		out.println(cmd);
		// 相手の選択した技の番号を受信
		int n = Integer.parseInt(in.readLine());
		rivalWaza = new PokemonWaza(rivalPokemon.waza[n]);
		
		// 先攻/後攻両方を受信
		for(int i=0; i<2; i++) {
			// text1に受信した文字列を格納
			text1[i] = in.readLine();
			// 攻撃されたポケモンの名前と残りHPを受信
			text2[i] = in.readLine();
			pokemonHP[i] = Integer.parseInt(in.readLine());
		}
		// 先攻の処理
		recieveText(0);
		textButton.setEnabled(true); // textButtonを操作可能にする
	}
	
	/* 受信したテキストを表示するメソッド */
	public void recieveText(int n) throws IOException {
		text.setText(text1[n]);
		if(text2[n].equals(myPokemon.name)) {
			myPokemon.HP = pokemonHP[n];
			myPokemonHP.setText(myPokemon.HP + " / " + myPokemonMaxHP);
		} else {
			rivalPokemon.HP = pokemonHP[n];
			rivalPokemonHP.setText(rivalPokemon.HP + " / " + rivalPokemonMaxHP);
		}
		if(pokemonHP[n] == 0) {
			if(myPokemon.HP == 0) 
				JOptionPane.showMessageDialog(mainFrame, "あなたの負けです", "結果",JOptionPane.PLAIN_MESSAGE);
			if(rivalPokemon.HP == 0)
				JOptionPane.showMessageDialog(mainFrame, "あなたの勝ちです", "結果",JOptionPane.PLAIN_MESSAGE);
			resultPane();
			layout.next(cardPane);
			return;
		}
	}
	
	/* エラーメッセージを表示するメソッド */
	public void errorMessage(Exception e) {
		String text = "<html>エラーが発生しました<br>"+e+"</html>";
		JOptionPane.showMessageDialog(mainFrame, text, "エラー", JOptionPane.ERROR_MESSAGE);
	}
	
	/* 戦闘時の技ボタンを操作不可にするメソッド */
	public void buttonDisabled() {
		for(int i=0; i<4; i++) button[i].setEnabled(false);
	}
	
	/* 戦闘時の技ボタンを操作可にするメソッド */
	public void buttonEnabled() {
		for(int i=0; i<4; i++) button[i].setEnabled(true);
	}
	
	public static void main(String[] args) {
		PokemonClient pokemonGame = new PokemonClient();
		pokemonGame.titlemoviePane();
		pokemonGame.mainFrame.setVisible(true);
	}
	
	/* 追加部分 */
	@SuppressWarnings("serial")
	class pict extends JPanel{
		private Image background;
		//private BufferedImage image;
		
		pict(){
			state++;
			switch(state){
			case 1:
				background = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("image/title.gif"));
				//background = new ImageIcon(image1).getImage();
				setOpaque(false);
				titlemovieSound("music/music1.wav");
				break;
			case 2:
				background = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("image/title2.gif"));
				//background = new ImageIcon(image1).getImage();
				setOpaque(false);
				break;
			default:
				break;
			}
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2D = (Graphics2D) g;
			// ウィンドウ内に描画したい図形をここに書く
			g2D.drawImage(background,0,0,this);
			g2D.dispose();
		}	
	}
}