import javax.swing.ImageIcon;

public class Fushigidane extends Pokemon {
	Fushigidane(){
		super("フシギダネ", "草", 1000, 100, 100, 100, 100, 80);
		icon = new ImageIcon("image/fushigidane.jpg");
		waza[0] = new PokemonWaza("たいあたり", "ノーマル", 40);
		waza[1] = new PokemonWaza("つるのむち", "草", 40);
		waza[2] = new PokemonWaza("のしかかり", "草", 90);
		waza[3] = new PokemonWaza("ソーラービーム", "草", 120);
	}
}
