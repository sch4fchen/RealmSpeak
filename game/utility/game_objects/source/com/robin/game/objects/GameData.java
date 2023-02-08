package com.robin.game.objects;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.robin.general.io.*;
import com.robin.general.util.*;

public class GameData extends ModifyableObject implements Serializable {
	
	private static long c_dataid = 0;
	private long dataid = c_dataid++;
	public long getDataId() {
		return dataid;
	}
	
	public boolean reportFormatErrors = true;
	public boolean ignoreRandomSeed = false;
	
	private String dataName = "defaultDataName"; // can use this variable (which is never saved in xml) to identify local instances of GameData
	
	private static final String ZIP_INTERNAL_FILENAME = "GameData_CHEATER_.xml";

	protected long cumulative_id = 0;
	
	protected long dataVersion = 0; // updated each time there is a real change?
	
	protected boolean filter = false;

	protected String gameName;
	protected String gameDesc;
	protected String scenarioDesc;
	protected boolean scenarioRegenerateRandomNumbers;
	protected boolean scenarioRandomGoldSpecialPlacement;
	protected boolean scenarioAddNewQuests;
	protected boolean scenarioRebuildQuestDeck;
	protected boolean scenarioShuffleQuestDeck;
	protected String filterString;
	protected ArrayList<GameObject> excludeList;
	protected ArrayList<GameObject> gameObjects;
	protected HashMap<Long, GameObject> gameObjectIDHash;
	protected HashLists<String,GameObject> gameObjectNameHash;
	protected ArrayList<GameObject> filteredGameObjects;
	
	protected ArrayList<GameSetup> gameSetups;
	
	protected boolean tracksChanges = false;
	private ArrayList<GameObjectChange> objectChanges;
	
	private ChangeListener modifyListener = new ChangeListener() {
		public void stateChanged(ChangeEvent ev) {
			setModified(true);
		}
	};
	
	public GameData() {
		this("SHOULDNT_SEE_THIS");
	}
	public GameData(String name) {
		filterString = null;
		excludeList = null;
		gameName = name;
		gameObjects = new ArrayList<>();
		gameObjectIDHash = new HashMap<>();
		gameObjectNameHash = new HashLists<>();
		filteredGameObjects = new ArrayList<>();
		gameSetups = new ArrayList<>();
		setModified(true);
	}
	public String getCheckSum() {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			for (GameObject go : gameObjects) {
				md.update(go.getName().getBytes());
				OrderedHashtable hash = go.getAttributeBlocks();
				ArrayList<String> blocks = new ArrayList<>(hash.keySet());
				Collections.sort(blocks);
				for (String blockName : blocks) {
					OrderedHashtable block = (OrderedHashtable)hash.get(blockName);
					ArrayList<String> keys = new ArrayList(block.keySet());
					Collections.sort(keys);
					for (String key : keys) {
						Object val = block.get(key);
						md.update(key.getBytes());
						md.update(val.toString().getBytes());
					}
				}
			}
			byte[] bytes = md.digest();
			StringBuffer sb=new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String hex=Integer.toHexString(0xff & bytes[i]);
				if(hex.length()==1) sb.append('0');
				sb.append(hex);
			}
			return sb.toString();
		}
		catch(NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public void setFilter(boolean val) {
		filter = val;
	}
	public ChangeListener getModifyListener() {
		return modifyListener;
	}
	public GameData copy() {
		GameData data = new GameData(gameName);
		for (GameObject go : getGameObjects()) {
			GameObject goCopy = data.createNewObject(go);
			goCopy.copyFrom(go);
		}
		for (GameObject go : data.getGameObjects()) {
			go.resolveHold(data.getGameObjectIDHash());
		}
		return data;
	}
	public boolean hasChanges() {
		return tracksChanges && objectChanges!=null && !objectChanges.isEmpty();
	}
	public void setGameName(String val) {
		gameName = val;
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameDescription(String val) {
		gameDesc = val;
		setModified(true);
	}
	public String getGameDescription() {
		return gameDesc;
	}
	public void setScenarioDescription(String val) {
		scenarioDesc = val;
		setModified(true);
	}
	public String getScenarioDescription() {
		return scenarioDesc;
	}
	public void removeScenarioDescription() {
		scenarioDesc = null;
	}
	public void setScenarioRegenerateRandomNumbers(boolean val) {
		scenarioRegenerateRandomNumbers = val;
	}
	public boolean getScenarioRegenerateRandomNumbers() {
		return scenarioRegenerateRandomNumbers;
	}
	public void setScenarioRandomGoldSpecialPlacement(boolean val) {
		scenarioRandomGoldSpecialPlacement = val;
	}
	public boolean getScenarioRandomGoldSpecialPlacement() {
		return scenarioRandomGoldSpecialPlacement;
	}
	public void setScenarioAddNewQuests(boolean val) {
		scenarioAddNewQuests = val;
	}
	public boolean getScenarioAddNewQuests() {
		return scenarioAddNewQuests;
	}
	public void setScenarioRebuildQuestDeck(boolean val) {
		scenarioRebuildQuestDeck = val;
	}
	public boolean getScenarioRebuildQuestDeck() {
		return scenarioRebuildQuestDeck;
	}
	public void setScenarioShuffleQuestDeck(boolean val) {
		scenarioShuffleQuestDeck = val;
	}
	public boolean getScenarioShuffleQuestDeck() {
		return scenarioShuffleQuestDeck;
	}
	public GameObject getGameObject(long id) {
		return getGameObject(Long.valueOf(id));
	}
	public GameObject getGameObject(Long id) {
		return gameObjectIDHash.get(id);
	}
	public GameObject getGameObject(Object obj){
		String id = (String)obj;
		return getGameObject(Long.valueOf(id));
	}
	
	public ArrayList<GameObject> getGameObjects() {
		return gameObjects;
	}
	public HashMap<Long, GameObject> getGameObjectIDHash() {
		return gameObjectIDHash;
	}
	public Set<String> getAllGameObjectNames() {
		return gameObjectNameHash.keySet();
	}
	public GameObject getGameObjectByNameIgnoreCase(String name) {
		for(String test:gameObjectNameHash.keySet()) {
			if (test.equalsIgnoreCase(name)) {
				return gameObjectNameHash.getList(test).get(0);
			}
		}
		return null;
	}
	public ArrayList<GameObject> getGameObjectsByNameIgnoreCase(String name) {
		ArrayList<GameObject> ret = new ArrayList<>();
		for(String test:gameObjectNameHash.keySet()) {
			if (test.equalsIgnoreCase(name)) {
				ret.addAll(gameObjectNameHash.getList(test));
			}
		}
		return ret;
	}
	public GameObject getGameObjectByName(String name) {
		ArrayList<GameObject> list = getGameObjectsByName(name);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	public ArrayList<GameObject> getGameObjectsByName(String name) {
		ArrayList<GameObject> ret = new ArrayList<>();
		ArrayList<GameObject> val = gameObjectNameHash.getList(name);
		if (val!=null) {
			ret.addAll(val);
		}
		return ret;
	}
	public ArrayList<GameObject> getGameObjectsByNameRegex(String nameRegex) {
		ArrayList<GameObject> ret = new ArrayList<>();
		String regex = nameRegex.trim()+".*";
		Pattern pattern = Pattern.compile(regex);
		for(String test:gameObjectNameHash.keySet()) {
			if (pattern.matcher(test.trim()).find()) {
				ret.addAll(gameObjectNameHash.getList(test));
			}
		}
		return ret;
	}
	public void renumberObjectsByName() {
		Collections.sort(gameObjects,new Comparator<GameObject>() {
			public int compare(GameObject g1,GameObject g2) {
				return g1.getName().compareTo(g2.getName());
			}
		});
		renumberObjects();
	}
	public void moveObjectsBefore(ArrayList<GameObject> objects,GameObject indexObject) {
		moveObjects(objects,indexObject,true);
	}
	public void moveObjectsAfter(ArrayList<GameObject> objects,GameObject indexObject) {
		moveObjects(objects,indexObject,false);
	}
	/**
	 * Moves the objects to the position BEFORE the GameObject with an id==idPosition
	 */
	private void moveObjects(ArrayList<GameObject> objects,GameObject indexObject,boolean before) {
		// First, verify ALL objects are in the list, and that the list is uniqued
		ArrayList<GameObject> validObjects = new ArrayList<>();
		for (GameObject go : objects) {
			if (go.parent==this && gameObjects.contains(go) && !validObjects.contains(go)) {
				validObjects.add(go);
			}
		}
		if (validObjects.size()!=objects.size()) {
			throw new IllegalStateException("Invalid object set to move!");
		}
		
		// Find the index of the specified id
		if (indexObject==null) {
			throw new IllegalStateException("Invalid indexObject!");
		}
		
		// Remove all valid objects
		gameObjects.removeAll(validObjects);
		
		int index = gameObjects.indexOf(indexObject);
		if (!before) index++;
		
		// Reinsert into specified position
		gameObjects.addAll(index,validObjects);
		
		// Renumber!
		renumberObjects();
	}
	/**
	 * Renumbers objects by their list order in gameObjects
	 */
	private void renumberObjects() {
		gameObjectIDHash.clear();
		cumulative_id = 0;
		for (GameObject go : gameObjects) {
			go.setId(cumulative_id++);
			gameObjectIDHash.put(Long.valueOf(go.getId()),go);
		}
		
		rebuildFilteredGameObjects();
		setModified(true);
	}
	public long getCumulativeId() {
		return cumulative_id;
	}
	public long getMaxId() {
		long max = 0;
		for (Long key : gameObjectIDHash.keySet()) {
			max = Math.max(key.longValue(),max);
		}
		return max;
	}
	public void renumberObjectsStartingWith(long startId) {
		gameObjectIDHash.clear();
		cumulative_id = startId;
		for (GameObject go : gameObjects) {
			go.setId(cumulative_id++);
			gameObjectIDHash.put(Long.valueOf(go.getId()),go);
		}
		
		rebuildFilteredGameObjects();
		setModified(true);
	}
	public void clearFilterAndExcludeList() {
		filterString = null;
		excludeList = null;
		rebuildFilteredGameObjects();
		setModified(true);
	}
	public void setFilterString(String filter) {
		filterString = filter;
		rebuildFilteredGameObjects();
		setModified(true);
	}
	public String getFilterString() {
		return filterString;
	}
	public void clearExcludeList() {
		excludeList = null;
		rebuildFilteredGameObjects();
	}
	public void setExcludeList(GameObject object) {
		ArrayList<GameObject> exclude = new ArrayList<>();
		exclude.add(object);
		setExcludeList(exclude);
	}
	public void setExcludeList(ArrayList<GameObject> exclude) {
		excludeList = exclude;
		rebuildFilteredGameObjects();
	}
	public ArrayList<GameObject> getExcludeList() {
		return excludeList;
	}
	public void rebuildFilteredGameObjects() {
		if (filter) {
			// Rebuild collection
			filteredGameObjects.clear();
			
			if (filterString==null) {
				filteredGameObjects.addAll(gameObjects);
			}
			else {
				// Filter gameObjects
				ArrayList<String> filterTerms = new ArrayList<>();
				StringTokenizer tokens = new StringTokenizer(filterString,",");
				while(tokens.hasMoreTokens()) {
					filterTerms.add(tokens.nextToken());
				}
				
				for (GameObject obj : gameObjects) {
					if (obj.hasAllKeyVals(filterTerms)) {
						// Conditions met - add it.
						filteredGameObjects.add(obj);
					}
				}
			}
			if (excludeList!=null) {
				// Remove exclude list
				filteredGameObjects.removeAll(excludeList);
			}
		}
	}
	public ArrayList<GameObject> getFilteredGameObjects() {
		return filter?filteredGameObjects:gameObjects;
	}
	public ArrayList<GameSetup> getGameSetups() {
		return gameSetups;
	}
	public void resetIdToMax(Collection<GameObject> c) {
		cumulative_id = 0;
		for (GameObject obj : c) {
			if (obj.getId()>cumulative_id) {
				cumulative_id = obj.getId();
			}
		}
		cumulative_id++;
	}
	public boolean zipFromFile(File zipFile) {
		ZipUtilities.unzip(zipFile);
		String path = FileUtilities.getFilePathString(zipFile,false,false);
		File tempFile = new File(path+ZIP_INTERNAL_FILENAME);
		if (loadFromFile(tempFile)) {
			tempFile.delete();
			return true;
		}
		return false;
	}
	/**
	 * This allows you to load from a file compressed in a jar archive
	 */
	public boolean loadFromPath(String path) {
		path = fixFilePath(path);
		InputStream stream = ResourceFinder.getInputStream(path);
		if (stream!=null) {
			return loadFromStream(stream);
		}
		System.err.println("GameData unable to loadFromPath.  "+path+" not found?");
		return false;
	}
	public boolean loadFromFile(File file) {
		file = fixFileExtension(file);
		try {
			InputStream stream = new FileInputStream(file);
			return loadFromStream(stream);
		}
		catch(FileNotFoundException ex) {
			System.out.println("Problem loading file: "+ex);
		}
		return false;
	}
	public boolean loadFromStream(InputStream stream) {
		try {
			// Load file
			Document doc = new SAXBuilder().build(stream);
			
			// Read game
			Element game = doc.getRootElement();
			setXML(game);
			return true;
		}
		catch(Exception ex) {
			if (reportFormatErrors) {
				JOptionPane.showMessageDialog(null,"Invalid file/format:\n\n"+ex,"Error",JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
		return false;
	}
	public void setXML(Element game) {
		gameName = game.getAttribute("name").getValue();
		gameDesc = game.getAttribute("description").getValue();
		if (game.getAttribute("scenarioDescription") != null) {
			scenarioDesc = game.getAttribute("scenarioDescription").getValue();
		}
		scenarioRegenerateRandomNumbers = (game.getAttribute("scenarioRegenerateRandomNumbers")!=null&&game.getAttribute("scenarioRegenerateRandomNumbers").getValue().matches("true"))?true:false;
		scenarioRandomGoldSpecialPlacement = (game.getAttribute("scenarioRandomGoldSpecialPlacement")!=null&&game.getAttribute("scenarioRandomGoldSpecialPlacement").getValue().matches("true"))?true:false;
		scenarioAddNewQuests = (game.getAttribute("scenarioAddNewQuests")!=null&&game.getAttribute("scenarioAddNewQuests").getValue().matches("true"))?true:false;
		scenarioRebuildQuestDeck = (game.getAttribute("scenarioRebuildQuestDeck")!=null&&game.getAttribute("scenarioRebuildQuestDeck").getValue().matches("true"))?true:false;
		scenarioShuffleQuestDeck = (game.getAttribute("scenarioShuffleQuestDeck")!=null&&game.getAttribute("scenarioShuffleQuestDeck").getValue().matches("true"))?true:false;
		String seedString = game.getAttributeValue("_rseed");
		if (!ignoreRandomSeed && seedString!=null) {
			String rt = game.getAttributeValue("_rgtype");
			RandomNumber.setRandomNumberGenerator(rt==null ? RandomNumberType.System : RandomNumberType.valueOf(rt));
			String countString = game.getAttributeValue("_rcount");
			RandomNumber.init(Long.valueOf(seedString),Long.valueOf(countString));
		}
		game.setAttribute(new Attribute("_rndSetup",String.valueOf(RandomNumber.getUseRandomNumberGeneratorForSetup())));
		
		// Read objects
		Collection<Element> objects = game.getChild("objects").getChildren();
		gameObjects.clear();
		gameObjectIDHash.clear();
		gameObjectNameHash.clear();
		for (Element obj : objects) {
			GameObject newObj = new GameObject(this);
			newObj.setXML(obj);
			gameObjects.add(newObj);
			gameObjectIDHash.put(Long.valueOf(newObj.getId()),newObj);
			gameObjectNameHash.put(newObj.getName(),newObj);
		}
		
		// Resolve objects (holds can't be calculated until all are loaded!)
		for (GameObject obj : gameObjects) {
			obj.resolveHold(gameObjectIDHash);
		}
		
		rebuildFilteredGameObjects();
		
		// Set cumulative_id to something real
		resetIdToMax(gameObjects);
		
		// Read setups
		Collection<Element> setups = game.getChild("setups").getChildren();
		gameSetups.clear();
		for (Element setup : setups) {
			GameSetup newSetup = new GameSetup(this);
			newSetup.setXML(setup);
			gameSetups.add(newSetup);
		}
		
		// Done.
		setModified(false);
	}
	public boolean zipToFile(File zipFile) {
		String path = FileUtilities.getFilePathString(zipFile,false,false);
		File tempFile = new File(path+ZIP_INTERNAL_FILENAME);
		
		if (saveToFile(tempFile)) {
			ArrayList<File> files = new ArrayList<>();
			files.add(tempFile);
			ZipUtilities.zip(zipFile,files.toArray(new File[files.size()]));
			tempFile.delete();
			return true;
		}
		
		return false;
	}
	public boolean saveToFile(File file) {
		file = fixFileExtension(file);
		Element game = getXML();
		
		// Save file
		try {
			FileOutputStream stream = new FileOutputStream(file);
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(game,stream);
			stream.close();
			setModified(false);
			return true;
		}
		catch(FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch(IOException ex) {
			if (reportFormatErrors) {
				ex.printStackTrace();
			}
		}
		return false;
	}
	public Element getXML() {
		// Build game
		Element game = new Element("game");
		game.setAttribute(new Attribute("file_version","1.0"));
		game.setAttribute(new Attribute("name",gameName));
		game.setAttribute(new Attribute("description",gameDesc==null?"":gameDesc));
		if (scenarioDesc!=null && !scenarioDesc.isEmpty()) {
			game.setAttribute(new Attribute("scenarioDescription",scenarioDesc==null?"":scenarioDesc));
		}
		if (scenarioRandomGoldSpecialPlacement) {
			game.setAttribute(new Attribute("scenarioRandomGoldSpecialPlacement","true"));
		}
		if (scenarioRegenerateRandomNumbers) {
			game.setAttribute(new Attribute("scenarioRegenerateRandomNumbers","true"));
		}
		if (scenarioAddNewQuests) {
			game.setAttribute(new Attribute("scenarioAddNewQuests","true"));
		}
		if (scenarioRebuildQuestDeck) {
			game.setAttribute(new Attribute("scenarioRebuildQuestDeck","true"));
		}
		if (scenarioShuffleQuestDeck) {
			game.setAttribute(new Attribute("scenarioShuffleQuestDeck","true"));
		}
		if (!ignoreRandomSeed && RandomNumber.hasBeenInitialized()) {
			game.setAttribute(new Attribute("_rseed",String.valueOf(RandomNumber.getSeed())));
			game.setAttribute(new Attribute("_rcount",String.valueOf(RandomNumber.getCount())));
			game.setAttribute(new Attribute("_rgtype",RandomNumber.getRandomNumberGenerator().toString()));
		}
		if (RandomNumber.hasBeenInitialized()) {
			game.setAttribute(new Attribute("_rndSetup",String.valueOf(RandomNumber.getUseRandomNumberGeneratorForSetup())));
		}
		
		// Build objects
		Element objects = new Element("objects");
		for (GameObject obj : gameObjects) {
			objects.addContent(obj.getXML());
		}
		game.addContent(objects);
		
		// Build setup
		Element setups = new Element("setups");
		for (GameSetup setup : gameSetups) {
			setups.addContent(setup.getXML());
		}
		game.addContent(setups);
		
		return game;
	}
	private static File fixFileExtension(File file) {
		return new File(file.getPath());
	}
	private static String fixFilePath(String path) {
		while(path.endsWith(File.separator) || path.endsWith(".")) {
			path = path.substring(0,path.length()-1);
		}
		if (!path.toLowerCase().endsWith(".xml")) {
			path = path + ".xml";
		}
		return path;
	}
	public void removeObject(GameObject obj) {
		int index = gameObjects.indexOf(obj);
		if (index>=0) {
//			if (tracksChanges) {
//				addChange(new GameObjectDeletionChange(obj));
//			}
			
			// Make sure all links are broken (these four lines were added 8/3/2007)
			// This is bad!  This breaks RealmSpeak, so I need a different solution!!!!
//			obj.clearHold();
//			if (obj.getHeldBy()!=null) {
//				obj.getHeldBy().remove(obj);
//			}
			
			gameObjects.remove(index);
			gameObjectIDHash.remove(Long.valueOf(obj.getId()));
			gameObjectNameHash.removeKeyValue(obj.getName(),obj);
			rebuildFilteredGameObjects();
			setModified(true);
		}
	}
	public void removeSetup(GameSetup setup) {
		int index = gameSetups.indexOf(setup);
		if (index>=0) {
			gameSetups.remove(index);
			setModified(true);
		}
	}
	public GameObject createNewObject() {
		return createNewObject(cumulative_id);
	}
	public GameObject createNewObject(long anId) {
		if (!gameObjectIDHash.containsKey(anId)) {
			if (anId>=cumulative_id) {
				cumulative_id = anId + 1;
			}
			GameObject obj = new GameObject(this,anId);
			gameObjects.add(obj);
			gameObjectIDHash.put(Long.valueOf(obj.getId()),obj);
			gameObjectNameHash.put(obj.getName(),obj);
			rebuildFilteredGameObjects();
			setModified(true);
			return obj;
		}
		throw new IllegalArgumentException("Cannot create an object with id "+anId+", because one already exists!!");
	}
	/**
	 * Creates a new game object that is a clone of the provided game object (same name and attributes)
	 */
	public GameObject createNewObject(GameObject go) {
		GameObject clone = createNewObject();
		clone.copyAttributesFrom(go);
		return clone;
	}
	public GameSetup createNewSetup() {
		GameSetup setup = new GameSetup(this);
		gameSetups.add(setup);
		setModified(true);
		return setup;
	}
	/**
	 * Replaces the game object with the same id.  Returns true on success.
	 */
	public boolean replaceObject(GameObject obj) {
		GameObject old = getGameObject(obj.getId());
		if (old!=null) {
			int oldIndex = gameObjects.indexOf(old);
			gameObjects.set(oldIndex,obj);
			gameObjectIDHash.put(Long.valueOf(obj.getId()),obj);
			gameObjectNameHash.removeKeyValue(old.getName(),old);
			gameObjectNameHash.put(obj.getName(),obj);
			return true;
		}
		return false;
	}
	protected void changingName(String oldName,String newName,GameObject obj) {
		gameObjectNameHash.removeKeyValue(oldName,obj);
		gameObjectNameHash.put(newName,obj);
	}
	/**
	 * Returns true if the provided object is the same one as found in GameData
	 */
	public boolean validate(GameObject obj) {
		GameObject real = getGameObject(obj.getId());
		return real==obj;
	}
	/**
	 * Provides an independant (deep) copy of the gameObjects collection
	 */
	private ArrayList<GameObject> getGameObjectsCopy() {
		HashMap<Long, GameObject> map = new HashMap<>();
		ArrayList<GameObject> goCopy = new ArrayList<>();
		for (GameObject obj : gameObjects) {
			GameObject theCopy = new GameObject(this);
			theCopy.copyFrom(obj);
			goCopy.add(theCopy);
			map.put(obj.getId(),theCopy);
		}
		for (GameObject obj : goCopy) {
			obj.resolveHold(map);
		}
		return goCopy;
	}
	public GameSetup findSetup(String setupName) {
		// Find setup
		for (GameSetup setup : gameSetups) {
			if (setup.getName().equals(setupName)) {
				return setup;
			}
		}
		return null;
	}
	public String[] getGameSetupNames() {
		String[] names = new String[gameSetups.size()];
		int n=0;
		for (GameSetup setup : gameSetups) {
			names[n++] = setup.getName();
		}
		return names;
	}
	
	/**
	 * Test setup - GameData itself is not modified - a copy of the objects is made
	 * prior to setup
	 */
	public ArrayList<GameObject> doTestSetup(String setupName) {
		return doTestSetup(new StringBuffer(),findSetup(setupName));
	}
	public ArrayList<GameObject> doTestSetup(StringBuffer result,String setupName) {
		return doTestSetup(result,findSetup(setupName));
	}
	public ArrayList<GameObject> doTestSetup(StringBuffer result,GameSetup setup) {
		if (setup!=null) {
			ArrayList<GameObject> aCopy = setup.processSetup(result,getGameObjectsCopy());
			return aCopy;
		}
		return null;
	}
	/**
	 * Process the game objects with a setup type
	 */
	public ArrayList<GameObject> doSetup(String setupName,ArrayList<String> keyVals) {
		return doSetup(new StringBuffer(),findSetup(setupName),keyVals);
	}
	public ArrayList<GameObject> doSetup(StringBuffer result,String setupName,ArrayList<String> keyVals) {
		return doSetup(result,findSetup(setupName),keyVals);
	}
	public ArrayList<GameObject> doSetup(StringBuffer result,GameSetup setup,ArrayList<String> keyVals) {
		if (setup!=null) {
			GamePool pool = new GamePool(gameObjects);
			ArrayList<GameObject> aCopy = setup.processSetup(result,pool.find(keyVals));
			return aCopy;
		}
		return null;
	}
	public String toString() {
		return gameName;
	}
	public void setModified(boolean val) {
		super.setModified(val);
		rebuildFilteredGameObjects();
	}
	public boolean isTracksChanges() {
		return tracksChanges;
	}
	/**
	 * Sets whether or not this data object will track changes.  If true, then all changes require a call to commit()
	 * to be set permanently.  To subvert this strategy, you can instead use the _set methods, but please don't.
	 */
	public void setTracksChanges(boolean tracksChanges) {
		this.tracksChanges = tracksChanges;
		if (tracksChanges) {
			objectChanges = new ArrayList<>();
		}
		else {
			objectChanges.clear();
			objectChanges = null;
		}
	}
	public long getDataVersion() {
		return dataVersion;
	}
	public synchronized void addChange(GameObjectChange change) {
		dataVersion++;
		if (tracksChanges) {
			objectChanges.add(change);
		}
		else throw new IllegalStateException("Cannot add a change to data when not tracking changes.");
	}
	public ArrayList<GameObjectChange> getObjectChanges() {
		return objectChanges;
	}
	public int getChangeCount() {
		if (objectChanges!=null) {
			return objectChanges.size();
		}
		return 0;
	}
	/**
	 * It's possible for the GameObjects to lose their uncommitted object, but still be changes remembered by this
	 * GameData object.  In this scenario, we need to restore synchronicity by rebuilding the changes.
	 */
	public void rebuildChanges() {
		if (objectChanges!=null && !objectChanges.isEmpty()) {
			ArrayList<GameObjectChange> rebuildObjectChanges = new ArrayList<GameObjectChange>(objectChanges);
			
			// First, go through all affected objects, and make sure that their uncommitted object is gone
			for (GameObjectChange change:rebuildObjectChanges) {
				change.getGameObject(this).stopUncommitted();
			}
			
			objectChanges.clear(); // Since we are rebuilding, clear out the changes so they can be added back
			for (GameObjectChange change:rebuildObjectChanges) {
				change.rebuildChange(this);
				// Note:  rebuildChange WILL cause feedback to GameData, and add a change to objectChanges
				//		This is why I'm using a new ArrayList, so there won't be any concurrent mods.
			}
		}
	}
	/**
	 * This pops changes off the objectChanges stack, and commits them immediately
	 */
	public synchronized ArrayList<GameObjectChange> popAndCommit() {
		ArrayList<GameObjectChange> list = new ArrayList<>();
		int size = objectChanges.size();
		if (size>0) {
			for (int i=0;i<size;i++) {
				GameObjectChange change = objectChanges.remove(0);
				change.applyChange(this);
				list.add(change);
			}
		}
		return list;
	}
	public synchronized void commit() {
		if (objectChanges!=null && !objectChanges.isEmpty()) {
			for (GameObjectChange change:objectChanges) {
				change.applyChange(this);
			}
			objectChanges.clear();
		}
	}
	public void rollback() {
		if (objectChanges!=null && !objectChanges.isEmpty()) {
			ArrayList<GameObject> objectsThatHaveChanged = new ArrayList<>();
			for (GameObjectChange change:objectChanges) {
				GameObject go = getGameObject(change.getId());
				if (!objectsThatHaveChanged.contains(go)) {
					objectsThatHaveChanged.add(go);
					go.rollback();
				}
			}
			objectChanges.clear();
		}
	}
	/**
	 * @return			A list of GameObjectChange objects required to make this game data object look exactly like the other
	 */
	public ArrayList<GameObjectChange> buildChanges(GameData other) {
		ArrayList<GameObjectChange> changes = new ArrayList<>();
		
//		long maxid = getMaxId();
		
		// Add new objects first (do separately from attribute builds in case they reference each other!)
		ArrayList<GameObject> newObjects = new ArrayList<>();
		for (GameObject otherGo : other.gameObjects) {
			if (!gameObjectIDHash.containsKey(Long.valueOf(otherGo.getId()))) {
//				if (otherGo.getId()<maxid) {
//					throw new IllegalStateException("This is not good");
//				}
				newObjects.add(otherGo);
				changes.add(new GameObjectCreationChange(otherGo));
			}
		}
		// Now we can get the builds
		for (GameObject otherGo : newObjects) {
			changes.addAll(otherGo.buildChanges());
		}
		
		// Finally
		for (GameObject go : gameObjects) {
			GameObject otherGo = other.getGameObject(go.getId());
			if (otherGo!=null) {
				changes.addAll(go.buildChanges(otherGo));
			}
			else {
				changes.add(new GameObjectDeletionChange(go));
//				throw new IllegalStateException("null object in other for id="+go.getId());
			}
		}
		
		return changes;
	}
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	public String toIdentifier() {
		return dataName+"["+dataid+"]";
	}
	public String getChangeInformation() {
		StringBuffer sb = new StringBuffer();
		sb.append(dataName);
		sb.append("<");
		sb.append(getRelativeSize());
		sb.append(">");
		sb.append("  ");
		sb.append("TrackChanges=");
		sb.append(tracksChanges?"T":"F");
		sb.append("  ");
		sb.append("Changes=");
		sb.append(getChangeCount());
		return sb.toString();
	}
	public int getRelativeSize() {
		int count = 0;
		ArrayList<GameObject> list = new ArrayList<>(gameObjects);
		for (GameObject go : list) {
			count+=go.getRelativeSize();
		}
		return count;
	}
}