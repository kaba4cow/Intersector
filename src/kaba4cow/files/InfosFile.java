package kaba4cow.files;

public class InfosFile extends GameFile {

	private static final String LOCATION = "infos/";

	public static final InfosFile postprocessing = new InfosFile("POSTPROCESSING");
	public static final InfosFile fonts = new InfosFile("FONTS");
	public static final InfosFile gui = new InfosFile("GUI");
	public static final InfosFile hud = new InfosFile("HUD");
	public static final InfosFile syllables = new InfosFile("SYLLABLES");
	public static final InfosFile scales = new InfosFile("SCALES");
	public static final InfosFile galaxy = new InfosFile("GALAXY");
	public static final InfosFile holograms = new InfosFile("HOLOGRAMS");

	public InfosFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {

	}

	@Override
	public void preparePostInit() {

	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

}
