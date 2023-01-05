package kaba4cow.engine.toolbox;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.toolbox.rng.RandomLehmer;

public class ColorRamp {

	public static int MIN_NUM = 2;
	public static int MAX_NUM = 16;

	private List<Element> elements = new ArrayList<Element>();

	public ColorRamp() {
		this.elements = new ArrayList<Element>();
	}

	public static ColorRamp generate(int num, Vector3f avgColor,
			Vector3f[] colors, float minAvgColorBlend, float maxAvgColorBlend,
			float positionFactor, float minBlendPower, float maxBlendPower,
			boolean invertPosition, long seed) {
		ColorRamp colorRamp = new ColorRamp();

		RNG rng = new RandomLehmer(seed);
		num = Maths.limit(num, MIN_NUM, MAX_NUM);
		float maxDist = 1f / (float) num;
		float position = 0f;
		float blendPower = 0f;
		float avgColorBlend = rng.nextFloat(minAvgColorBlend, maxAvgColorBlend);
		Vector3f currentColor = new Vector3f();
		Element element = null;
		for (int i = 0; i < num; i++) {
			currentColor.set(rng.nextFloat(0f, 1f), rng.nextFloat(0f, 1f),
					rng.nextFloat(0f, 1f));
			Maths.blend(currentColor, colors[rng.nextInt(1, colors.length)],
					rng.nextFloat(0.1f, 0.4f), currentColor);
			Maths.blend(avgColor, currentColor, avgColorBlend, currentColor);

			position = Maths.limit(position + maxDist
					* rng.nextFloat(0.5f, 1.5f));
			blendPower = rng.nextFloat(minBlendPower, maxBlendPower);
			element = new Element(currentColor, position, blendPower,
					positionFactor, invertPosition);
			position = element.position;
			colorRamp.addElement(element);
		}

		return colorRamp;
	}

	public Vector3f getColor(float position, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		if (elements.isEmpty())
			return dest;
		int index = 0;
		for (int i = 0; i < elements.size(); i++)
			if (position >= elements.get(i).position)
				index = i;
		Element element = elements.get(index);
		if (index == 0 && position <= element.position)
			return element.color;
		if (index == elements.size() - 1 && position >= element.position)
			return element.color;
		Element next = elements.get(index + 1);
		float blendFactor = Maths.map(position, element.position,
				next.position, 0f, 1f);
		blendFactor = Maths.pow(blendFactor, element.contrast);
		return Maths.blend(next.color, element.color, blendFactor, dest);
	}

	public ColorRamp addElement(Element element) {
		if (!elements.contains(element))
			elements.add(element);
		return this;
	}

	public Element get(int index) {
		if (index < 0 || index >= size())
			return Element.INVALID;
		return elements.get(index);
	}

	public int size() {
		return elements.size();
	}

	public static class Element {

		public static Element INVALID = new Element(
				new Vector3f(-1f, -1f, -1f), 1f, 1f, 1f, false);

		public Vector3f color;
		public float position;
		public float contrast;

		public Element(Vector3f color, float position, float contrast,
				float positionFactor, boolean invertPosition) {
			this.position = position;
			this.color = new Vector3f(color.x, color.y, color.z);
			this.color.scale(Maths.blend(invertPosition ? 1f - position
					: position, 1f, positionFactor));
			this.contrast = contrast;
		}
	}

}