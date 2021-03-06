package model.entity;

import engine.Cmd;
import model.factory.TextureFactory;
import model.plateau.Square;
import model.plateau.Wall;

import java.awt.*;

public class Ghost extends Monster{

	public Ghost(Square position, int hp, int atk, Hero target, int difficulty) {
		super(position,hp, atk, 10, target, difficulty);
		texture = TextureFactory.getTexGhost();
	}

	@Override
	public String getType() {
		return "Ghost";
	}

	@Override
	public Image getTexture() {
		Image ret = null;
		switch(orientation){
			case NORTH:
				ret = texture.getSubimage(0, 12, 70, 76);
				break;
			case WEST:
				ret = texture.getSubimage(0, 238, 70, 76);
				break;
			case SOUTH:
				ret = texture.getSubimage(0, 166, 70, 76);
				break;
			case EAST:
				ret = texture.getSubimage(0, 88, 70, 76);
				break;
		}
		return ret;
	}

	/**
	 * Verifie si la prochaine case contient deja une entite pour attaquer
	 * Permet de traverser des murs
	 *
	 * @return false (entite), true si valide
	 */
	@Override
	protected boolean canMove(Square nextPos){
		if(nextPos == null)
			return false;
		if(nextPos.getEntity() != null){
			return false;
		}
		return true;
	}

	/**
	 * Utilise dans l'IA difficile.
	 * Le fantome peut passer dans les murs.
	 */
	@Override
	protected boolean validityTest(Square sq){
		return sq != null/* && !(sq instanceof Wall)*/;
	}
}
