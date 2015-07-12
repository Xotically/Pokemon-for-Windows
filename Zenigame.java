import javax.swing.ImageIcon;

public class Zenigame extends Pokemon {
	Zenigame(){
		super("ゼニガメ", "水", 1000, 100, 100, 100, 100, 70);
		icon = new ImageIcon("image/zenigame.jpg");
		waza[0] = new PokemonWaza("ひっかく", "ノーマル", 40);
		waza[1] = new PokemonWaza("みずでっぽう", "水", 40);
		waza[2] = new PokemonWaza("からにこもる", "ノーマル", 0);
		waza[3] = new PokemonWaza("ハイドロポンプ", "水", 120);
	}
}
