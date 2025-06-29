package wily.factoryapi.base.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

public interface FactoryGuiMatrixStack {
	public static FactoryGuiMatrixStack of(PoseStack stack) {
		return new FactoryGuiMatrixStack() {
			@Override
			public void push() {
				stack.pushPose();
			}

			@Override
			public void pop() {
				stack.popPose();
			}

			@Override
			public void translate(double x, double y, double z) {
				stack.translate(x, y, z);
			}

			@Override
			public void translate(double x, double y) {
				this.translate(x, y, 0);
			}

			@Override
			public void scale(double x, double y, double z) {
				stack.scale((float) x, (float) y, (float) z);
			}

			@Override
			public void scale(double x, double y) {
				this.scale(x, y, 0);
			}

			@Override
			public <T> T getNative() {
				return (T) stack;
			}
		};
	}

	static FactoryGuiMatrixStack of(GuiGraphics graphics) {
		return new FactoryGuiMatrixStack() {
			private FactoryGuiMatrixStack delegate = of(graphics.pose());
			@Override
			public void push() {
				delegate.push();
			}

			@Override
			public void pop() {
				delegate.pop();
			}

			@Override
			public void translate(double x, double y, double z) {
				delegate.translate(x, y, z);
			}

			@Override
			public void translate(double x, double y) {
				delegate.translate(x, y);
			}

			@Override
			public void scale(double x, double y, double z) {
				delegate.scale(x, y, z);
			}

			@Override
			public void scale(double x, double y) {
				delegate.scale(x, y);
			}

			@Override
			public <T> T getNative() {
				return (T) graphics;
			}
		};
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

			@Override
			public void scale(double x, double y, double z) {
				this.scale(x, y);
			}

			@Override
			public void scale(double x, double y) {
				stack.scale((float) x, (float) y);
			}

			@Override
			public <T> T getNative() {
				return (T) stack;
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
	void scale(double x, double y, double z);
	void scale(double x, double y);
	<T> T getNative();
}
