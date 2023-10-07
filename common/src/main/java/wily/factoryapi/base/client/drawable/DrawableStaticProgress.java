package wily.factoryapi.base.client.drawable;

import com.mojang.blaze3d.vertex.PoseStack;
import wily.factoryapi.base.Progress;

public class DrawableStaticProgress extends AbstractDrawableStatic<DrawableStaticProgress,IFactoryDrawableType.DrawableProgress>{

    public DrawableStaticProgress(DrawableProgress drawable, int posX, int posY) {
        super(drawable, posX, posY);
    }
    public void drawProgress(PoseStack poseStack, float percentage){
        drawable.drawProgress(poseStack,getX(),getY(), percentage);
    }
    public void drawProgress(PoseStack poseStack, int progress, int max){
        drawable.drawProgress(poseStack,getX(),getY(), progress, max);
    }
    public void drawProgress(PoseStack poseStack, Progress progress){
        drawable.drawProgress(poseStack,getX(),getY(),progress);
    }
}
