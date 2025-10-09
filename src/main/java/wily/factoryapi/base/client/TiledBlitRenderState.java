//? if >=1.21.6 && <1.21.9 {
/*package wily.factoryapi.base.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

// Backport from 1.21.9
public record TiledBlitRenderState(
	RenderPipeline pipeline,
	TextureSetup textureSetup,
	Matrix3x2f pose,
	int tileWidth,
	int tileHeight,
	int x0,
	int y0,
	int x1,
	int y1,
	float u0,
	float u1,
	float v0,
	float v1,
	int color,
	@Nullable ScreenRectangle scissorArea,
	@Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
	public TiledBlitRenderState(
		RenderPipeline renderPipeline,
		TextureSetup textureSetup,
		Matrix3x2f matrix3x2f,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		float f,
		float g,
		float h,
		float o,
		int p,
		@Nullable ScreenRectangle screenRectangle
	) {
		this(renderPipeline, textureSetup, matrix3x2f, i, j, k, l, m, n, f, g, h, o, p, screenRectangle, getBounds(k, l, m, n, matrix3x2f, screenRectangle));
	}

	@Override
	public void buildVertices(VertexConsumer vertexConsumer, float level) {
		int i = this.x1() - this.x0();
		int j = this.y1() - this.y0();

		for (int k = 0; k < i; k += this.tileWidth()) {
			int l = i - k;
			int m;
			float f;
			if (this.tileWidth() <= l) {
				m = this.tileWidth();
				f = this.u1();
			} else {
				m = l;
				f = Mth.lerp((float)l / this.tileWidth(), this.u0(), this.u1());
			}

			for (int n = 0; n < j; n += this.tileHeight()) {
				int o = j - n;
				int p;
				float g;
				if (this.tileHeight() <= o) {
					p = this.tileHeight();
					g = this.v1();
				} else {
					p = o;
					g = Mth.lerp((float)o / this.tileHeight(), this.v0(), this.v1());
				}

				int q = this.x0() + k;
				int r = this.x0() + k + m;
				int s = this.y0() + n;
				int t = this.y0() + n + p;
				vertexConsumer.addVertexWith2DPose(this.pose(), q, s, level).setUv(this.u0(), this.v0()).setColor(this.color());
				vertexConsumer.addVertexWith2DPose(this.pose(), q, t, level).setUv(this.u0(), g).setColor(this.color());
				vertexConsumer.addVertexWith2DPose(this.pose(), r, t, level).setUv(f, g).setColor(this.color());
				vertexConsumer.addVertexWith2DPose(this.pose(), r, s, level).setUv(f, this.v0()).setColor(this.color());
			}
		}
	}

	@Nullable
	private static ScreenRectangle getBounds(int i, int j, int k, int l, Matrix3x2f matrix3x2f, @Nullable ScreenRectangle screenRectangle) {
		ScreenRectangle screenRectangle2 = new ScreenRectangle(i, j, k - i, l - j).transformMaxBounds(matrix3x2f);
		return screenRectangle != null ? screenRectangle.intersection(screenRectangle2) : screenRectangle2;
	}
}
*///?}
