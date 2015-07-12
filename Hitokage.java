import javax.swing.ImageIcon;

public class Hitokage extends Pokemon {
	Hitokage(){
		super("ヒトカゲ", "炎", 800, 100, 100, 100, 100, 100);
		icon = new ImageIcon("image/hitokage.jpg");
		waza[0] = new PokemonWaza("ひっかく", "ノーマル", 40);
		waza[1] = new PokemonWaza("ひのこ", "炎", 40);
		waza[2] = new PokemonWaza("いかく", "ノーマル", 0);
		waza[3] = new PokemonWaza("だいもんじ", "炎", 120);
	}
}
