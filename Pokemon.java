import javax.swing.ImageIcon;

public class Pokemon {
	String name;
	String type;
	int HP;
	int attack;
	int block;
	int contack;
	int diffence;
	int speed;
	ImageIcon icon;
	PokemonWaza[] waza = new PokemonWaza[4];

	public Pokemon() {
	}

	public Pokemon(String n, String t, int hp, int a, int b, int c, int d, int s) {
		name = n;
		type = t;
		HP = hp;
		attack = a;
		block = b;
		contack = c;
		diffence = d;
		speed = s;
	}

	public void setWazaName(String name1,String name2, String name3, String name4) {
		waza[0].name = name1;
		waza[1].name = name2;
		waza[2].name = name3;
		waza[3].name = name4;
	}

	public void setWazaDamage(int damage1, int damage2, int damage3, int damage4) {
		waza[0].damage = damage1;
		waza[1].damage = damage2;
		waza[2].damage = damage3;
		waza[3].damage = damage4;
	}

	public void setWazaType(String type1, String type2, String type3, String type4) {
		waza[0].type = type1;
		waza[1].type = type2;
		waza[2].type = type3;
		waza[3].type = type4;
	}
}
