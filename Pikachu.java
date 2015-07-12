import javax.swing.ImageIcon;

public class Pikachu extends Pokemon {
	Pikachu(){
		super("ピカチュウ", "電気", 800, 100, 100, 100, 100, 150);
		icon = new ImageIcon("image/pikachu.jpg");
		waza[0] = new PokemonWaza("ひっかく", "ノーマル", 40);
		waza[1] = new PokemonWaza("でんきショック", "電気", 40);
		waza[2] = new PokemonWaza("10まんボルト", "電気", 95);
		waza[3] = new PokemonWaza("かみなり", "電気", 120);
	}
}
