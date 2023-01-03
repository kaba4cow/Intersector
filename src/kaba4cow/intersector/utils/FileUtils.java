package kaba4cow.intersector.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.opengl.Display;

import kaba4cow.engine.renderEngine.postProcessing.effects.BlendMode;
import kaba4cow.engine.renderEngine.postProcessing.effects.BloomEffect;
import kaba4cow.engine.renderEngine.postProcessing.effects.CRTEffect;
import kaba4cow.engine.renderEngine.postProcessing.effects.ContrastEffect;
import kaba4cow.engine.renderEngine.postProcessing.effects.DownScaleEffect;
import kaba4cow.engine.renderEngine.postProcessing.effects.GammaCorrection;
import kaba4cow.engine.renderEngine.postProcessing.effects.NoiseEffect;
import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.renderEngine.postProcessing.effects.PosterizeEffect;
import kaba4cow.engine.renderEngine.postProcessing.effects.filters.ExposureFilter;
import kaba4cow.engine.renderEngine.postProcessing.effects.filters.SaturationFilter;
import kaba4cow.engine.renderEngine.postProcessing.effects.filters.SharpnessFilter;
import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.utils.ProgramUtils;
import kaba4cow.files.ContainerFile;
import kaba4cow.files.ContainerGroupFile;
import kaba4cow.files.FractionFile;
import kaba4cow.files.InfosFile;
import kaba4cow.files.MachineFile;
import kaba4cow.files.ManufacturerFile;
import kaba4cow.files.ModelTextureFile;
import kaba4cow.files.ParticleSystemFile;
import kaba4cow.files.ParticleTextureFile;
import kaba4cow.files.PlanetFile;
import kaba4cow.files.ProjectileFile;
import kaba4cow.files.SystemFile;
import kaba4cow.files.TextureSetFile;
import kaba4cow.files.ThrustTextureFile;
import kaba4cow.files.WeaponFile;
import kaba4cow.intersector.Intersector;

public final class FileUtils {

	private FileUtils() {

	}

	public static void loadGameFiles() {
		Printer.println("LOADING GAME FILES");
		List<String> list = new ArrayList<String>();

		ManufacturerFile.load(loadFiles(new File("resources/files/manufacturers/"), null, list));
		list.clear();

		SystemFile.load(loadFiles(new File("resources/files/systems/"), null, list));
		list.clear();

		PlanetFile.load(loadFiles(new File("resources/files/planets/"), null, list));
		list.clear();

		ContainerGroupFile.load(loadFiles(new File("resources/files/containergroups/"), null, list));
		list.clear();

		ThrustTextureFile.load(loadFiles(new File("resources/files/thrusttextures/"), null, list));
		list.clear();

		ModelTextureFile.load(loadFiles(new File("resources/files/modeltextures/"), null, list));
		list.clear();

		ParticleTextureFile.load(loadFiles(new File("resources/files/particletextures/"), null, list));
		list.clear();

		ParticleSystemFile.load(loadFiles(new File("resources/files/particlesystems/"), null, list));
		list.clear();

		TextureSetFile.load(loadFiles(new File("resources/files/texturesets/"), null, list));
		list.clear();

		ContainerFile.load(loadFiles(new File("resources/files/containers/"), null, list));
		list.clear();

		ProjectileFile.load(loadFiles(new File("resources/files/projectiles/"), null, list));
		list.clear();

		WeaponFile.load(loadFiles(new File("resources/files/weapons/"), null, list));
		list.clear();

		MachineFile.load(loadFiles(new File("resources/files/machines/"), null, list));
		list.clear();

		FractionFile.load(loadFiles(new File("resources/files/fractions/"), null, list));
		list.clear();
	}

	public static List<String> loadFiles(File directory, File file, List<String> list) {
		if (file == null)
			file = directory;
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++)
				loadFiles(directory, children[i], list);
		} else {
			String fileName = file.getName();
			File current = file;
			while (true) {
				if (!current.getParentFile().equals(directory)) {
					fileName = current.getParentFile().getName() + "/" + fileName;
					current = current.getParentFile();
				} else
					break;
			}
			list.add(fileName);
		}
		Collections.sort(list);
		return list;
	}

	public static void saveLog() {
		Printer.println("SAVING LOG FILE");
		File directory = new File("logs");
		if (!directory.exists())
			directory.mkdirs();

		File file = new File("logs/log" + Intersector.GAME_VERSION + "_" + ProgramUtils.getDate() + ".txt");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				return;
			}

		try {
			PrintWriter print = new PrintWriter(file);
			String[] history = Printer.getHistory();
			for (int i = 0; i < history.length; i++)
				print.println(history[i]);
			print.close();
		} catch (FileNotFoundException e) {
			return;
		}
	}

	public static List<PostProcessingEffect> readPostProcessingPipeline() {
		final int width = Display.getWidth();
		final int height = Display.getHeight();
		List<PostProcessingEffect> list = new ArrayList<PostProcessingEffect>();

		DataFile data = InfosFile.postprocessing.data().node("effects");
		int effects = data.objectSize();
		for (int i = 0; i < effects; i++) {
			DataFile node = data.node(i);
			String name = node.getString(0);
			if (name.equalsIgnoreCase("DOWNSCALE")) {
				int divisor = node.getInt(1);
				DownScaleEffect downscale = new DownScaleEffect(width, height, divisor);
				list.add(downscale);
			} else if (name.equalsIgnoreCase("BLOOM")) {
				int divisor = node.getInt(1);
				float filtering = node.getFloat(2);
				float intensity = node.getFloat(3);
				BloomEffect bloom = new BloomEffect(width, height, divisor);
				bloom.setBrightFiltering(filtering);
				bloom.setIntensity(intensity);
				list.add(bloom);
			} else if (name.equalsIgnoreCase("CRT")) {
				float curvature = node.getFloat(1);
				float frequency = node.getFloat(2);
				float delta = node.getFloat(3);
				CRTEffect crt = new CRTEffect(width, height);
				crt.setCurvature(curvature);
				crt.setRayFrequency(frequency);
				crt.setDeltaTime(delta);
				list.add(crt);
			} else if (name.equalsIgnoreCase("POSTERIZE")) {
				int levels = node.getInt(1);
				PosterizeEffect posterize = new PosterizeEffect(width, height, levels);
				list.add(posterize);
			} else if (name.equalsIgnoreCase("NOISE")) {
				float power = node.getFloat(1);
				NoiseEffect noise = new NoiseEffect(width, height);
				noise.setPower(power);
				list.add(noise);
			} else if (name.equalsIgnoreCase("CONTRAST")) {
				float power = node.getFloat(1);
				ContrastEffect contrast = new ContrastEffect(width, height);
				contrast.setContrast(power);
				list.add(contrast);
			} else if (name.equalsIgnoreCase("SHARPNESS")) {
				float power = node.getFloat(1);
				SharpnessFilter sharpness = new SharpnessFilter(width, height);
				sharpness.setSharpness(power);
				list.add(sharpness);
			} else if (name.equalsIgnoreCase("SATURATION")) {
				float r = node.getFloat(1);
				float g = node.getFloat(2);
				float b = node.getFloat(3);
				SaturationFilter saturation = new SaturationFilter(width, height);
				saturation.setSaturation(r, g, b);
				list.add(saturation);
			} else if (name.equalsIgnoreCase("EXPOSURE")) {
				float power = node.getFloat(1);
				ExposureFilter exposure = new ExposureFilter(width, height);
				exposure.setExposure(power);
				list.add(exposure);
			} else if (name.equalsIgnoreCase("BLEND")) {
				String mode = node.getString(1);
				float blendFactor = node.getFloat(2);
				BlendMode blend = new BlendMode(width, height, mode);
				blend.setBlendFactor(blendFactor);
				list.add(blend);
			} else if (name.equalsIgnoreCase("GAMMA")) {
				float power = node.getFloat(1);
				GammaCorrection gamma = new GammaCorrection(width, height);
				gamma.setCorrection(power);
				list.add(gamma);
			}
		}

		return list;
	}

	public static class MachineSizeComparator implements Comparator<MachineFile> {

		public static final MachineSizeComparator instance = new MachineSizeComparator();

		@Override
		public int compare(MachineFile o1, MachineFile o2) {
			return Float.compare(o1.getSize(), o2.getSize());
		}

	}

	public static class MachineHealthComparator implements Comparator<MachineFile> {

		public static final MachineHealthComparator instance = new MachineHealthComparator();

		@Override
		public int compare(MachineFile o1, MachineFile o2) {
			return Float.compare(o1.getHealth(), o2.getHealth());
		}

	}

}
