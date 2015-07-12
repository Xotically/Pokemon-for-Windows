import java.io.*;
import java.net.*;

public class PokemonServer {
	public static final int PORT = 8080;
	public static Socket socket[] = new Socket[2];
	public static BufferedReader in[] = new BufferedReader[2];
	public static PrintWriter out[] = new PrintWriter[2];
	public static Pokemon pokemon[] = new Pokemon[2];
	public static PokemonWaza waza[] = new PokemonWaza[2];
	public static int damage;
	public static boolean check = true;
	public static String text = null;
	
	public static void pokemonServer() throws IOException {
		ServerSocket s = new ServerSocket(PORT);
		System.out.println("Started : " + s); // ポート番号を表示
		System.out.println("IPアドレス : " + InetAddress.getLocalHost().getHostAddress()); // 自身のIPアドレスを取得
		try {
			for(int i=0; i<2; i++) {
				socket[i] = s.accept();
				System.out.println("Connection accepted: " + socket[i]);
			}
			try {
				for(int i=0; i<2; i++) {
					in[i] = new BufferedReader(new InputStreamReader(socket[i].getInputStream()));
					out[i] = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket[i].getOutputStream())), true);
				}
				// ポケモン選択結果の送受信
				pokemonSelect();
				// どちらかのHPが0になるまで戦闘
				while(check) pokemonBattle();
				// 勝者判定
				if(pokemon[0].HP == 0) System.out.println(pokemon[1].name + "の勝利です");
				if(pokemon[1].HP == 0) System.out.println(pokemon[0].name + "の勝利です");
			} finally {
				System.out.println("closing...");
				for(int i=0; i<2; i++) socket[i].close();
			}
		} finally {
			s.close();
		}
	}
	
	/* ポケモンの選択結果を送受信するメソッド */
	private static void pokemonSelect() throws IOException {
		// ポケモンの選択結果の受信
		for(int i=0; i<2; i++) {
			// 選択されたポケモンの名前を受信
			String name = in[i].readLine();
			// 名前によって選択されたポケモンを判定
			if(name.equals("ピカチュウ")) {
				pokemon[i] = new Pikachu();
			} else if(name.equals("フシギダネ")) {
				pokemon[i] = new Fushigidane();
			} else if(name.equals("ヒトカゲ")) {
				pokemon[i] = new Hitokage();
			} else if(name.equals("ゼニガメ")) {
				pokemon[i] = new Zenigame();
			}
		}
		// 相手が選択したポケモンの名前を送信
		out[0].println(pokemon[1].name);
		out[1].println(pokemon[0].name);
	}
	
	private static void pokemonBattle() throws IOException {
		int cmd[] = new int[2];
		// プレイヤーが選択した技を受信
		for(int i=0; i<2; i++) {
			cmd[i] = Integer.parseInt(in[i].readLine());
			waza[i]= new PokemonWaza(pokemon[i].waza[cmd[i]]);
		}
		// 相手が選択した技の番号を送信
		out[0].println(cmd[1]);
		out[1].println(cmd[0]);
		
		// 両者のポケモンのスピードが同じ場合
		if(pokemon[0].speed == pokemon[1].speed) {
			int random = (int)(Math.random()*10);
			if(random < 5) {
				pokemonDamage(0,1); // 先攻の処理
				pokemonDamage(1,0); // 後攻の処理
			} else {
				pokemonDamage(1,0); // 先攻の処理
				pokemonDamage(0,1); // 後攻の処理
			}
		}
		
		if(pokemon[0].speed > pokemon[1].speed) {
			pokemonDamage(0,1); // 先攻の処理
			pokemonDamage(1,0); // 後攻の処理
		}
		
		if(pokemon[0].speed < pokemon[1].speed) {
			pokemonDamage(1,0); // 先攻の処理
			pokemonDamage(0,1); // 後攻の処理
		}
	}
	
	/* 技の処理を行うメソッド */
	private static void pokemonDamage(int attackPlayer, int attackedPlayer) {
		int p1 = attackPlayer; // 攻撃する側のプレイヤーをp1とする
		int p2 = attackedPlayer; // 攻撃される側のプレイヤーをp2とする
				
		text = pokemon[p1].name + "は" + waza[p1].name + "をくりだした"; // 送信する文字列
		
		// ダメージ計算
		damage = DamageCalculation(waza[p1], pokemon[p1], pokemon[p2]);
		pokemon[p2].HP -= damage;
		
		// テキストに表示する文字列の送信
		for(int i=0; i<2; i++) {
			if(i==p1) out[i].println("自分の" + text);
			if(i!=p1) out[i].println("相手の" + text);
		}
		
		if(pokemon[p2].HP < 0) pokemon[p2].HP = 0;
		// 攻撃を受けたポケモンの名前と残りHPを送信
		for(int i=0; i<2; i++) {
			out[i].println(pokemon[p2].name);
			out[i].println(pokemon[p2].HP);
		}
		
		// 攻撃を受けたポケモンのHPが0になったらループ終了
		if(pokemon[p2].HP == 0) {
			check = false;
		} else {
			System.out.println("player[" + p1 + "] : " + text);
			System.out.println("player[" + p1 + "] : " + pokemon[p2].name + "の HP  " + pokemon[p2].HP + " (" + damage +  "のダメージ)");
		}
	}
	
	/* ダメージ計算を行うメソッド */
	private static int DamageCalculation(PokemonWaza waza, Pokemon attackPokemon, Pokemon attackedPokemon) {
		int damage = waza.damage;
		
		// 急所判定
		int random = (int) (Math.random()*16);
		if(random == 0) {
			damage = damage * 2;
			text = text + "   (急所にあたった)";
		}
		
		// タイプ一致判定
		if(waza.type.equals(attackedPokemon.type)) damage = (int) (damage * 1.5);
		
		// タイプ相性判定
		int check = 0;
		switch(waza.type) {
		case "ノーマル":
			break;
		case "草":
			if(attackedPokemon.type.equals("水")) check++;
			if(attackedPokemon.type.equals("草")) check--;
			if(attackedPokemon.type.equals("炎")) check--;
			break;
		case "炎":
			if(attackedPokemon.type.equals("草")) check++;
			if(attackedPokemon.type.equals("炎")) check--;
			if(attackedPokemon.type.equals("水")) check--;
			break;
		case "水":
			if(attackedPokemon.type.equals("炎")) check++;
			if(attackedPokemon.type.equals("水")) check--;
			if(attackedPokemon.type.equals("草")) check--;
			break;
		case "電気":
			if(attackedPokemon.type.equals("水")) check++;
			if(attackedPokemon.type.equals("電気")) check--;
			if(attackedPokemon.type.equals("草")) check--;
			break;
		default:
			break;
		}
		if(check > 0) {
			damage = (int) (damage * Math.pow(2, check));
			text = text + "      (効果はばつぐんだ!)";
		}
		if(check < 0) {
			damage = (int) (damage * Math.pow(2, check));
			text = text + "      (効果はいまひとつだ...)";
		}
		
		if(damage == 0) text = text + "      (しかし何も起こらなかった...)";
		
		// 乱数
		random = (int) (Math.random()*16) + 85;
		damage = damage * random / 100; // 0.85〜1.0倍
		
		return damage;
	}

}
