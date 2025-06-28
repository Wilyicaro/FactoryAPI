package wily.factoryapi.base.client;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix3x2fStack;

public interface FactoryGuiMatrixStack {
	public static FactoryGuiMatrixStack of(PoseStack stack) {
		return null;
	}

	static FactoryGuiMatrixStack of(Matrix3x2fStack stack) {
		return new FactoryGuiMatrixStack() {
			@Override
			public void push() {
				stack.pushMatrix();
			}

			@Override
			public void pop() {
				stack.popMatrix();
			}

			@Override
			public void translate(double x, double y, double z) {
				this.translate(x, y);
			}

			@Override
			public void translate(double x, double y) {
				stack.translate((float) x, (float) y);
			}
		};
	}
	@Deprecated
	default void pushPose() {
		push();
	}
	@Deprecated
	default void popPose() {
		pop();
	}

	void push();
	void pop();

	void translate(double x, double y, double z);
	void translate(double x, double y);
}
