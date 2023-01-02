package kaba4cow.engine.toolbox.maths;

public enum Easing {

	LINEAR {

		@Override
		protected float calculate(float x) {
			return x;
		}

	},
	EASE_IN_SINE {

		@Override
		protected float calculate(float x) {
			return 1f - Maths.cos(0.5f * x * Maths.PI);
		}

	},
	EASE_OUT_SINE {

		@Override
		protected float calculate(float x) {
			return Maths.sin(0.5f * x * Maths.PI);
		}

	},
	EASE_IN_OUT_SINE {

		@Override
		protected float calculate(float x) {
			return -0.5f * (Maths.cos(Maths.PI * x) - 1f);
		}

	},
	EASE_IN_QUAD {

		@Override
		protected float calculate(float x) {
			return x * x;
		}

	},
	EASE_OUT_QUAD {

		@Override
		protected float calculate(float x) {
			return 1f - (1f - x) * (1f - x);
		}

	},
	EASE_IN_OUT_QUAD {

		@Override
		protected float calculate(float x) {
			if (x < 0.5f)
				return 2f * x * x;
			return 1f - 0.5f * Maths.pow(-2f * x + 2f, 2f);
		}

	},
	EASE_IN_CUBIC {

		@Override
		protected float calculate(float x) {
			return x * x * x;
		}

	},
	EASE_OUT_CUBIC {

		@Override
		protected float calculate(float x) {
			return 1f - Maths.pow(1f - x, 3f);
		}

	},
	EASE_IN_OUT_CUBIC {

		@Override
		protected float calculate(float x) {
			if (x < 0.5f)
				return 4f * x * x * x;
			return 1f - 0.5f * Maths.pow(-2f * x + 2f, 3f);
		}

	},
	EASE_IN_QUART {

		@Override
		protected float calculate(float x) {
			return x * x * x * x;
		}

	},
	EASE_OUT_QUART {

		@Override
		protected float calculate(float x) {
			return 1f - Maths.pow(1f - x, 4f);
		}

	},
	EASE_IN_OUT_QUART {

		@Override
		protected float calculate(float x) {
			if (x < 0.5f)
				return 8f * x * x * x * x;
			return 1f - 0.5f * Maths.pow(-2f * x + 2f, 4f);
		}

	},
	EASE_IN_QUINT {

		@Override
		protected float calculate(float x) {
			return x * x * x * x * x;
		}

	},
	EASE_OUT_QUINT {

		@Override
		protected float calculate(float x) {
			return 1f - Maths.pow(1f - x, 5f);
		}

	},
	EASE_IN_OUT_QUINT {

		@Override
		protected float calculate(float x) {
			if (x < 0.5f)
				return 16f * x * x * x * x * x;
			return 1f - 0.5f * Maths.pow(-2f * x + 2f, 5f);
		}

	},
	EASE_IN_EXPO {

		@Override
		protected float calculate(float x) {
			if (x == 0f)
				return 0f;
			return Maths.pow(2f, 10f * x - 10f);
		}

	},
	EASE_OUT_EXPO {

		@Override
		protected float calculate(float x) {
			if (x == 1f)
				return 1f;
			return 1f - Maths.pow(2f, -10f * x);
		}

	},
	EASE_IN_OUT_EXPO {

		@Override
		protected float calculate(float x) {
			if (x == 0f)
				return 0f;
			if (x == 1f)
				return 1f;
			if (x < 0.5f)
				return 0.5f * Maths.pow(2f, 20f * x - 10f);
			return 0.5f * (2f - Maths.pow(2f, -20f * x + 10f));
		}

	},
	EASE_IN_CIRC {

		@Override
		protected float calculate(float x) {
			return 1f - Maths.sqrt(1f - Maths.pow(x, 2f));
		}

	},
	EASE_OUT_CIRC {

		@Override
		protected float calculate(float x) {
			return Maths.sqrt(1f - Maths.pow(x - 1f, 2f));
		}

	},
	EASE_IN_OUT_CIRC {

		@Override
		protected float calculate(float x) {
			if (x < 0.5f)
				return 0.5f * (1f - Maths.sqrt(1f - Maths.pow(2f * x, 2f)));
			return 0.5f * (1f + Maths.sqrt(1f - Maths.pow(-2f * x + 2f, 2f)));
		}

	},
	EASE_IN_BACK {

		@Override
		protected float calculate(float x) {
			return c3 * x * x * x - c1 * x * x;
		}

	},
	EASE_OUT_BACK {

		@Override
		protected float calculate(float x) {
			float c3 = c1 + 1f;
			return 1f + c3 * Maths.pow(x - 1f, 3f) + c1 * Maths.pow(x - 1f, 2f);
		}

	},
	EASE_IN_OUT_BACK {

		@Override
		protected float calculate(float x) {
			if (x < 0.5f)
				return 0.5f * (Maths.pow(2f * x, 2f) * ((c2 + 1f) * 2f * x - c2));
			return 0.5f * (Maths.pow(2f * x - 2f, 2f)
					* ((c2 + 1f) * (x * 2f - 2f) + c2) + 2f);
		}

	},
	EASE_IN_ELASTIC {

		@Override
		protected float calculate(float x) {
			if (x == 0f)
				return 0f;
			if (x == 1f)
				return 1f;
			return -Maths.pow(2f, 10f * x - 10f)
					* Maths.sin((10f * x - 10.75f) * c4);
		}

	},
	EASE_OUT_ELASTIC {

		@Override
		protected float calculate(float x) {
			if (x == 0f)
				return 0f;
			if (x == 1f)
				return 1f;
			return Maths.pow(2f, -10f * x) * Maths.sin((10f * x - 0.75f) * c4)
					+ 1f;
		}

	},
	EASE_IN_OUT_ELASTIC {

		@Override
		protected float calculate(float x) {
			if (x == 0f)
				return 0f;
			if (x == 1f)
				return 1f;
			if (x < 0.5f)
				return -0.5f * Maths.pow(2f, 20f * x - 10f)
						* Maths.sin((20f * x - 11.125f) * c5);
			return 0.5f * Maths.pow(2f, -20f * x + 10f)
					* Maths.sin((20f * x - 11.125f) * c5) + 1f;
		}

	},
	EASE_IN_BOUNCE {

		@Override
		protected float calculate(float x) {
			return 1f - EASE_OUT_BOUNCE.calculate(1f - x);
		}

	},
	EASE_OUT_BOUNCE {

		@Override
		protected float calculate(float x) {
			if (x < 1f / d1)
				return n1 * x * x;
			if (x < 2f / d1)
				return n1 * (x -= 1.5f / d1) * x + 0.75f;
			if (x < 2.5f / d1)
				return n1 * (x -= 2.25f / d1) * x + 0.9375f;
			return n1 * (x -= 2.625f / d1) * x + 0.984375f;
		}

	},
	EASE_IN_OUT_BOUNCE {

		@Override
		protected float calculate(float x) {
			if (x < 0.5f)
				return 0.5f * (1f - EASE_OUT_BOUNCE.calculate(1f - 2f * x));
			return 0.5f * (1f + EASE_OUT_BOUNCE.calculate(2f * x - 1f));
		}

	};

	private static final float c1 = 1.70158f;
	private static final float c2 = c1 * 1.525f;
	private static final float c3 = c1 + 1f;
	private static final float c4 = Maths.TWO_PI * Maths.DIV3;
	private static final float c5 = Maths.TWO_PI / 4.5f;
	private static final float n1 = 7.5625f;
	private static final float d1 = 2.75f;

	public float getValue(float x) {
		x = clamp(x);
		if (x < 0f)
			x = 1f - x;
		return calculate(x);
	}

	private static float clamp(float x) {
		if (x < -1f)
			return -1f;
		if (x > 1f)
			return 1f;
		return x;
	}

	protected abstract float calculate(float x);

}