package kaba4cow.intersector.renderEngine;

import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.renderers.CubemapRenderer;
import kaba4cow.engine.renderEngine.renderers.GUIRenderer;
import kaba4cow.engine.renderEngine.renderers.ModelRenderer;
import kaba4cow.engine.renderEngine.renderers.ParticleRenderer;
import kaba4cow.engine.renderEngine.renderers.TextRenderer;
import kaba4cow.intersector.renderEngine.renderers.DebugRenderer;
import kaba4cow.intersector.renderEngine.renderers.HologramRenderer;
import kaba4cow.intersector.renderEngine.renderers.LaserRenderer;
import kaba4cow.intersector.renderEngine.renderers.MachineRenderer;
import kaba4cow.intersector.renderEngine.renderers.ShieldRenderer;
import kaba4cow.intersector.renderEngine.renderers.ThrustRenderer;
import kaba4cow.intersector.renderEngine.renderers.gui.FrameRenderer;
import kaba4cow.intersector.renderEngine.renderers.gui.GUITextRenderer;
import kaba4cow.intersector.renderEngine.renderers.menu.DynamicFrameRenderer;
import kaba4cow.intersector.renderEngine.renderers.menu.SliderFrameRenderer;
import kaba4cow.intersector.renderEngine.renderers.menu.StaticFrameRenderer;
import kaba4cow.intersector.renderEngine.renderers.planets.FlareRenderer;
import kaba4cow.intersector.renderEngine.renderers.planets.RingRenderer;
import kaba4cow.intersector.renderEngine.renderers.planets.TerrainRenderer;

public class RendererContainer {

	private Renderer renderer;

	private final ModelRenderer modelRenderer;
	private final MachineRenderer machineRenderer;
	private final TerrainRenderer terrainRenderer;
	private final RingRenderer ringRenderer;
	private final FlareRenderer flareRenderer;
	private final LaserRenderer laserRenderer;
	private final ThrustRenderer thrustRenderer;
	private final ShieldRenderer shieldRenderer;
	private final HologramRenderer hologramRenderer;
	private final DebugRenderer debugRenderer;
	private final CubemapRenderer cubemapRenderer;
	private final ParticleRenderer particleRenderer;
	private final FrameRenderer frameRenderer;
	private final GUITextRenderer guiTextRenderer;
	private final StaticFrameRenderer staticFrameRenderer;
	private final DynamicFrameRenderer dynamicFrameRenderer;
	private final SliderFrameRenderer sliderFrameRenderer;
	private final GUIRenderer guiRenderer;
	private final TextRenderer textRenderer;

	public RendererContainer(Renderer renderer) {
		this.renderer = renderer;
		this.modelRenderer = new ModelRenderer(renderer);
		this.machineRenderer = new MachineRenderer(renderer);
		this.cubemapRenderer = new CubemapRenderer(renderer);
		this.particleRenderer = new ParticleRenderer(renderer);
		this.terrainRenderer = new TerrainRenderer(renderer);
		this.ringRenderer = new RingRenderer(renderer);
		this.flareRenderer = new FlareRenderer(renderer);
		this.laserRenderer = new LaserRenderer(renderer);
		this.thrustRenderer = new ThrustRenderer(renderer);
		this.shieldRenderer = new ShieldRenderer(renderer);
		this.hologramRenderer = new HologramRenderer(renderer);
		this.debugRenderer = new DebugRenderer(renderer);
		this.frameRenderer = new FrameRenderer(renderer);
		this.guiTextRenderer = new GUITextRenderer(renderer);
		this.staticFrameRenderer = new StaticFrameRenderer(renderer);
		this.dynamicFrameRenderer = new DynamicFrameRenderer(renderer);
		this.sliderFrameRenderer = new SliderFrameRenderer(renderer);
		this.guiRenderer = new GUIRenderer(renderer);
		this.textRenderer = new TextRenderer(renderer);
	}

	public void processModelRenderers(Cubemap cubemap) {
		modelRenderer.setCubemap(cubemap);
		modelRenderer.process();
		machineRenderer.setCubemap(cubemap);
		machineRenderer.process();
		terrainRenderer.process();
		ringRenderer.process();
		flareRenderer.process();
		laserRenderer.process();
		thrustRenderer.process();
		shieldRenderer.process();
		hologramRenderer.process();
		debugRenderer.process();
		frameRenderer.process();
		guiTextRenderer.process();
	}

	public void processGUIRenderers() {
		staticFrameRenderer.process();
		dynamicFrameRenderer.process();
		sliderFrameRenderer.process();
		guiRenderer.process();
		textRenderer.process();
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		if (this.renderer == renderer)
			return;
		this.renderer = renderer;
		modelRenderer.setRenderer(renderer);
		machineRenderer.setRenderer(renderer);
		cubemapRenderer.setRenderer(renderer);
		particleRenderer.setRenderer(renderer);
		terrainRenderer.setRenderer(renderer);
		ringRenderer.setRenderer(renderer);
		flareRenderer.setRenderer(renderer);
		laserRenderer.setRenderer(renderer);
		thrustRenderer.setRenderer(renderer);
		shieldRenderer.setRenderer(renderer);
		hologramRenderer.setRenderer(renderer);
		debugRenderer.setRenderer(renderer);
		frameRenderer.setRenderer(renderer);
		guiTextRenderer.setRenderer(renderer);
		staticFrameRenderer.setRenderer(renderer);
		dynamicFrameRenderer.setRenderer(renderer);
		sliderFrameRenderer.setRenderer(renderer);
		guiRenderer.setRenderer(renderer);
		textRenderer.setRenderer(renderer);
	}

	public ModelRenderer getModelRenderer() {
		return modelRenderer;
	}

	public MachineRenderer getMachineRenderer() {
		return machineRenderer;
	}

	public TerrainRenderer getTerrainRenderer() {
		return terrainRenderer;
	}

	public RingRenderer getRingRenderer() {
		return ringRenderer;
	}

	public ParticleRenderer getParticleRenderer() {
		return particleRenderer;
	}

	public CubemapRenderer getCubemapRenderer() {
		return cubemapRenderer;
	}

	public FlareRenderer getFlareRenderer() {
		return flareRenderer;
	}

	public LaserRenderer getLaserRenderer() {
		return laserRenderer;
	}

	public ThrustRenderer getThrustRenderer() {
		return thrustRenderer;
	}

	public ShieldRenderer getShieldRenderer() {
		return shieldRenderer;
	}

	public HologramRenderer getHologramRenderer() {
		return hologramRenderer;
	}

	public DebugRenderer getDebugRenderer() {
		return debugRenderer;
	}

	public TextRenderer getTextRenderer() {
		return textRenderer;
	}

	public GUIRenderer getGuiRenderer() {
		return guiRenderer;
	}

	public FrameRenderer getFrameRenderer() {
		return frameRenderer;
	}

	public GUITextRenderer getGuiTextRenderer() {
		return guiTextRenderer;
	}

	public StaticFrameRenderer getStaticFrameRenderer() {
		return staticFrameRenderer;
	}

	public DynamicFrameRenderer getDynamicFrameRenderer() {
		return dynamicFrameRenderer;
	}

	public SliderFrameRenderer getSliderFrameRenderer() {
		return sliderFrameRenderer;
	}

}
