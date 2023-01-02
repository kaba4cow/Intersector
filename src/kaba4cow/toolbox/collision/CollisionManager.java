package kaba4cow.toolbox.collision;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.GameSettings;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.gameobjects.GameObject;
import kaba4cow.gameobjects.objectcomponents.ColliderComponent;

public class CollisionManager {

	private static ColliderComponent[] tempColliders1;
	private static ColliderComponent[] tempColliders2;

	private static Matrix4f tempObjMatrix1 = new Matrix4f();
	private static Matrix4f tempObjMatrix2 = new Matrix4f();
	private static Matrix4f tempMat1 = new Matrix4f();
	private static Matrix4f tempMat2 = new Matrix4f();

	private static Vector3f tempColliderPos1 = new Vector3f();
	private static Vector3f tempColliderPos2 = new Vector3f();
	private static Vector3f tempColliderScale1 = new Vector3f();
	private static Vector3f tempColliderScale2 = new Vector3f();
	private static Vector3f tempResponseVel = new Vector3f();
	private static Vector3f tempCurrentVel = new Vector3f();
	private static Vector3f tempPos = new Vector3f();
	private static Vector3f tempVel = new Vector3f();

	private static float tempSize1;
	private static float tempSize2;
	private static float tempSizeRatio;
	private static float tempRadius;
	private static float tempDistSq;
	private static float tempStep;
	private static float tempCollidersScale1;
	private static float tempCollidersScale2;

	public static void collide(ColliderHolder obj1, ColliderHolder obj2, float dt) {
		tempPos.set(obj1.getPos());
		tempVel.set(obj1.getVel());
		tempSize1 = obj1.getSize();
		tempSize2 = obj2.getSize();
		Matrices.set(tempObjMatrix1, obj1.getDirection().getMatrix(tempPos, true, tempSize1));
		Matrices.set(tempObjMatrix2, obj2.getDirection().getMatrix(obj2.getPos(), true, tempSize2));
		tempColliders1 = obj1.getColliders();
		tempColliders2 = obj2.getColliders();
		tempResponseVel.set(0f, 0f, 0f);
		tempCurrentVel.set(0f, 0f, 0f);
		tempSizeRatio = tempSize2 / tempSize1;
		tempCollidersScale1 = obj1.getCollidersScale();
		tempCollidersScale2 = obj2.getCollidersScale();
		for (int i = 0; i < tempColliders1.length; i++) {
			Vectors.set(tempColliderScale1, tempColliders1[i].size * tempCollidersScale1);
			tempMat1.setIdentity();
			tempMat1.translate(tempColliders1[i].pos.negate(null));
			tempMat1.scale(tempColliderScale1);
			Matrix4f.mul(tempObjMatrix1, tempMat1, tempMat1);
			Matrices.getTranslation(tempMat1, tempColliderPos1);
			for (int j = 0; j < tempColliders2.length; j++) {
				Vectors.set(tempColliderScale2, tempColliders2[j].size * tempCollidersScale2);
				tempMat2.setIdentity();
				tempMat2.translate(tempColliders2[j].pos.negate(null));
				tempMat2.scale(tempColliderScale2);
				Matrix4f.mul(tempObjMatrix2, tempMat2, tempMat2);
				Matrices.getTranslation(tempMat2, tempColliderPos2);

				tempRadius = tempSize1 * tempColliders1[i].size * tempCollidersScale1
						+ tempSize2 * tempColliders2[j].size * tempCollidersScale2;
				tempDistSq = Maths.distSq(tempColliderPos1, tempColliderPos2) - tempRadius * tempRadius;
				if (tempDistSq <= 0f) {
					Vector3f.sub(tempColliderPos2, tempColliderPos1, tempCurrentVel);
					Vector3f.add(tempResponseVel, tempCurrentVel, tempResponseVel);
				}
			}
		}
		tempStep = -GameObject.getMassDivider(obj2.getMass()) * tempSizeRatio / (float) GameSettings.getCollisions();
		Vectors.addScaled(obj2.getPos(), tempResponseVel, tempStep, obj2.getPos());
		Vectors.addScaled(obj2.getVel(), tempResponseVel, tempStep, obj2.getVel());
		tempStep = GameObject.getMassDivider(obj1.getMass()) * tempSizeRatio / (float) GameSettings.getCollisions();
		Vectors.addScaled(obj1.getPos(), tempResponseVel, tempStep, obj1.getPos());
		Vectors.addScaled(obj1.getVel(), tempResponseVel, tempStep, obj1.getVel());
	}

}
