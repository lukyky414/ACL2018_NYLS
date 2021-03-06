package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import dao_Txt.MapTxtDAO;
import engine.Cmd;
import engine.Game;
import exceptions.CorruptDataException;
import model.entity.*;
import model.option.Option;
import model.plateau.Map;
import model.plateau.Square;

/**
 * @author Horatiu Cirstea, Vincent Thomas
 *
 *         Version avec personnage qui peut se deplacer. A completer dans les
 *         versions suivantes.
 * 
 */
public class LabyrinthGame implements engine.Game {
	private static int nbLevel = 3;
	//private int level =0 ;
	private GameState state;
	private Option option;

	/**
	 * constructeur avec fichier source pour le help
	 * 
	 */

	private Map map;

	private Hero hero;
	/*private Goblin goblin;
	private Ghost ghost;*/
	private ArrayList<Movable> entites;

	public LabyrinthGame(String source) throws CorruptDataException {
		BufferedReader helpReader;
		option = new Option();
		try {
			helpReader = new BufferedReader(new FileReader(source));
			String ligne;
			while ((ligne = helpReader.readLine()) != null) {
				System.out.println(ligne);
			}
			helpReader.close();
		} catch (IOException e) {
			System.out.println("Help not available");
		}
		map = MapTxtDAO.getInstance().load(0);
		//System.out.println(map.toString());

		state = GameState.RUN;
		loadEntity();
	}

	private void loadEntity(){
		entites = new ArrayList<Movable>();
		this.hero = null;
		Entity ent;
		for(Square sq : map){
			ent = sq.getEntity();
			if(ent != null){
				entites.add((Movable) ent);
				if(ent instanceof Hero)
					this.hero = (Hero) ent;
			}
		}
	}

	public Map getMap(){
		return map;
	}
	
	/**
	 * load the next level, if there is none, reload the current one
	 * @throws CorruptDataException
	 */
	
	public void loadNextLevel() throws CorruptDataException{
		if(map.getLevelNumber()<nbLevel){
			map.setLevelNumber(map.getLevelNumber()+1);
		}
		map = MapTxtDAO.getInstance().load(map.getLevelNumber());
		loadEntity();
	}

	@Override
	public Hero getHero(){
		return hero;
	}

	/**
	 * faire evoluer le jeu avec la commande actuelle.
	 * Est executee toutes les 15ms
	 */
	@Override
	public void evolve() {
		if(state==GameState.RUN){

			for(Movable e : entites){
				e.evolve();
				e.move();
			}

			for(Movable e : entites){
				if(e instanceof Monster)
					e.attack();
			}

			deleteDead();
		}
		
	}

	private void deleteDead(){
		for(int i = 0; i < entites.size(); i++){
			if(entites.get(i).isDead()){
				entites.get(i).getPos().setEntity(null);
				entites.remove(i);
				i--;
			}
		}
		if (hero.isDead()){
			state = GameState.OVER;
		}
	}

	/**
	 * verifier si le jeu est fini
	 */
	@Override
	public boolean isFinished() {
		return map.isFinished();
	}


	public ArrayList<Movable> getEntities(){
		return entites;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public Option getOption() {
		return option;
	}
	
	public void saveGame(){
		MapTxtDAO.getInstance().save(map);
	}
	public void loadGame(){
		try {
			map = MapTxtDAO.getInstance().load(-1);
			loadEntity();
			setState(GameState.RUN);
		} catch (CorruptDataException e) {
			e.printStackTrace();
		}
	}
	
	public void newGame(){
		try {
			map = MapTxtDAO.getInstance().load(0);
			loadEntity();
			setState(GameState.RUN);
		} catch (CorruptDataException e) {
			e.printStackTrace();
		}
	}
}
