package kaba4cow.engine.toolbox.maths;

import kaba4cow.engine.toolbox.rng.RNG;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class Maths {

	public static final float PI = (float) Math.PI;
	public static final float TWO_PI = 2.0f * PI;
	public static final float HALF_PI = 0.5f * PI;
	public static final float QUARTER_PI = 0.25f * PI;

	public static final float SQRT2 = sqrt(2f);

	public static final float DIV3 = 1f / 3f;
	public static final float DIV255 = 1f / 255f;

	private static final float DEG_TO_RAD = 0.017453292519f;
	private static final float RAD_TO_DEG = 57.295779513082f;

	public static int nextPowerOf2(int a) {
		int b = 1;
		while (b < a)
			b = b << 1;
		return b;
	}

	public static boolean equal(float value, float center, float width) {
		return value >= center - width && value < center + width;
	}

	public static float level(float value, float levels) {
		return (int) (value * levels) / levels;
	}

	public static float toRadians(float value) {
		return value * DEG_TO_RAD;
	}

	public static float toDegrees(float value) {
		return value * RAD_TO_DEG;
	}

	public static int booleanToInt(boolean value) {
		return value ? 1 : 0;
	}

	public static boolean intToBoolean(int value) {
		if (value > 0)
			return true;
		return false;
	}

	public static float toCenter(float x, float power) {
		float value = 2f * x - 1f;
		float sign = Maths.signum(value);
		value = Maths.pow(Maths.abs(value), power);
		value = 0.5f * (1f + sign * value);
		return value;
	}

	public static int getBiasedInt(float x, int start, int end, float bias) {
		int value = (int) Maths.map(bias(x, bias), 0f, 1f, start, end + 1);
		if (value > end)
			value = end;
		return value;
	}

	public static float getBiasedFloat(float x, float start, float end,
			float bias) {
		float value = Maths.map(bias(x, bias), 0f, 1f, start, end);
		if (value > end)
			value = end;
		return value;
	}

	public static float getPitch(Vector3f v) {
		return (float) Math.asin(-v.y);
	}

	public static float getYaw(Vector3f v) {
		return (float) Math.atan2(v.x, v.z);
	}

	public static Vector2f direction(Vector2f start, Vector2f end) {
		Vector2f direction = Vector2f.sub(end, start, null);
		try {
			direction.normalise();
		} catch (IllegalStateException e) {
			direction.x = RNG.randomFloat(-1f, 1f);
			direction.y = RNG.randomFloat(-1f, 1f);
			direction.normalise();
		}
		return direction;
	}

	public static Vector3f direction(Vector3f start, Vector3f end) {
		Vector3f direction = Vector3f.sub(end, start, null);
		try {
			direction.normalise();
		} catch (IllegalStateException e) {
			direction.x = RNG.randomFloat(-1f, 1f);
			direction.y = RNG.randomFloat(-1f, 1f);
			direction.z = RNG.randomFloat(-1f, 1f);
			direction.normalise();
		}
		return direction;
	}

	public static double quickSqrt(double value) {
		double sqrt = Double
				.longBitsToDouble(((Double.doubleToLongBits(value) - (1l << 52)) >> 1)
						+ (1l << 61));
		double better = 0.5d * (sqrt + value / sqrt);
		double better2 = 0.5d * (better + value / better);
		return better2;
	}

	public static int signum(float x) {
		return x >= 0f ? 1 : -1;
	}

	public static int signumZero(float x) {
		if (x > 0f)
			return 1;
		if (x < 0f)
			return -1;
		return 0;
	}

	public static float avg(float... args) {
		float sum = 0f;
		for (float i : args)
			sum += i;
		return sum / (float) args.length;
	}

	public static double avg(double... args) {
		double sum = 0f;
		for (double i : args)
			sum += i;
		return sum / (double) args.length;
	}

	public static int min(int... args) {
		if (args.length < 2)
			return 0;
		int min = Integer.MAX_VALUE;
		for (int i : args) {
			if (i < min)
				min = i;
		}
		return min;
	}

	public static int max(int... args) {
		if (args.length < 2)
			return 0;
		int max = -Integer.MAX_VALUE;
		for (int i : args) {
			if (i > max)
				max = i;
		}
		return max;
	}

	public static float min(float... args) {
		if (args.length < 2)
			return 0;
		float min = Float.MAX_VALUE;
		for (float i : args) {
			if (i < min)
				min = i;
		}
		return min;
	}

	public static float max(float... args) {
		if (args.length < 2)
			return 0;
		float max = -Float.MAX_VALUE;
		for (float i : args) {
			if (i > max)
				max = i;
		}
		return max;
	}

	public static double min(double... args) {
		if (args.length < 2)
			return 0;
		double min = Double.MAX_VALUE;
		for (double i : args) {
			if (i < min)
				min = i;
		}
		return min;
	}

	public static double max(double... args) {
		if (args.length < 2)
			return 0;
		double max = -Double.MAX_VALUE;
		for (double i : args) {
			if (i > max)
				max = i;
		}
		return max;
	}

	public static float round(float x, int n) {
		float d = 1;
		for (int i = 0; i < n; i++)
			d *= 10f;
		return (int) (d * x) / d;
	}

	public static int floor(double x) {
		return (int) Math.floor(x);
	}

	public static int ceil(double x) {
		return (int) Math.ceil(x);
	}

	public static int round(double x) {
		return (int) Math.round(x);
	}

	public static int abs(int x) {
		return Math.abs(x);
	}

	public static float abs(float x) {
		return Math.abs(x);
	}

	public static double abs(double x) {
		return Math.abs(x);
	}

	public static float sin(float x) {
		return (float) Math.sin(x);
	}

	public static float cos(float x) {
		return (float) Math.cos(x);
	}

	public static float tan(float x) {
		return (float) Math.tan(x);
	}

	public static float atan(float x) {
		return (float) Math.atan(x);
	}

	public static float atan2(float x, float y) {
		return (float) Math.atan2(x, y);
	}

	public static float acos(float x) {
		return (float) Math.acos(x);
	}

	public static float asin(float x) {
		return (float) Math.asin(x);
	}

	public static float saw(float x, int sharpness) {
		sharpness = max(sharpness, 1);
		float sum = 0f;
		float sign = -1f;
		for (float i = 1f; i <= sharpness; i++) {
			sign *= -1f;
			sum += sign * sin(i * x) / i;
		}
		return sum;
	}

	public static float tri(float x) {
		return abs(cos(x)) - abs(sin(x));
	}

	public static float exp(float x) {
		return (float) Math.exp(x);
	}

	public static float pow(float x, float pow) {
		return (float) Math.pow(x, pow);
	}

	public static double pow(double x, double pow) {
		return Math.pow(x, pow);
	}

	public static float sqrt(float x) {
		return (float) Math.sqrt(x);
	}

	public static float sqr(float x) {
		return x * x;
	}

	public static float bias(float x, float bias) {
		if (bias < 0f)
			return 1f - bias(x, -bias);
		float k = pow(1f - bias, 3f);
		return (x * k) / (x * k - x + 1f);
	}

	public static double bias(double x, double bias) {
		if (bias < 0d)
			return 1d - bias(x, -bias);
		double k = pow(1d - bias, 3d);
		return (x * k) / (x * k - x + 1d);
	}

	public static float smooth(float x, float y, float k) {
		float t = Maths.limit((k - x) / (y - x), 0f, 1f);
		return t * t * (3f - 2f * t);
	}

	public static double smooth(double x, double y, double k) {
		double t = Maths.limit((k - x) / (y - x), 0f, 1f);
		return t * t * (3f - 2f * t);
	}

	public static float smoothMin(float x, float y, float k) {
		float h = limit((y - x + k) / (2f * k));
		return x * h + y * (1f - h) - k * h * (1f - h);
	}

	public static double smoothMin(double x, double y, double k) {
		double h = limit((y - x + k) / (2f * k));
		return x * h + y * (1f - h) - k * h * (1f - h);
	}

	public static float smoothMax(float x, float y, float k) {
		k *= -1f;
		float h = limit((y - x + k) / (2f * k));
		return x * h + y * (1f - h) - k * h * (1f - h);
	}

	public static double smoothMax(double x, double y, double k) {
		k *= -1d;
		double h = limit((y - x + k) / (2f * k));
		return x * h + y * (1f - h) - k * h * (1f - h);
	}

	public static float limit(float x) {
		if (x <= 0f)
			x = 0f;
		else if (x > 1f)
			x = 1f;
		return x;
	}

	public static double limit(double x) {
		if (x <= 0f)
			x = 0f;
		else if (x > 1f)
			x = 1f;
		return x;
	}

	public static int limit(int x, int min, int max) {
		if (x <= min)
			x = min;
		else if (x > max)
			x = max;
		return x;
	}

	public static float limit(float x, float min, float max) {
		if (x <= min)
			x = min;
		else if (x > max)
			x = max;
		return x;
	}

	public static double limit(double x, double min, double max) {
		if (x <= min)
			x = min;
		else if (x > max)
			x = max;
		return x;
	}

	public static float limitMin(float x, float min) {
		return x <= min ? min : x;
	}

	public static float limitMax(float x, float max) {
		return x > max ? max : x;
	}

	public static double limitMin(double x, double min) {
		return x <= min ? min : x;
	}

	public static double limitMax(double x, double max) {
		return x > max ? max : x;
	}

	public static float map(float x, float start1, float stop1, float start2,
			float stop2) {
		return start2 + (stop2 - start2) * ((x - start1) / (stop1 - start1));
	}

	public static double map(double x, double start1, double stop1,
			double start2, double stop2) {
		return start2 + (stop2 - start2) * ((x - start1) / (stop1 - start1));
	}

	public static float mapBias(float x, float start1, float stop1,
			float start2, float stop2, float bias) {
		float value = map(x, start1, stop1, 0f, 1f);
		float biased = bias(value, bias);
		return map(biased, 0f, 1f, start2, stop2);
	}

	public static double mapBias(double x, double start1, double stop1,
			double start2, double stop2, double bias) {
		double value = map(x, start1, stop1, 0f, 1f);
		double biased = bias(value, bias);
		return map(biased, 0d, 1d, start2, stop2);
	}

	public static float mapLimit(float x, float start1, float stop1,
			float start2, float stop2) {
		float result = map(x, start1, stop1, start2, stop2);
		float min = min(start2, stop2);
		float max = max(start2, stop2);
		return limit(result, min, max);
	}

	public static double mapLimit(double x, double start1, double stop1,
			double start2, double stop2) {
		double result = map(x, start1, stop1, start2, stop2);
		double min = min(start2, stop2);
		double max = max(start2, stop2);
		return limit(result, min, max);
	}

	public static float norm(float x, float start1, float stop1) {
		return (x - start1) / (stop1 - start1);
	}

	public static double norm(double x, double start1, double stop1) {
		return (x - start1) / (stop1 - start1);
	}

	public static int wrap(int x, int min, int max) {
		if (x >= min && x < max)
			return x;
		int length = dist(min, max);
		if (x >= max)
			x = min + x % length;
		else if (x < min)
			x = max - abs(x) % length;
		return x;
	}

	public static float wrap(float x, float min, float max) {
		if (x >= min && x < max)
			return x;
		float length = dist(min, max);
		if (x >= max)
			x = min + x % length;
		else if (x < min)
			x = max - abs(x) % length;
		return x;
	}

	public static double wrap(double x, double min, double max) {
		if (x >= min && x < max)
			return x;
		double length = dist(min, max);
		if (x >= max)
			x = min + x % length;
		else if (x < min)
			x = max - abs(x) % length;
		return x;
	}

	public static float blend(float x, float y, float blendFactor) {
		blendFactor = limit(blendFactor);
		return x * blendFactor + y * (1.0f - blendFactor);
	}

	public static double blend(double x, double y, double blendFactor) {
		blendFactor = limit(blendFactor);
		return x * blendFactor + y * (1.0f - blendFactor);
	}

	public static Vector3f blend(Vector3f left, Vector3f right,
			float blendFactor, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		float x = blend(left.x, right.x, blendFactor);
		float y = blend(left.y, right.y, blendFactor);
		float z = blend(left.z, right.z, blendFactor);
		dest.x = x;
		dest.y = y;
		dest.z = z;
		return dest;
	}

	public static Vector2f blend(Vector2f left, Vector2f right,
			float blendFactor, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		float x = blend(left.x, right.x, blendFactor);
		float y = blend(left.y, right.y, blendFactor);
		dest.x = x;
		dest.y = y;
		return dest;
	}

	public static int manhattan(int x0, int y0, int x1, int y1) {
		return abs(x1 - x0) + abs(y1 - y0);
	}

	public static int manhattan(int x0, int y0, int z0, int x1, int y1, int z1) {
		return abs(x1 - x0) + abs(y1 - y0) + abs(z1 - z0);
	}

	public static float manhattan(float x0, float y0, float x1, float y1) {
		return abs(x1 - x0) + abs(y1 - y0);
	}

	public static float manhattan(float x0, float y0, float z0, float x1,
			float y1, float z1) {
		return abs(x1 - x0) + abs(y1 - y0) + abs(z1 - z0);
	}

	public static float dist(float x0, float y0, float x1, float y1) {
		float dist = distSq(x0, y0, x1, y1);
		return sqrt(dist);
	}

	public static float dist(float x0, float y0, float z0, float x1, float y1,
			float z1) {
		float dist = distSq(x0, y0, z0, x1, y1, z1);
		return sqrt(dist);
	}

	public static float dist(float x0, float y0, float z0, float w0, float x1,
			float y1, float z1, float w1) {
		float dist = distSq(x0, y0, z0, w0, x1, y1, z1, w1);
		return sqrt(dist);
	}

	public static float distSq(float x0, float y0, float x1, float y1) {
		float distX = (x0 - x1) * (x0 - x1);
		float distY = (y0 - y1) * (y0 - y1);
		float dist = abs(distX + distY);
		return dist;
	}

	public static float distSq(float x0, float y0, float z0, float x1,
			float y1, float z1) {
		float distX = (x0 - x1) * (x0 - x1);
		float distY = (y0 - y1) * (y0 - y1);
		float distZ = (z0 - z1) * (z0 - z1);
		float dist = abs(distX + distY + distZ);
		return dist;
	}

	public static float distSq(float x0, float y0, float z0, float w0,
			float x1, float y1, float z1, float w1) {
		float distX = (x0 - x1) * (x0 - x1);
		float distY = (y0 - y1) * (y0 - y1);
		float distZ = (z0 - z1) * (z0 - z1);
		float distW = (w0 - w1) * (w0 - w1);
		float dist = abs(distX + distY + distZ + distW);
		return dist;
	}

	public static double dist(double a, double b) {
		return abs(b - a);
	}

	public static float dist(float a, float b) {
		return abs(b - a);
	}

	public static float distSq(float a, float b) {
		float dist = dist(a, b);
		return dist * dist;
	}

	public static int dist(int a, int b) {
		return abs(b - a);
	}

	public static int distSq(int a, int b) {
		int dist = dist(a, b);
		return dist * dist;
	}

	public static float dist(Vector2f x, Vector2f y) {
		float dist = distSq(x, y);
		return sqrt(dist);
	}

	public static float distSq(Vector2f x, Vector2f y) {
		float distX = (x.x - y.x) * (x.x - y.x);
		float distY = (x.y - y.y) * (x.y - y.y);
		float dist = abs(distX + distY);
		return dist;
	}

	public static float dist(Vector3f x, Vector3f y) {
		float dist = distSq(x, y);
		return sqrt(dist);
	}

	public static float distSq(Vector3f x, Vector3f y) {
		float distX = (x.x - y.x) * (x.x - y.x);
		float distY = (x.y - y.y) * (x.y - y.y);
		float distZ = (x.z - y.z) * (x.z - y.z);
		float dist = abs(distX + distY + distZ);
		return dist;
	}

}
