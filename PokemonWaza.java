public class PokemonWaza {
	String name;
	String type;
	int damage;
	
	public PokemonWaza() {		
	}

	public PokemonWaza(PokemonWaza waza) {
		name = waza.name;
		type = waza.type;
		damage = waza.damage;
	}
	
	public PokemonWaza(String name, String type, int damage) {
		this.name = name;
		this.type = type;
		this.damage = damage;
	}
	
	public void setPokemonWaza(PokemonWaza waza) {
		name = waza.name;
		type = waza.type;
		damage = waza.damage;
	}

}
