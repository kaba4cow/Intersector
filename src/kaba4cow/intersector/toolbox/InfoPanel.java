package kaba4cow.intersector.toolbox;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import kaba4cow.engine.Input;
import kaba4cow.engine.audio.AudioManager;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.renderEngine.postProcessing.FrameBufferObject;
import kaba4cow.engine.renderEngine.renderers.TextRenderer;
import kaba4cow.engine.toolbox.Loaders;
import kaba4cow.engine.toolbox.MemoryAnalyzer;
import kaba4cow.intersector.Intersector;
import kaba4cow.intersector.renderEngine.fborendering.RingRendering;
import kaba4cow.intersector.renderEngine.fborendering.SkyRendering;
import kaba4cow.intersector.renderEngine.fborendering.TerrainRendering;

public class InfoPanel {
	
	private final GUIText[][] infos;

	private int mode;

	public InfoPanel() {
		String font = FontContainer.get("system");
		
		this.infos = new GUIText[3][];
		this.infos[0] = new GUIText[2];
		this.infos[0][0] = new GUIText("", font, new Vector2f(-1f, 1f), 1f, 1f, false); // fps
		this.infos[0][1] = new GUIText("", font, new Vector2f(-1f, 0.95f), 1f, 1f, false); // memory

		this.infos[1] = new GUIText[3];
		this.infos[1][0] = new GUIText("", font, new Vector2f(-1f, 1f), 1f, 1f, false); // sky
		this.infos[1][1] = new GUIText("", font, new Vector2f(-1f, 0.95f), 1f, 1f, false); // terrain
		this.infos[1][2] = new GUIText("", font, new Vector2f(-1f, 0.9f), 1f, 1f, false); // ring

		this.infos[2] = new GUIText[6];
		this.infos[2][0] = new GUIText("", font, new Vector2f(-1f, 1f), 1f, 1f, false); // fbos
		this.infos[2][1] = new GUIText("", font, new Vector2f(-1f, 0.95f), 1f, 1f, false); // vaos
		this.infos[2][2] = new GUIText("", font, new Vector2f(-1f, 0.9f), 1f, 1f, false); // textures
		this.infos[2][3] = new GUIText("", font, new Vector2f(-1f, 0.85f), 1f, 1f, false); // shaders
		this.infos[2][4] = new GUIText("", font, new Vector2f(-1f, 0.8f), 1f, 1f, false); // sources
		this.infos[2][5] = new GUIText("", font, new Vector2f(-1f, 0.75f), 1f, 1f, false); // buffers

		this.mode = -1;
	}

	public void render(TextRenderer renderer) {
		if (Input.isKeyDown(Keyboard.KEY_F2))
			mode++;
		if (mode >= infos.length)
			mode = -1;
		if (mode == -1)
			return;

		infos[0][0].setTextString("FPS: " + Intersector.getCurrentFPS());
		infos[0][1].setTextString("MEMORY: "
				+ (MemoryAnalyzer.getCurrentUsage() / 1024l) + " KB");

		infos[1][0].setTextString("SKY: " + SkyRendering.size());
		infos[1][1].setTextString("TERRAIN: " + TerrainRendering.size());
		infos[1][2].setTextString("RING: " + RingRendering.size());
		
		infos[2][0].setTextString("FBOS: " + FrameBufferObject.fbos());
		infos[2][1].setTextString("VAOS: " + Loaders.vaos());
		infos[2][2].setTextString("TEXTURES: " + Loaders.textures());
		infos[2][3].setTextString("SHADERS: " + Loaders.shaders());
		infos[2][4].setTextString("SOURCES: " + AudioManager.sources());
		infos[2][5].setTextString("BUFFERS: " + AudioManager.buffers());

		for (int i = 0; i < infos[mode].length; i++)
			renderer.render(infos[mode][i]);
		renderer.process();
	}

}
